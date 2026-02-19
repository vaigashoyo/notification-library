package com.notification.domain.model;

public record Recipient(String address, String name, Channel channel) {

    public Recipient {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("La dirección del destinatario no puede estar vacía");
        }
        if (channel == null) {
            throw new IllegalArgumentException("El canal no puede ser nulo");
        }
    }
}
