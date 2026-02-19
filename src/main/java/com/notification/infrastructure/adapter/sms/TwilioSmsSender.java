package com.notification.infrastructure.adapter.sms;

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

public class TwilioSmsSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(TwilioSmsSender.class);
    private static final String PROVIDER_NAME = "twilio";

    private String accountSid;
    private String authToken;
    private String fromNumber;
    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("Iniciando envío de SMS Twilio a: {}", notification.recipient().address());

        if (!configured) {
            logger.error("TwilioSmsSender no está configurado");
            throw new SendingException(ErrorCode.S002, "Twilio no configurado");
        }

        try {
            logger.debug("Preparando solicitud a Twilio API");
            logger.debug("De: {}, Para: {}", fromNumber, notification.recipient().address());
            logger.debug("Cuerpo del mensaje: {} caracteres", notification.body().length());

            // Simulación de envío HTTP a la API de Twilio
            logger.debug("Enviando POST a api.twilio.com/2010-04-01/Accounts/{}/Messages.json", accountSid);

            logger.info("SMS enviado exitosamente via Twilio a: {}", notification.recipient().address());
            return NotificationResult.success(notification.id(), PROVIDER_NAME,
                    "SMS enviado via Twilio a " + notification.recipient().address());

        } catch (Exception e) {
            logger.error("Error al enviar SMS via Twilio: {}", e.getMessage(), e);
            throw new SendingException(ErrorCode.S005, e);
        }
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
        logger.info("Configurando TwilioSmsSender");

        this.accountSid = config.getCredential("accountSid");
        this.authToken = config.getCredential("authToken");
        this.fromNumber = config.getCredential("fromNumber");

        if (accountSid == null || authToken == null || fromNumber == null) {
            logger.error("Configuración Twilio incompleta");
            throw new ConfigurationException(ErrorCode.C002, "Se requieren: accountSid, authToken, fromNumber");
        }

        this.configured = true;
        logger.info("TwilioSmsSender configurado exitosamente - from: {}", fromNumber);
    }
}
