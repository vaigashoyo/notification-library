package com.notification.domain.port.input;

import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SendBatchNotificationUseCase {

    CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications);
}
