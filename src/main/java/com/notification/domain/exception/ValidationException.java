package com.notification.domain.exception;

public final class ValidationException extends NotificationException {

    public ValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ValidationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
