package com.ruoyi.web.exception;

public class AutoLogException extends RuntimeException {
    public AutoLogException(String message) {
        super(message);
    }

    public AutoLogException(String message, Throwable cause) {
        super(message, cause);
    }
}
