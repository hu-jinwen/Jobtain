package com.hujinwen.download.entity;

import com.hujinwen.download.core.DownloadInit;
import com.hujinwen.utils.ArrayUtils;
import com.hujinwen.utils.ConvertUtils;

import java.io.*;

/**
 * Created by joe on 2019/2/20
 */
public class DownloadInfo {

    /**
     * 任务下载线程数
     */
    public int threadNum;

    /**
     * 下载开始位置
     */
    public long downloadStart;

    /**
     * 下载结束位置
     */
    public long downloadEnd;

    /**
     * 下载文件的大小
     */
    public long fileSize;

    /**
     * 下载开始时间
     */
    public long startTime;

    /**
     * ts任务下标（m3u8下载）
     */
    public int eggIndex;

    /**
     * 单次任务下载进度
     */
    public long position;

    /**
     * 已下载文件总和
     */
    public long downloadSum;

    public DownloadInfo(File file) throws IOException {
        threadNum = DownloadInit.THREAD_NUM;

        if (file.exists()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    final DataInputStream dataInputStream = new DataInputStream(fileInputStream)
            ) {
                threadNum = dataInputStream.readInt();
                downloadStart = dataInputStream.readLong();
                downloadEnd = dataInputStream.readLong();
                eggIndex = dataInputStream.readInt();
                position = dataInputStream.readLong();
                downloadSum = dataInputStream.readLong();
                fileSize = dataInputStream.readLong();
            }
        }
    }

    /**
     * 保存缓存
     */
    public void saveTemp(RandomAccessFile outputTemp) throws IOException {
        // byte合并一次性写入效率高10倍
        byte[] result = ArrayUtils.union(
                ConvertUtils.intToBytes(threadNum),
                ConvertUtils.longToBytes(downloadStart),
                ConvertUtils.longToBytes(downloadEnd),
                ConvertUtils.intToBytes(eggIndex),
                ConvertUtils.longToBytes(position),
                ConvertUtils.longToBytes(downloadSum),
                ConvertUtils.longToBytes(fileSize)
        );
        outputTemp.seek(0);
        outputTemp.write(result);
    }

}
