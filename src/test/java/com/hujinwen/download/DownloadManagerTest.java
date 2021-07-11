package com.hujinwen.download;

import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.HttpDownloadSeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class DownloadManagerTest {
    private static final Logger logger = LogManager.getLogger(DownloadManager.class);

    @Test
    void test() throws IOException, InterruptedException {

//        Map<String, String> headers = new HashMap<String, String>() {{
//            put("Accept", "*/*");
//            put("Origin", "https://www.bilibili.com");
//            put("Accept-Language", "en-us");
//            put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Safari/605.1.15");
//            put("Referer", "https://www.bilibili.com/");
//            put("Accept-Encoding", "identity");
//            put("Connection", "keep-alive");
//        }};

        String url = "https://vod5.wenshibaowenbei.com/20210203/x8SnUGqP/1000kb/hls/index.m3u8";
        String localPath = "/Users/hujinwen/Downloads/";
        String localName = "7.mp4";

        final HttpDownloadSeed downloadSeed = new HttpDownloadSeed(url, localPath, localName);
//        downloadSeed.setHeaders(headers);

        final DownloadWorker worker = DownloadManager.download(downloadSeed, false);
        while (!worker.isFinish()) {
            logger.debug("progress -> {}%, speed -> {}kb/s", worker.getProgress() * 100, worker.getSpeed());
            Thread.sleep(1000);
        }
    }

    @Test
    void qqDownload() throws IOException, InterruptedException {
        String url = "http://dldir1.qq.com/qqfile/QQforMac/QQ_6.6.7.dmg";
        String localPath = "/Users/hujinwen/Desktop/Download";
        String localName = "";

        final HttpDownloadSeed downloadSeed = new HttpDownloadSeed(url, localPath, localName);

        final DownloadWorker worker = DownloadManager.download(downloadSeed, false);
        while (!worker.isFinish()) {
            logger.debug("progress -> {}%, speed -> {}kb/s", worker.getProgress() * 100, worker.getSpeed());
            Thread.sleep(1000);
        }
    }

}