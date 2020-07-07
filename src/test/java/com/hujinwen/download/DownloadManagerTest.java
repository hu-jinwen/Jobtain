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
        //设置https协议访问
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,SSLv3");

        Map<String, String> headers = new HashMap<String, String>() {{
            put("Accept", "*/*");
            put("Origin", "https://www.bilibili.com");
            put("Accept-Language", "en-us");
            put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Safari/605.1.15");
            put("Referer", "https://www.bilibili.com/");
            put("Accept-Encoding", "identity");
            put("Connection", "keep-alive");
        }};

        String url = "https://43-250-171-203.dhost.00cdn.com:9305/upos-sz-mirrorkodo.bilivideo.com/upgcxcode/57/24/175142457/175142457_da2-1-30112.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M=&uipk=5&nbs=1&deadline=1593428844&gen=playurl&os=kodobv&oi=460522550&trid=b2a6778f6e3f4b6193f8124fc9260feap&platform=pc&upsig=b0aaa3e214a1985cb101c55970de1f11&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,platform&mid=487941522&orderid=0,3&logo=80000000&xyip=43.250.171.203&xyct=5";
        String localPath = "/Users/hujinwen/Desktop/Download";
        String localName = "霍比特人1.mp4";

        final HttpDownloadSeed downloadSeed = new HttpDownloadSeed(url, localPath, localName);
        downloadSeed.setHeaders(headers);

        final DownloadWorker worker = DownloadManager.download(downloadSeed, false);
        while (!worker.isFinish()) {
            logger.debug("progress -> {}%, speed -> {}kb/s", worker.getProgress() * 100, worker.getSpeed());
            Thread.sleep(1000);
        }

    }

    @Test
    void war3Download() throws IOException, InterruptedException {
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