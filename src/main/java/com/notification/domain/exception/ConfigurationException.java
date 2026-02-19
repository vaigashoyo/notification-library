package com.notification.domain.exception;

public final class ConfigurationException extends NotificationException {

    public ConfigurationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConfigurationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
