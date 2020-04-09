package com.hujinwen.download.core;

import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.download.entity.seeds.FtpDownloadSeed;
import com.hujinwen.download.entity.seeds.M3u8DownloadSeed;
import com.hujinwen.download.workers.Aria2cWorker;
import com.hujinwen.download.workers.FtpWorker;
import com.hujinwen.download.workers.HttpWorker;
import com.hujinwen.download.workers.M3u8Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Create by joe on 2019/1/28
 */
public abstract class WorkerFactory {
    private static final Logger logger = LoggerFactory.getLogger(WorkerFactory.class);

    public DownloadWorker getWorker(DownloadSeed seed) throws IOException {
        DownloadWorker worker = findWorker(seed);
        workerHandle(worker);
        worker.init();
        return worker;
    }

    public static WorkerFactory getDefaultFactory() {
        return new WorkerFactory() {
            @Override
            public void workerHandle(DownloadWorker worker) {
            }
        };
    }

    /**
     * 查找可以下载的 worker
     */
    private DownloadWorker findWorker(DownloadSeed seed) {
        DownloadWorker worker = null;
        String url = seed.getUrl();
        try {
            if (seed instanceof M3u8DownloadSeed || url.endsWith(".m3u8") || url.contains(".m3u8?")) {
                worker = new M3u8Worker(seed);
            } else if (url.startsWith("http")) {
                worker = new HttpWorker(seed);
            } else if (url.startsWith("magnet:?")) {
                worker = new Aria2cWorker(seed);
            } else if (url.startsWith("ftp://")) {
                worker = new FtpWorker(new FtpDownloadSeed(seed));
            } else {
                logger.warn("Can not find useful downloadWorker, Please check the download url -> " + url);
            }
        } catch (IOException e) {
            logger.error("Download worker initialize failed! url -> " + url, e);
        }
        return worker;
    }

    public abstract void workerHandle(DownloadWorker worker);

}
