package com.hujinwen.download.entity.exceptions;

/**
 * Create by joe on 2019/9/20
 */
public class TooManyRequestsException extends Exception {
    public TooManyRequestsException() {
        super();
    }

    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyRequestsException(Throwable cause) {
        super(cause);
    }

    protected TooManyRequestsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
