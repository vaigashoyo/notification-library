package com.notification.infrastructure.adapter.email;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.SendingException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.port.output.NotificationSender;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class SmtpEmailSender implements NotificationSender {

    private static final Logger logger = LogManager.getLogger(SmtpEmailSender.class);
    private static final String PROVIDER_NAME = "smtp";

    private String host;
    private String port;
    private String username;
    private String password;
    private String from;
    private boolean configured;

    @Override
    public NotificationResult send(Notification notification) {
        logger.info("Iniciando envío de email SMTP a: {}", notification.recipient().address());

        if (!configured) {
            logger.error("SmtpEmailSender no está configurado");
            throw new SendingException(ErrorCode.S002, "SMTP no configurado");
        }

        try {
            logger.debug("Configurando propiedades SMTP - host: {}, port: {}", host, port);
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            logger.debug("Creando sesión de correo");
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            logger.debug("Construyendo mensaje - de: {}, para: {}, asunto: {}",
                    from, notification.recipient().address(), notification.subject());
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(notification.recipient().address()));
            message.setSubject(notification.subject());
            message.setText(notification.body());

            logger.debug("Enviando mensaje SMTP");
            Transport.send(message);

            logger.info("Email enviado exitosamente a: {}", notification.recipient().address());
            return NotificationResult.success(notification.id(), PROVIDER_NAME,
                    "Email enviado a " + notification.recipient().address());

        } catch (MessagingException e) {
            logger.error("Error al enviar email SMTP: {}", e.getMessage(), e);
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
        logger.info("Configurando SmtpEmailSender con proveedor: {}", config.providerName());

        this.host = config.getCredential("host");
        this.port = config.getCredential("port");
        this.username = config.getCredential("username");
        this.password = config.getCredential("password");
        this.from = config.getCredential("from");

        if (host == null || port == null || username == null || password == null || from == null) {
            logger.error("Configuración SMTP incompleta");
            throw new ConfigurationException(ErrorCode.C002,
                    "Se requieren: host, port, username, password, from");
        }

        this.configured = true;
        logger.info("SmtpEmailSender configurado exitosamente - host: {}, port: {}", host, port);
    }
}
