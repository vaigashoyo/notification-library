package com.notification.application.service;

import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.SendingException;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.port.input.SendBatchNotificationUseCase;
import com.notification.domain.port.input.SendNotificationUseCase;
import com.notification.domain.port.output.NotificationSender;
import com.notification.domain.validation.NotificationValidator;
import com.notification.infrastructure.factory.SenderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationService implements SendNotificationUseCase, SendBatchNotificationUseCase {

    private static final Logger logger = LogManager.getLogger(NotificationService.class);

    private final SenderFactory senderFactory;
    private final ExecutorService executor;

    public NotificationService(SenderFactory senderFactory) {
        this.senderFactory = senderFactory;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        logger.info("NotificationService inicializado con virtual threads");
    }

    public NotificationService(SenderFactory senderFactory, ExecutorService executor) {
        this.senderFactory = senderFactory;
        this.executor = executor;
        logger.info("NotificationService inicializado con executor personalizado");
    }

    @Override
    public CompletableFuture<NotificationResult> send(Notification notification) {
        logger.info("Recibida solicitud de envío - canal: {}, destino: {}",
                notification.channel(), notification.recipient().address());

        return CompletableFuture.supplyAsync(() -> {
            logger.debug("Ejecutando envío asíncrono para notificación: {}", notification.id());

            logger.debug("Paso 1: Validando notificación");
            NotificationValidator.validate(notification);

            logger.debug("Paso 2: Obteniendo sender para canal: {}", notification.channel());
            NotificationSender sender = senderFactory.getSender(notification.channel());

            logger.debug("Paso 3: Enviando notificación via proveedor: {}", sender.getProviderName());
            NotificationResult result = sender.send(notification);

            logger.info("Envío completado - id: {}, estado: {}, proveedor: {}",
                    result.notificationId(), result.status(), result.providerName());
            return result;

        }, executor).exceptionally(throwable -> {
            Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
            logger.error("Error durante el envío de notificación: {}", cause.getMessage(), cause);

            if (cause instanceof com.notification.domain.exception.NotificationException) {
                throw (RuntimeException) cause;
            }

            return NotificationResult.failed(notification.id(), "unknown",
                    "Error inesperado: " + cause.getMessage());
        });
    }

    @Override
    public CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications) {
        logger.info("Recibida solicitud de envío en lote - cantidad: {}", notifications.size());

        List<CompletableFuture<NotificationResult>> futures = notifications.stream()
                .map(this::send)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<NotificationResult> results = futures.stream()
                            .map(CompletableFuture::join)
                            .toList();
                    logger.info("Envío en lote completado - total: {}, exitosos: {}",
                            results.size(),
                            results.stream().filter(NotificationResult::isSuccess).count());
                    return results;
                });
    }

    public void shutdown() {
        logger.info("Cerrando NotificationService");
        executor.shutdown();
    }
}
