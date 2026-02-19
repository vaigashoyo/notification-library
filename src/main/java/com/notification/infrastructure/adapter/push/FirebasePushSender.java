package com.notification.infrastructure.adapter.push;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.SendingException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.port.output.NotificationSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FirebasePushSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(FirebasePushSender.class);
    private static final String PROVIDER_NAME = "firebase";

    private String serverKey;
    private String projectId;
    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("Iniciando envío de push Firebase a: {}", notification.recipient().address());

        if (!configured) {
            logger.error("FirebasePushSender no está configurado");
            throw new SendingException(ErrorCode.S002, "Firebase no configurado");
        }

        try {
            logger.debug("Preparando solicitud a Firebase Cloud Messaging");
            logger.debug("Proyecto: {}, Token: {}...", projectId,
                    notification.recipient().address().substring(0, Math.min(10, notification.recipient().address().length())));
            logger.debug("Título: {}, Cuerpo: {} caracteres",
                    notification.subject(), notification.body().length());

            // Simulación de envío HTTP a Firebase
            logger.debug("Enviando POST a fcm.googleapis.com/v1/projects/{}/messages:send", projectId);

            logger.info("Push enviado exitosamente via Firebase a: {}", notification.recipient().address());
            return NotificationResult.success(notification.id(), PROVIDER_NAME,
                    "Push enviado via Firebase a " + notification.recipient().address());

        } catch (Exception e) {
            logger.error("Error al enviar push via Firebase: {}", e.getMessage(), e);
            throw new SendingException(ErrorCode.S005, e);
        }
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
        logger.info("Configurando FirebasePushSender");

        this.serverKey = config.getCredential("serverKey");
        this.projectId = config.getCredential("projectId");

        if (serverKey == null || projectId == null) {
            logger.error("Configuración Firebase incompleta");
            throw new ConfigurationException(ErrorCode.C002, "Se requieren: serverKey, projectId");
        }

        this.configured = true;
        logger.info("FirebasePushSender configurado exitosamente - proyecto: {}", projectId);
    }
}
