package com.notification.domain.exception;

public enum ErrorCode {

    // Errores de validación (V001-V010)
    V001("V001", "Destinatario vacío o nulo"),
    V002("V002", "Formato de email inválido"),
    V003("V003", "Cuerpo del mensaje vacío"),
    V004("V004", "Asunto requerido para email"),
    V005("V005", "Formato de teléfono inválido"),
    V006("V006", "Canal no especificado"),
    V007("V007", "Notificación nula"),
    V008("V008", "Dirección de destinatario vacía"),
    V009("V009", "Token de dispositivo inválido para push"),
    V010("V010", "Metadatos inválidos"),

    // Errores de envío (S001-S006)
    S001("S001", "Autenticación del proveedor fallida"),
    S002("S002", "Proveedor no disponible"),
    S003("S003", "Tiempo de espera agotado"),
    S004("S004", "Límite de envíos excedido"),
    S005("S005", "Error de conexión con el proveedor"),
    S006("S006", "Error inesperado durante el envío"),

    // Errores de configuración (C001-C002)
    C001("C001", "Ningún sender configurado para el canal"),
    C002("C002", "Configuración de proveedor inválida");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "[%s] %s".formatted(code, description);
    }
}
