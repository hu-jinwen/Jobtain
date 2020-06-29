package com.hujinwen.download.entity.seeds;

import java.util.Map;

/**
 * Created by joe on 2020/6/29
 */
public class HttpDownloadSeed extends DownloadSeed {

    private Map<String, String> headers;

    private Map<String, String> cookies;

    public HttpDownloadSeed(String url, String localPath, String localName) {
        super(url, localPath, localName);
    }

    public HttpDownloadSeed(String url, String localPath, String localName, boolean subTaskSeed) {
        super(url, localPath, localName, subTaskSeed);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }
}
