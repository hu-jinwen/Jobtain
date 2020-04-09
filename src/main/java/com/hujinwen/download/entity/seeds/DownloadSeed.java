package com.hujinwen.download.entity.seeds;

import com.hujinwen.download.core.DownloadInit;
import com.hujinwen.download.entity.exceptions.DuplicateFileException;
import com.hujinwen.utils.FileUtils;
import com.hujinwen.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by joe on 2019/2/1
 */
public class DownloadSeed {
    private final MessageFormat NAME_FORMAT = new MessageFormat("{0}({1}).{2}");

    /**
     * 下载链接
     */
    protected String url;

    /**
     * 本地路径
     */
    protected String localPath;

    /**
     * 本地文件名
     */
    protected String localName;

    /**
     * 是否是sub task
     */
    private boolean subTaskSeed;

    /**
     * params
     */
    private Map<String, Object> params;


    public DownloadSeed(String url, String localPath, String localName) {
        this(url, null, localPath, localName, false);
    }

    public DownloadSeed(String url, String localPath, String localName, boolean subTaskSeed) {
        this(url, null, localPath, localName, subTaskSeed);
    }

    public DownloadSeed(String url, Map<String, Object> params, String localPath, String localName) {
        this(url, params, localPath, localName, false);
    }

    public DownloadSeed(String url, Map<String, Object> params, String localPath, String localName, boolean subTaskSeed) {
        try {
            this.subTaskSeed = subTaskSeed;
            this.params = params;
            this.url = url;
            this.localPath = FileUtils.checkAndMkdirs(localPath).getPath();
            this.localName = makeFilename(localName);
        } catch (DuplicateFileException e) {
            throw new RuntimeException("Download seed initialize failed! -> " + e.getMessage());
        }
    }

    /**
     * 获取文件名
     */
    private String makeFilename(String localName) throws DuplicateFileException {
        String filename = StringUtils.isBlank(localName) ? UrlUtils.extraFilename(url) : localName;

        if (subTaskSeed || !new File(localPath + "/" + filename).exists() || !DownloadInit.AUTO_RENAME) {
            return filename;
        }

        int fileNo = 0;
        String name = filename;
        String suffix = "";
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            suffix = filename.substring(index + 1);
            name = filename.substring(0, index);
        }
        while (new File(localPath + "/" + filename).exists() &&
                !new File(localPath + "/" + filename + ".temp").exists()) {
            filename = NAME_FORMAT.format(new Object[]{name, fileNo, suffix});
            fileNo++;
        }
        return filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
