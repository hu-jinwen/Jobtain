package com.hujinwen.download;

import com.hujinwen.download.core.DownloadInit;
import com.hujinwen.download.core.DownloadWorker;
import com.hujinwen.download.core.WorkerFactory;
import com.hujinwen.download.entity.seeds.DownloadSeed;
import com.hujinwen.utils.EncryptUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Create by hjw on 2019/1/24
 * TODO ed2k:// 链接下载
 * TODO 注意杀掉aria进程 （重新分析一下是否需要删除）
 * TODO 区分不同操作系统之间的区别
 * TODO 可提供上传种子文件下载
 * TODO RMTP 下载
 * TODO 频繁的saveTemp可以用定时/定量save的方式来解决。
 */
public class DownloadManager {
    private static final WorkerFactory WORKER_FACTORY;

    static {
        DownloadInit.init();
        try {
            WORKER_FACTORY = findFactory();
        } catch (Exception e) {
            throw new RuntimeException("Worker factory initialize failed! " + e.getMessage(), e);
        }
    }

    /**
     * 下载
     *
     * @param downloadSeed 种子
     * @param sync         下载是否同步,true 为同步， false 为异步
     */
    public static DownloadWorker download(DownloadSeed downloadSeed, boolean sync) throws IOException {
        preprocess(downloadSeed);
        DownloadWorker worker = WORKER_FACTORY.getWorker(downloadSeed);
        if (sync) {
            worker.run();
        } else {
            worker.start();
        }
        return worker;
    }

    /**
     * 下载
     *
     * @param url       下载链接
     * @param localPath 本地路径
     * @param localName 本地文件名
     * @param sync      下载是否同步
     */
    public static DownloadWorker download(String url, String localPath, String localName, boolean sync) throws IOException {
        return download(new DownloadSeed(url, localPath, localName), sync);
    }

    /**
     * 下载（默认为异步）
     */
    public static DownloadWorker download(DownloadSeed downloadSeed) throws IOException {
        return download(downloadSeed, false);
    }

    /**
     * 下载（默认为异步）
     */
    public static DownloadWorker download(String url, String localPath, String localName) throws IOException {
        return download(url, localPath, localName, false);
    }

    /**
     * 查找可用的 worker factory
     */
    private static WorkerFactory findFactory() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        WorkerFactory factory;
        if (StringUtils.isBlank(DownloadInit.WORKER_FACTORY)) {
            factory = WorkerFactory.getDefaultFactory();
        } else {
            factory = (WorkerFactory) Class.forName(DownloadInit.WORKER_FACTORY).newInstance();
        }
        return factory;
    }

    /**
     * 种子预处理
     */
    private static void preprocess(DownloadSeed downloadSeed) throws UnsupportedEncodingException {
        String url = downloadSeed.getUrl();
        if (url.startsWith("thunder://")) {
            String base64Str = url.substring(url.indexOf("//") + 2);
            String result = new String(EncryptUtils.base64Decode(base64Str));
            downloadSeed.setUrl(result.substring(2, result.length() - 2));
        }
    }

}
