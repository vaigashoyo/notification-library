package com.notification.infrastructure.adapter.email;

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

public class SendGridEmailSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(SendGridEmailSender.class);
    private static final String PROVIDER_NAME = "sendgrid";

    private String apiKey;
    private String fromEmail;
    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("Iniciando envío de email SendGrid a: {}", notification.recipient().address());

        if (!configured) {
            logger.error("SendGridEmailSender no está configurado");
            throw new SendingException(ErrorCode.S002, "SendGrid no configurado");
        }

        try {
            logger.debug("Preparando solicitud HTTP a SendGrid API");
            logger.debug("De: {}, Para: {}, Asunto: {}",
                    fromEmail, notification.recipient().address(), notification.subject());

            // Simulación de envío HTTP a la API de SendGrid
            logger.debug("Enviando solicitud POST a api.sendgrid.com/v3/mail/send");
            logger.debug("Autenticación con API key: {}...", apiKey.substring(0, Math.min(4, apiKey.length())));

            logger.info("Email enviado exitosamente via SendGrid a: {}", notification.recipient().address());
            return NotificationResult.success(notification.id(), PROVIDER_NAME,
                    "Email enviado via SendGrid a " + notification.recipient().address());

        } catch (Exception e) {
            logger.error("Error al enviar email via SendGrid: {}", e.getMessage(), e);
            throw new SendingException(ErrorCode.S005, e);
        }
    }

    @Override
    public Channel getChannel() {
        return Channel.EMAIL;
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public boolean supports(Channel channel) {
        return channel == Channel.EMAIL;
    }

    @Override
    public void configure(ProviderConfig config) {
        logger.info("Configurando SendGridEmailSender");

        this.apiKey = config.getCredential("apiKey");
        this.fromEmail = config.getCredential("from");

        if (apiKey == null || fromEmail == null) {
            logger.error("Configuración SendGrid incompleta");
            throw new ConfigurationException(ErrorCode.C002, "Se requieren: apiKey, from");
        }

        this.configured = true;
        logger.info("SendGridEmailSender configurado exitosamente - from: {}", fromEmail);
    }
}
