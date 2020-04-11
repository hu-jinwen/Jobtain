package com.hujinwen.download.workers;

import com.hujinwen.client.HttpClient;
import com.hujinwen.download.core.DownloadInit;
import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.download.entity.seeds.M3u8DownloadSeed;
import com.hujinwen.utils.UrlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Created by joe on 2019/2/19
 * <p>
 * m3u8 download worker
 */
public class M3u8Worker extends DownloadWorker {
    private static final Logger logger = LogManager.getLogger(M3u8Worker.class);

    public M3u8Worker(DownloadSeed seed) throws IOException {
        super(seed instanceof M3u8DownloadSeed ? seed : new M3u8DownloadSeed(seed), false);
    }

    public M3u8Worker(DownloadSeed seed, CountDownLatch countDownLatch, Semaphore semaphore, boolean isSubTask) throws IOException {
        super(seed, isSubTask);
        this.slaveCountDownLatch = countDownLatch;
        this.semaphore = semaphore;
    }

    /**
     * m3u8初始化
     */
    @Override
    protected void init() throws IOException {
        if (isSubTask) {
            return;
        }
        M3u8DownloadSeed seed = (M3u8DownloadSeed) this.seed;
        final HttpClient httpClient = HttpClient.createDefault();

        String content = httpClient.doGetAsStr(seed.getUrl());
        String redirectUrl = httpClient.getLastRedirectUrl();
        if (!StringUtils.isBlank(redirectUrl)) seed.setUrl(redirectUrl);
        if (StringUtils.isBlank(content)) {
            throw new RuntimeException("M3u8 download worker initialize failed! the response content is empty.");
        }

        if (!content.startsWith("#EXTM3U")) {
            throw new RuntimeException("m3u8 content format error!");
        }

        String[] lines = content.split("\n");

        if (content.contains("#EXT-X-STREAM-INF")) {
            String bandWidth = "";
            for (String line : lines) {
                if (line.endsWith(".m3u8")) {
                    String subUrl = UrlUtils.absUrl(seed.getUrl(), line);
                    String localPath = seed.getLocalPath();
                    String localName = seed.getLocalName();
                    String subName;
                    int index = localName.lastIndexOf(".");
                    if (index > 0) {
                        subName = localName.substring(0, index) + "_" + bandWidth + localName.substring(index);
                    } else {
                        subName = seed.getLocalName() + "_" + bandWidth;
                    }
                    M3u8Worker m3u8Worker = new M3u8Worker(new M3u8DownloadSeed(subUrl, localPath, subName));
                    m3u8Worker.parentWorker = this;
                    m3u8Worker.init();
                    internalTaskList.add(m3u8Worker);
                } else if (line.startsWith("#EXT-X-STREAM-INF:")) {
                    int index = line.indexOf("BANDWIDTH=");
                    int index1 = line.indexOf(",", index);
                    int endIndex = index1 == -1 ? line.length() : index1;
                    bandWidth = line.substring(index + 10, endIndex);
                }
            }
            if (!ObjectUtils.isEmpty(internalTaskList)) {
                masterCountDownLatch = new CountDownLatch(internalTaskList.size());
                for (DownloadWorker worker : internalTaskList) {
                    worker.setSlaveCountDownLatch(masterCountDownLatch);
                }
            }
        } else {
            List<String> seedEggs = seed.eggs;
            for (String line : lines) {
                if (line.contains(".ts")) {
                    seedEggs.add(UrlUtils.absUrl(seed.getUrl(), line));
                }
            }
            if (!isSubTask && info.threadNum > 1) {
                // 任务分发
                int[] aveResults = taskSchedule(seedEggs.size(), info.threadNum);

                masterCountDownLatch = new CountDownLatch(info.threadNum);
                semaphore = new Semaphore(info.threadNum);
                for (int i = 0; i < info.threadNum; i++) {
                    String subName = seed.getLocalName() + "_" + i;
                    M3u8DownloadSeed subSeed = new M3u8DownloadSeed(null, seed.getLocalPath(), subName, true);
                    for (int j = 0; j < aveResults[i]; j++) {
                        subSeed.eggs.add(seedEggs.remove(0));
                    }
                    M3u8Worker worker = new M3u8Worker(subSeed, masterCountDownLatch, semaphore, true);
                    worker.parentWorker = this;
                    subTaskList.add(worker);
                }
            }
        }
    }

    /**
     * 任务调度，计算每个线程分多少个任务
     */
    private static int[] taskSchedule(int taskCount, int threadNum) {
        int[] result = new int[threadNum];

        int ave = taskCount / threadNum;
        for (int i = 0; i < threadNum; i++) {
            result[i] = ave;
        }
        int remainder = taskCount % threadNum;
        for (int i = 0; i < remainder; i++) {
            result[i]++;
        }
        return result;
    }


    @Override
    public void run() {
        for (DownloadWorker worker : subTaskList) {
            worker.start();
        }
        for (DownloadWorker worker : internalTaskList) {
            worker.start();
        }
        super.run();
    }

    @Override
    protected void download() throws IOException, InterruptedException {
        if (!ObjectUtils.isEmpty(subTaskList)) {
            masterCountDownLatch.await();
            return;
        }
        if (!ObjectUtils.isEmpty(internalTaskList)) {
            masterCountDownLatch.await();
            return;
        }
        M3u8DownloadSeed seed = (M3u8DownloadSeed) this.seed;
        List<String> eggs = seed.eggs;
        if (ObjectUtils.isEmpty(eggs)) {
            return;
        }

        RandomAccessFile outputFile = new RandomAccessFile(seed.getLocalPath() + "/" + seed.getLocalName(), "rw");
        for (int i = info.eggIndex; i < eggs.size(); i++) {
            String egg = eggs.get(i);
            info.eggIndex = i;
            int err_count = 0;
            while (err_count < DownloadInit.RETRY_TIMES) {
                try (
                        final HttpClient httpClient = HttpClient.createDefault()
                ) {
                    InputStream inputStream = httpClient.doGetAsStream(egg);
                    if (inputStream == null) {
                        throw new SocketException("Connection failed!");
                    }
                    downloadFromInputStream(inputStream, outputFile, "skip");
                    info.fileSize += httpClient.getContentLength();
                    break;
                } catch (SocketTimeoutException /*| URISyntaxException*/ | SocketException e) {
                    err_count++;
                    logger.info(RETRY_INFO_TEMP.format(new Object[]{err_count, egg, e.getMessage()}));
                }
            }
            if (err_count == DownloadInit.RETRY_TIMES) {
                throw new RuntimeException("+++++>> Download failed! The maximum number of retries was exceeded. -> " + egg);
            }
        }
        outputFile.close();
    }


    @Override
    public double getProgress() {
        List<DownloadWorker> taskList = !ObjectUtils.isEmpty(subTaskList) ? subTaskList :
                !ObjectUtils.isEmpty(internalTaskList) ? internalTaskList : null;
        if (taskList != null) {
            double progressCount = 0.0;
            for (DownloadWorker worker : taskList) {
                // double计算会丢失精度
                progressCount += worker.getProgress();
            }
            return (int) (progressCount / taskList.size() * 100) / 100.0;
        }
        return (int) ((double) info.eggIndex / ((M3u8DownloadSeed) seed).eggs.size() * 100) / 100.0;
    }

    @Override
    public double getSpeed() {
        List<DownloadWorker> taskList = !ObjectUtils.isEmpty(subTaskList) ? subTaskList :
                !ObjectUtils.isEmpty(internalTaskList) ? internalTaskList : null;

        if (taskList != null) {
            double speedCount = 0.0;
            for (DownloadWorker worker : taskList) {
                // double计算会丢失精度
                speedCount += worker.getSpeed();
            }
            return (int) (speedCount * 100) / 100.0;
        }
        return super.getSpeed();
    }

    @Override
    public boolean isFinish() {
        for (DownloadWorker worker : internalTaskList) {
            if (!worker.isFinish()) {
                return false;
            }
        }
        for (DownloadWorker worker : subTaskList) {
            if (!worker.isFinish()) {
                return false;
            }
        }
        return super.isFinish();
    }

}

