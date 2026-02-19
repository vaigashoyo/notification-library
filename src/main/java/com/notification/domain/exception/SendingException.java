package com.notification.domain.exception;

public final class SendingException extends NotificationException {

    public SendingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SendingException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }

    public SendingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
