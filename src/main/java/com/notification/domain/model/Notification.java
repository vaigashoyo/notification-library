package com.notification.domain.model;

import java.util.Map;
import java.util.UUID;

public record Notification(
        String id,
        Recipient recipient,
        String subject,
        String body,
        Channel channel,
        Map<String, String> metadata
) {

    public Notification {
        if (id == null || id.isBlank()) {
            id = UUID.randomUUID().toString();
        }
        if (recipient == null) {
            throw new IllegalArgumentException("El destinatario no puede ser nulo");
        }
        if (channel == null) {
            throw new IllegalArgumentException("El canal no puede ser nulo");
        }
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static Notification create(Recipient recipient, String subject, String body, Channel channel) {
        return new Notification(null, recipient, subject, body, channel, null);
    }

    public static Notification create(Recipient recipient, String subject, String body, Channel channel, Map<String, String> metadata) {
        return new Notification(null, recipient, subject, body, channel, metadata);
    }
}
