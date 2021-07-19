package com.hujinwen.download;

import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.entity.seeds.HttpDownloadSeed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

        final DownloadWorker worker = DownloadManager.download(downloadSeed);
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

    /**
     * 东京食尸鬼下载
     */
    private static void tokyoCCDownload() throws IOException {
        String[] season3 = {
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/ce2b1b805285890811754198989/09KCxLVLiDwA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e01504c5285890811735655171/gSSOaDUneA0A.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/cfd8e2a45285890811754200194/ePu6evMjuXoA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e01e8f45285890811735656439/GCsBpZidokEA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e026cec5285890811735657241/P4vbqz4f8bQA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e02f1345285890811735658077/yUDNXo3NQNUA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e0312b75285890811735658836/12GndM3581cA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/4e03a3365285890811735659924/RBn1fvxNYToA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/219252ca5285890811755415416/WMFvgw11RFsA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/2192ce655285890811755416032/nsBC1B8aLAMA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/a20b82f85285890811736962388/IgOsnETMuEsA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/2193e7905285890811755418068/gwo9uASWEtMA.mp4",
        };
        String[] season4 = {
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/a20f6ec25285890811736969877/DodGTaxIO1IA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/a21c86b45285890811736970466/rKTQN7cDjTMA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/73b608575285890811756686067/fMtvp9IHNcoA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/73b629b55285890811756686812/oYhLHETdP0QA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/26effe6c5285890811738053284/WTfJKyM2wxwA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/26f081a65285890811738054034/ier2Q64VH2QA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/73b7be575285890811756689451/xzbgFwq6iPkA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/26f2c4d65285890811738058487/epXtcWqEUisA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/26f3486e5285890811738059262/WY73AvyIAMgA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/c5234ddf5285890811757854689/allq6dADfUYA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/2701028d5285890811738061394/vnAAzMyHeicA.mp4",
                "https://1251316161.vod2.myqcloud.com/007a649dvodcq1251316161/762cbee85285890811739142280/Dv7GmTOrY40A.mp4",
        };

        String localPath = "/Users/hujinwen/Downloads/东京喰种第四季";

        for (int i = 0; i < season4.length; i++) {
            final String url = season4[i];
            final HttpDownloadSeed downloadSeed = new HttpDownloadSeed(url, localPath, i + ".mp4");
            final DownloadWorker worker = DownloadManager.download(downloadSeed);
            System.out.println("Start new download");
        }

    }


    public static void main(String[] args) throws IOException {
        tokyoCCDownload();
    }

}