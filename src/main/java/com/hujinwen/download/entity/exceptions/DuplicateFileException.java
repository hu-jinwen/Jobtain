package com.hujinwen.download.entity.exceptions;

/**
 * Create by hjw on 2019/2/28
 */
public class DuplicateFileException extends Exception {
    public DuplicateFileException() {
    }

    public DuplicateFileException(String message) {
        super(message);
    }

    public DuplicateFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateFileException(Throwable cause) {
        super(cause);
    }

    public DuplicateFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
