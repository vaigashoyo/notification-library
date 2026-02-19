package com.notification.domain.model;

import java.time.Instant;

public record NotificationResult(
        String notificationId,
        NotificationStatus status,
        String providerName,
        String message,
        Instant timestamp
) {

    public NotificationResult {
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public static NotificationResult success(String notificationId, String providerName, String message) {
        return new NotificationResult(notificationId, NotificationStatus.SUCCESS, providerName, message, null);
    }

    public static NotificationResult failed(String notificationId, String providerName, String message) {
        return new NotificationResult(notificationId, NotificationStatus.FAILED, providerName, message, null);
    }

    public boolean isSuccess() {
        return status == NotificationStatus.SUCCESS;
    }
}
