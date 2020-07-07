package com.hujinwen.download.entity.exceptions;

/**
 * Created by joe on 2020/7/7
 */
public class DownloadWorkerInitException extends RuntimeException {

    public DownloadWorkerInitException() {
        super();
    }

    public DownloadWorkerInitException(String message) {
        super(message);
    }

    public DownloadWorkerInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadWorkerInitException(Throwable cause) {
        super(cause);
    }

    protected DownloadWorkerInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
