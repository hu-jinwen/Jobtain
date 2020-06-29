package com.hujinwen.download.entity.seeds;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 2019/2/19
 */
public class M3u8DownloadSeed extends HttpDownloadSeed {

    public final List<String> eggs = new ArrayList<>();

    public M3u8DownloadSeed(DownloadSeed seed) {
        super(seed.getUrl(), seed.getLocalPath(), seed.getLocalName());
    }

    public M3u8DownloadSeed(String url, String localPath, String localName) {
        super(url, localPath, localName);
    }

    public M3u8DownloadSeed(String url, String localPath, String localName, boolean subTaskSeed) {
        super(url, localPath, localName, subTaskSeed);
    }
}
