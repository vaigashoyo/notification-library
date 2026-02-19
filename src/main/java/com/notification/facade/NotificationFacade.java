package com.notification.facade;

import com.notification.application.service.NotificationService;
import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.port.output.NotificationSender;
import com.notification.infrastructure.adapter.email.SendGridEmailSender;
import com.notification.infrastructure.adapter.email.SmtpEmailSender;
import com.notification.infrastructure.adapter.push.FirebasePushSender;
import com.notification.infrastructure.adapter.push.MockPushSender;
import com.notification.infrastructure.adapter.sms.MockSmsSender;
import com.notification.infrastructure.adapter.sms.TwilioSmsSender;
import com.notification.infrastructure.factory.SenderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class NotificationFacade {

    private static final Logger logger = LogManager.getLogger(NotificationFacade.class);

    private static final Map<String, java.util.function.Supplier<NotificationSender>> PROVIDER_REGISTRY = Map.of(
            "smtp", SmtpEmailSender::new,
            "sendgrid", SendGridEmailSender::new,
            "twilio", TwilioSmsSender::new,
            "mock-sms", MockSmsSender::new,
            "firebase", FirebasePushSender::new,
            "mock-push", MockPushSender::new
    );

    private final NotificationService service;

    private NotificationFacade(NotificationService service) {
        this.service = service;
    }

    public static NotificationFacade create(NotificationConfig config) {
        logger.info("Creando NotificationFacade con {} canales configurados", config.getChannels().size());

        SenderFactory factory = new SenderFactory();

        for (ChannelConfig channelConfig : config.getChannels()) {
            logger.debug("Configurando canal: {} con proveedor: {}",
                    channelConfig.getChannel(), channelConfig.getProviderName());

            NotificationSender sender = createSender(channelConfig.getProviderName());
            sender.configure(new ProviderConfig(channelConfig.getProviderName(), channelConfig.getCredentials()));
            factory.registerSender(channelConfig.getChannel(), sender);

            logger.info("Canal {} configurado con proveedor: {}",
                    channelConfig.getChannel(), channelConfig.getProviderName());
        }

        NotificationService service = new NotificationService(factory);
        logger.info("NotificationFacade creado exitosamente");
        return new NotificationFacade(service);
    }

    private static NotificationSender createSender(String providerName) {
        var supplier = PROVIDER_REGISTRY.get(providerName);
        if (supplier == null) {
            logger.error("Proveedor desconocido: {}", providerName);
            throw new ConfigurationException(ErrorCode.C002,
                    "Proveedor desconocido: " + providerName +
                            ". Proveedores disponibles: " + PROVIDER_REGISTRY.keySet());
        }
        return supplier.get();
    }

    public CompletableFuture<NotificationResult> send(Notification notification) {
        logger.info("Enviando notificación - canal: {}, destino: {}",
                notification.channel(), notification.recipient().address());
        return service.send(notification);
    }

    public CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications) {
        logger.info("Enviando lote de {} notificaciones", notifications.size());
        return service.sendBatch(notifications);
    }

    public void shutdown() {
        logger.info("Cerrando NotificationFacade");
        service.shutdown();
    }
}
