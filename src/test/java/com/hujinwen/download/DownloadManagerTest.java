package com.hujinwen.download;

import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DownloadManagerTest {
    private static final Logger logger = LogManager.getLogger(DownloadManager.class);

    @Test
    void test() throws IOException, InterruptedException {
        String url = "http://forspeed.onlinedown.net/down/newdown/2/17/Warcraft3_1.24E.rar";
        String localPath = "/home/joe/Desktop";
        String localName = "";

        final DownloadWorker worker = DownloadManager.download(new DownloadSeed(url, localPath, localName), false);
        while (!worker.isFinish()) {
            logger.debug("progress -> {}%, speed -> {}kb/s", worker.getProgress() * 100, worker.getSpeed());
            Thread.sleep(1000);
        }

    }
}