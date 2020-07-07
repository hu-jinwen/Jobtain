package com.hujinwen.download.core;


import com.hujinwen.download.entity.DownloadInfo;
import com.hujinwen.download.entity.exceptions.TooManyRequestsException;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.download.workers.M3u8Worker;
import com.hujinwen.utils.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * Create by joe on 2019/1/25
 */
public abstract class DownloadWorker extends Thread {
    private static final Logger logger = LogManager.getLogger(DownloadWorker.class);

    protected static final MessageFormat RETRY_INFO_TEMP = new MessageFormat("Retrying... err_count -> {0} url -> {1}\t{2}");

    /**
     * 多线程下载，下载同步
     */
    protected CountDownLatch masterCountDownLatch;

    /**
     * 多线程下载，下载同步
     */
    protected CountDownLatch slaveCountDownLatch;

    /**
     * 线程同步信号量
     */
    protected Semaphore semaphore;

    /**
     * 种子
     */
    protected final DownloadSeed seed;

    /**
     * 下载进度信息
     */
    protected final DownloadInfo info;

    /**
     * 内部任务
     */
    protected final List<DownloadWorker> internalTaskList = new ArrayList<>();

    /**
     * 子任务（分段下载用到）
     */
    protected final List<DownloadWorker> subTaskList = new ArrayList<>();

    /**
     * 子任务的父线程
     */
    protected DownloadWorker parentWorker;

    /**
     * 是否是子任务
     */
    protected boolean isSubTask;

    /**
     * 下载是否已失败
     */
    private boolean downloadFailed;

    public DownloadWorker(DownloadSeed seed, boolean isSubTask) throws IOException {
        this.seed = seed;
        this.info = new DownloadInfo(new File(seed.getLocalPath() + "/" + seed.getLocalName() + ".temp"));
        this.isSubTask = isSubTask;
    }

    /**
     * 初始化
     */
    protected abstract void init() throws IOException;

    protected abstract void download() throws IOException, InterruptedException, TooManyRequestsException, URISyntaxException;

    @Override
    public void run() {
        info.startTime = System.currentTimeMillis();
        int err_count = 0;
        while (err_count++ < DownloadInit.RETRY_TIMES) {
            try {
                final File tempFile = new File(seed.getLocalPath() + "/" + seed.getLocalName() + ".temp");
                // 处理先下载完成的子任务重复下载的问题
                if (!tempFile.exists() && new File(seed.getLocalPath() + "/" + seed.getLocalName()).exists()) {
                    info.downloadSum = info.downloadEnd - info.downloadStart + 1;
                    break;
                }
                RandomAccessFile outputTemp = new RandomAccessFile(tempFile, "rw");
                info.saveTemp(outputTemp);
                outputTemp.close();

                if (this.isSubTask && semaphore != null) {
                    semaphore.acquire();
                }
                download();
                break;
            } catch (TooManyRequestsException e) {
                // 抛出此类异常，线程等待重试
                logger.info(e.getMessage() + " -> Waiting for retry!");
                // 如果所有子线程都为失败等待状态，那么该下载也没必要继续进行
                if (semaphore != null && semaphore.getQueueLength() == info.threadNum - 1) {
                    logger.error("+> Download failed! All the tasks has failed!");
                    if (parentWorker != null) {
                        parentWorker.interruptWorker();
                    }
                }
            } catch (SocketTimeoutException /*| ConnectTimeoutException*/ e) {  // FIXME 这里应抛出此异常
                // 抛出此类异常，线程立即重试
                logger.info(RETRY_INFO_TEMP.format(new Object[]{err_count, seed.getUrl(), e.getMessage()}));
                if (semaphore != null) {
                    semaphore.release();
                }
                // TODO 超过最大重试应该终止父线程和其所有子线程，以节省资源
            } catch (Exception e) {
                logger.error("+> Download failed! url - > " + seed.getUrl(), e);
                if (parentWorker != null) {
                    parentWorker.interruptWorker();
                }
                downloadFailed = true;
                break;
            }
        }
        afterDownloading();
    }

    /**
     * 下载字节流流到指定位置
     */
    protected void downloadFromInputStream(InputStream inputStream, RandomAccessFile outputFile, String mode) throws IOException {
        File tempFile = new File(seed.getLocalPath() + "/" + seed.getLocalName() + ".temp");
        try (
                final RandomAccessFile outputTemp = new RandomAccessFile(tempFile, "rw")
        ) {
            byte[] buffer = new byte[1024];
            int read;
            if ("skip".equals(mode)) {
                skip(inputStream, info.position);
            }
            outputFile.seek(this instanceof M3u8Worker ? info.downloadSum + info.position : info.position);

            while ((read = inputStream.read(buffer)) != -1) {
                outputFile.write(buffer, 0, read);
                info.position += read;
                info.downloadSum += read;
                info.saveTemp(outputTemp);
            }
            info.position = 0L;
        }
    }

    /**
     * inputStream跳过指定字节
     */
    private void skip(InputStream inputStream, long len) throws IOException {
        long skip;
        while ((skip = inputStream.skip(len)) < len) {
            len -= skip;
        }
    }


    /**
     * 下载完成后执行的任务
     */
    private void afterDownloading() {
        // 非子任务 且（不是m3u8下载 或 有子任务的的m3u8下载）
        if (!isSubTask && (!(this instanceof M3u8Worker) || !ObjectUtils.isEmpty(((M3u8Worker) this).subTaskList))) {
            if (!ObjectUtils.isEmpty(subTaskList)) {
                long downloadedCount = 0;
                for (DownloadWorker worker : subTaskList) {
                    downloadedCount += worker.info.downloadSum;
                }
                info.downloadSum = downloadedCount;
                if (info.fileSize == 0) {
                    for (DownloadWorker worker : subTaskList) {
                        info.fileSize += worker.info.fileSize;
                    }
                }
            }

            if (info.downloadSum != info.fileSize) {
                logger.error("Download failed! The downloaded length does't equals the total length!\turl -> {}\tpath -> {}/{}", seed.getUrl(), seed.getLocalPath(), seed.getLocalName());
//                downloadFailed = true;
                return;
            }
            logger.info("Download completed! url -> " + seed.getUrl());
            if (info.threadNum > 1) {
                mergeFile();
            }
        }

        FileUtils.deleteFile(seed.getLocalPath() + "/" + seed.getLocalName() + ".temp");

        if (semaphore != null) {
            semaphore.release();
        }
        if (slaveCountDownLatch != null) {
            slaveCountDownLatch.countDown();
        }
    }


    /**
     * 下载完成合并文件
     */
    private void mergeFile() {
        logger.info("File merging... -> {}/{}", seed.getLocalPath(), seed.getLocalName());
        if (info.threadNum <= 1 || ObjectUtils.isEmpty(subTaskList)) {
            return;
        }

        File file = new File(seed.getLocalPath() + "/" + seed.getLocalName());
        try (
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                final BufferedOutputStream outputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            for (int i = 0; i < info.threadNum; i++) {
                File tempFile = new File(file.getPath() + "_" + i);
                FileInputStream inputStream = new FileInputStream(tempFile);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                inputStream.close();
                FileUtils.deleteFile(tempFile);
            }
            outputStream.flush();
        } catch (IOException ioe) {
            logger.error("+++++>> File merge failed! file -> {}", file.getPath(), ioe);
        }
    }

    /**
     * 终止自身和所有子任务的阻塞状态
     */
    protected void interruptWorker() {
        for (DownloadWorker worker : subTaskList) {
            worker.interrupt();
        }
        this.interrupt();
    }

    /**
     * 获取下载进度
     */
    public double getProgress() {
        if (info.downloadSum == 0L || info.fileSize == 0L || info.downloadSum > info.fileSize) {
            return 0.0;
        }
        return (int) (((double) info.downloadSum / info.fileSize) * 100) / 100.0;
    }

    /**
     * 获取下载速度
     */
    public double getSpeed() {
        return (int) (((info.downloadSum / 1024.0) / ((System.currentTimeMillis() - info.startTime) / 1000.0)) * 100) / 100.0;
    }

    /**
     * 下载是否完成
     */
    public boolean isFinish() {
        return !this.isAlive();
    }


    /**
     * 下载是否已经失败
     */
    public boolean isDownloadFailed() {
        return downloadFailed;
    }

    public void setSlaveCountDownLatch(CountDownLatch slaveCountDownLatch) {
        this.slaveCountDownLatch = slaveCountDownLatch;
    }

    public String getUrl() {
        return seed.getUrl();
    }

    public void setThreadNum(int threadNum) {
        info.threadNum = threadNum;
    }
}
