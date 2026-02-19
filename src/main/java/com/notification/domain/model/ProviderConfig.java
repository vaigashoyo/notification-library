package com.notification.domain.model;

import java.util.Map;

public record ProviderConfig(String providerName, Map<String, String> credentials) {

    public ProviderConfig {
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío");
        }
        credentials = credentials == null ? Map.of() : Map.copyOf(credentials);
    }

    public String getCredential(String key) {
        return credentials.get(key);
    }

    public boolean hasCredential(String key) {
        return credentials.containsKey(key);
    }
}
