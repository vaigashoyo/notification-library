package com.notification.demo;

import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.Recipient;
import com.notification.facade.ChannelConfig;
import com.notification.facade.NotificationConfig;
import com.notification.facade.NotificationFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class NotificationDemo {

    private static final Logger logger = LogManager.getLogger(NotificationDemo.class);

    public static void main(String[] args) {
        logger.info("=== Notification Library Demo ===");

        // Configurar canales con proveedores mock
        NotificationConfig config =
                NotificationConfig.create().channel(ChannelConfig.sms().provider("mock-sms"))
                        .channel(ChannelConfig.push().provider("mock-push"));

        NotificationFacade facade = NotificationFacade.create(config);

        try {
            // 1. Envío individual de SMS
            logger.info("\n \n");
            logger.info("--- Envío individual de SMS ---");
            Recipient smsRecipient = new Recipient("+1234567890", "Juan Pérez", Channel.SMS);
            Notification smsNotification = Notification.create(smsRecipient, null,
                    "Tu código de verificación es: 123456", Channel.SMS);

            CompletableFuture<NotificationResult> smsResult = facade.send(smsNotification);
            NotificationResult result = smsResult.get();
            logger.info("Resultado SMS: {} - {}", result.status(), result.message());

            // 2. Envío individual de Push
            logger.info("\n \n");
            logger.info("--- Envío individual de Push ---");
            Recipient pushRecipient =
                    new Recipient("device_token_abc123def456", "María López", Channel.PUSH);
            Notification pushNotification = Notification.create(pushRecipient, "Nueva oferta",
                    "Descuento del 50% en tu próxima compra", Channel.PUSH);

            CompletableFuture<NotificationResult> pushResult = facade.send(pushNotification);
            result = pushResult.get();
            logger.info("Resultado Push: {} - {}", result.status(), result.message());

            // 3. Envío en lote
            logger.info("\n \n");
            logger.info("--- Envío en lote ---");
            List<Notification> batch = List.of(
                    Notification.create(new Recipient("+1987654321", "Carlos García", Channel.SMS),
                            null, "Recordatorio: tu cita es mañana a las 10:00", Channel.SMS),
                    Notification.create(new Recipient("+1555666777", "Ana Martínez", Channel.SMS),
                            null, "Tu pedido ha sido enviado", Channel.SMS),
                    Notification.create(
                            new Recipient("device_token_xyz789abc012", "Pedro Sánchez",
                                    Channel.PUSH),
                            "Mensaje nuevo", "Tienes 3 mensajes sin leer", Channel.PUSH));

            CompletableFuture<List<NotificationResult>> batchResult = facade.sendBatch(batch);
            List<NotificationResult> results = batchResult.get();

            logger.info("Resultados del lote:");
            for (NotificationResult r : results) {
                logger.info("  - [{}] {} | {}", r.status(), r.providerName(), r.message());
            }

            // 4. Manejo de errores
            logger.info("\n \n");
            logger.info("--- Manejo de errores ---");
            try {
                Recipient badRecipient = new Recipient("invalid", "Test", Channel.SMS);
                Notification badNotification =
                        Notification.create(badRecipient, null, "Test message", Channel.SMS);
                facade.send(badNotification).get();
            } catch (Exception e) {
                Throwable cause = e.getCause() != null ? e.getCause() : e;
                logger.info("Error capturado correctamente: {}", cause.getMessage());
            }

            logger.info("\n \n");
            logger.info("=== Demo completada exitosamente ===");

        } catch (Exception e) {
            logger.error("Error inesperado en la demo: {}", e.getMessage(), e);
        } finally {
            facade.shutdown();
        }
    }
}
