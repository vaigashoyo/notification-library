package com.notification.domain.port.input;

import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;

import java.util.concurrent.CompletableFuture;

public interface SendNotificationUseCase {

    CompletableFuture<NotificationResult> send(Notification notification);
}
