package com.notification.domain.exception;

public abstract sealed class NotificationException extends RuntimeException
        permits ValidationException, SendingException, ConfigurationException {

    private final ErrorCode errorCode;

    protected NotificationException(ErrorCode errorCode) {
        super(errorCode.toString());
        this.errorCode = errorCode;
    }

    protected NotificationException(ErrorCode errorCode, String detail) {
        super("[%s] %s - %s".formatted(errorCode.getCode(), errorCode.getDescription(), detail));
        this.errorCode = errorCode;
    }

    protected NotificationException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.toString(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
