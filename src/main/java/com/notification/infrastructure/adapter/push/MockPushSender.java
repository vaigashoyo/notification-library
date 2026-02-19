package com.notification.infrastructure.adapter.push;

import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.port.output.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockPushSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(MockPushSender.class);
    private static final String PROVIDER_NAME = "mock-push";

    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("[MOCK] Enviando push a: {}", notification.recipient().address());
        logger.debug("[MOCK] Título: {}, Cuerpo: {}", notification.subject(), notification.body());

        logger.info("[MOCK] Push enviado exitosamente a: {}", notification.recipient().address());
        return NotificationResult.success(notification.id(), PROVIDER_NAME,
                "[MOCK] Push enviado a " + notification.recipient().address());
    }

    @Override
    public Channel getChannel() {
        return Channel.PUSH;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.PUSH;
    }

    @Override
    public void configure(ProviderConfig config) {
        logger.info("[MOCK] Configurando MockPushSender");
        this.configured = true;
        logger.info("[MOCK] MockPushSender configurado exitosamente");
    }
}
