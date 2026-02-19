package com.notification.infrastructure.adapter.sms;

import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.port.output.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MockSmsSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(MockSmsSender.class);
    private static final String PROVIDER_NAME = "mock-sms";

    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("[MOCK] Enviando SMS a: {}", notification.recipient().address());
        logger.debug("[MOCK] Cuerpo: {}", notification.body());

        logger.info("[MOCK] SMS enviado exitosamente a: {}", notification.recipient().address());
        return NotificationResult.success(notification.id(), PROVIDER_NAME,
                "[MOCK] SMS enviado a " + notification.recipient().address());
    }

    @Override
    public Channel getChannel() {
        return Channel.SMS;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.SMS;
    }

    @Override
    public void configure(ProviderConfig config) {
        logger.info("[MOCK] Configurando MockSmsSender");
        this.configured = true;
        logger.info("[MOCK] MockSmsSender configurado exitosamente");
    }
}
