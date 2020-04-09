package com.hujinwen.download.workers;

import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Create by joe on 2019/1/25
 * <p>
 * http download worker
 */
public class HttpWorker extends DownloadWorker {
    private static final Logger logger = LoggerFactory.getLogger(HttpWorker.class);

    public HttpWorker(DownloadSeed seed) throws IOException {
        super(seed, false);
    }

    public HttpWorker(DownloadSeed seed, CountDownLatch countDownLatch, Semaphore semaphore, DownloadWorker parentWorker, boolean isSubTask) throws IOException {
        super(seed, isSubTask);
        this.slaveCountDownLatch = countDownLatch;
        this.semaphore = semaphore;
        this.parentWorker = parentWorker;
    }

    /**
     * 下载初始化
     */
    @Override
    protected void init() throws IOException {
        // 子任务不做请求，节约资源
        if (isSubTask) {
            return;
        }
        boolean isSupport = checkAndSetLen(seed.getUrl());

        if (!isSupport) {
            info.threadNum = 1;
        }
        // 多线程分段下载（任务分发）
        if (info.threadNum > 1) {
            long subLen = info.fileSize / info.threadNum;

            masterCountDownLatch = new CountDownLatch(info.threadNum);
            semaphore = new Semaphore(info.threadNum);
            for (int i = 0; i < info.threadNum; i++) {
                String subName = seed.getLocalName() + "_" + i;
                DownloadSeed subSeed = new DownloadSeed(seed.getUrl(), seed.getLocalPath(), subName, true);
                HttpWorker httpWorker = new HttpWorker(subSeed, masterCountDownLatch, semaphore, this, true);
                httpWorker.info.fileSize = info.fileSize;  // 子任务的文件大小，设为和父任务相同
                httpWorker.info.downloadStart = i == 0 ? 0 : (subLen * i) + 1;
                httpWorker.info.downloadEnd = i == info.threadNum - 1 ? info.fileSize - 1 : subLen * (i + 1);

                subTaskList.add(httpWorker);
            }
        }
    }

    @Override
    public void run() {
        for (DownloadWorker worker : subTaskList) {
            worker.start();
        }
        super.run();
    }

    /**
     * 下载
     */
    @Override
    protected void download() throws IOException, InterruptedException, URISyntaxException {
        if (!ObjectUtils.isEmpty(subTaskList)) {
            masterCountDownLatch.await();
            return;
        }

        FetchWeb fetchWeb = createFetchWeb();
        String start = String.valueOf(info.downloadStart + info.downloadSum);
        String end = info.downloadEnd == 0 ? "" : String.valueOf(info.downloadEnd);
        fetchWeb.putHeader("Range", "bytes=" + start + "-" + end);

        InputStream inputStream = fetchWeb.doGetAsStream(seed.getUrl(), null);
        RandomAccessFile outputFile = new RandomAccessFile(seed.getLocalPath() + "/" + seed.getLocalName(), "rw");
        try {
            Map<String, String> respHeaders = fetchWeb.getRespHeaders();
            if (respHeaders.containsKey(HttpHeader.CONTENT_RANGE)) {
                downloadFromInputStream(inputStream, outputFile, null);
            } else {
                downloadFromInputStream(inputStream, outputFile, "skip");
            }
        } finally {
            fetchWeb.close();
            outputFile.close();
        }
    }

    /**
     * 检查是否支持多线程下载，并设置文件长度
     */
    private boolean checkAndSetLen(String url) throws IOException {
        boolean support = false;
        try (
                final FetchWeb fetchWeb = createFetchWeb();
        ) {
            fetchWeb.putHeader("Range", "bytes=1-");
            fetchWeb.doGetAsStream(url, null);
            Map<String, String> respHeaders = fetchWeb.getRespHeaders();
            if (respHeaders.containsKey(HttpHeader.CONTENT_RANGE)) {
                String value = respHeaders.get(HttpHeader.CONTENT_RANGE);
                info.fileSize = Long.parseLong(value.substring(value.lastIndexOf("/") + 1));
                support = true;
            } else {
                info.fileSize = Long.parseLong(respHeaders.get(HttpHeader.CONTENT_LENGTH));
            }
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
        }
        return support;
    }

    @Override
    public double getProgress() {
        if (ObjectUtils.isEmpty(subTaskList)) {
            return super.getProgress();
        }
        double count = 0.0;
        for (DownloadWorker worker : subTaskList) {
            count += worker.getProgress();
        }
        return count;
    }

    @Override
    public double getSpeed() {
        if (ObjectUtils.isEmpty(subTaskList)) {
            return super.getSpeed();
        }
        double count = 0.0;
        for (DownloadWorker worker : subTaskList) {
            count += worker.getSpeed();
        }
        return (int) (count * 100) / 100.0;
    }
}
