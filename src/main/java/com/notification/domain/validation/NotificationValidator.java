package com.notification.domain.validation;

import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.ValidationException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Pattern;

public final class NotificationValidator {

    private static final Logger logger = LogManager.getLogger(NotificationValidator.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[1-9]\\d{6,14}$"
    );

    private static final Pattern PUSH_TOKEN_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_:.-]{10,}$"
    );

    private NotificationValidator() {
    }

    public static void validate(Notification notification) {
        logger.debug("Iniciando validación de notificación");

        validateNotNull(notification);
        validateChannel(notification);
        validateRecipient(notification);
        validateBody(notification);
        validateChannelSpecific(notification);

        logger.debug("Validación completada exitosamente para notificación: {}", notification.id());
    }

    private static void validateNotNull(Notification notification) {
        if (notification == null) {
            logger.error("La notificación es nula");
            throw new ValidationException(ErrorCode.V007);
        }
    }

    private static void validateChannel(Notification notification) {
        if (notification.channel() == null) {
            logger.error("Canal no especificado");
            throw new ValidationException(ErrorCode.V006);
        }
    }

    private static void validateRecipient(Notification notification) {
        if (notification.recipient() == null) {
            logger.error("Destinatario nulo");
            throw new ValidationException(ErrorCode.V001);
        }
        if (notification.recipient().address() == null || notification.recipient().address().isBlank()) {
            logger.error("Dirección de destinatario vacía");
            throw new ValidationException(ErrorCode.V008);
        }
    }

    private static void validateBody(Notification notification) {
        if (notification.body() == null || notification.body().isBlank()) {
            logger.error("Cuerpo del mensaje vacío");
            throw new ValidationException(ErrorCode.V003);
        }
    }

    private static void validateChannelSpecific(Notification notification) {
        switch (notification.channel()) {
            case EMAIL -> validateEmail(notification);
            case SMS -> validateSms(notification);
            case PUSH -> validatePush(notification);
        }
    }

    private static void validateEmail(Notification notification) {
        if (notification.subject() == null || notification.subject().isBlank()) {
            logger.error("Asunto requerido para email");
            throw new ValidationException(ErrorCode.V004, notification.recipient().address());
        }
        if (!EMAIL_PATTERN.matcher(notification.recipient().address()).matches()) {
            logger.error("Formato de email inválido: {}", notification.recipient().address());
            throw new ValidationException(ErrorCode.V002, notification.recipient().address());
        }
    }

    private static void validateSms(Notification notification) {
        if (!PHONE_PATTERN.matcher(notification.recipient().address()).matches()) {
            logger.error("Formato de teléfono inválido: {}", notification.recipient().address());
            throw new ValidationException(ErrorCode.V005, notification.recipient().address());
        }
    }

    private static void validatePush(Notification notification) {
        if (!PUSH_TOKEN_PATTERN.matcher(notification.recipient().address()).matches()) {
            logger.error("Token de dispositivo inválido: {}", notification.recipient().address());
            throw new ValidationException(ErrorCode.V009, notification.recipient().address());
        }
    }
}
