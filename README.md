# Notification Library

Biblioteca reutilizable en Java 25 para enviar notificaciones por múltiples canales: **Email**, **SMS** y **Push Notification**.

Arquitectura hexagonal, sin frameworks, con soporte asíncrono y logging detallado.

---

## Instalación

### Requisitos

- Java 25+
- Maven 3.9+

### Compilar

```bash
mvn clean package
```

### Ejecutar con Docker (sin Java local)

```bash
docker build -t notification-library .
docker run notification-library
```

---

## Quick Start

```java
import com.notification.domain.model.*;
import com.notification.facade.*;

// 1. Configurar canales
NotificationConfig config = NotificationConfig.create()
    .channel(ChannelConfig.sms()
        .provider("mock-sms"))
    .channel(ChannelConfig.push()
        .provider("mock-push"));

// 2. Crear facade
NotificationFacade facade = NotificationFacade.create(config);

// 3. Enviar notificación
Recipient recipient = new Recipient("+1234567890", "Juan", Channel.SMS);
Notification notification = Notification.create(
    recipient, null, "Hola desde la librería", Channel.SMS);

NotificationResult result = facade.send(notification).get();
System.out.println(result.status()); // SUCCESS

// 4. Cerrar
facade.shutdown();
```

---

## Configuración

### Email - SMTP

```java
ChannelConfig.email()
    .provider("smtp")
    .host("smtp.gmail.com")
    .port("587")
    .username("tu-email@gmail.com")
    .password("tu-password")
    .from("tu-email@gmail.com")
```

### Email - SendGrid

```java
ChannelConfig.email()
    .provider("sendgrid")
    .credential("apiKey", "SG.tu-api-key")
    .from("noreply@tudominio.com")
```

### SMS - Twilio

```java
ChannelConfig.sms()
    .provider("twilio")
    .credential("accountSid", "ACxxxxxxxxx")
    .credential("authToken", "tu-auth-token")
    .credential("fromNumber", "+15551234567")
```

### SMS - Mock (para pruebas)

```java
ChannelConfig.sms()
    .provider("mock-sms")
```

### Push - Firebase

```java
ChannelConfig.push()
    .provider("firebase")
    .credential("serverKey", "tu-server-key")
    .credential("projectId", "tu-proyecto-id")
```

### Push - Mock (para pruebas)

```java
ChannelConfig.push()
    .provider("mock-push")
```

### Configuración completa

```java
NotificationConfig config = NotificationConfig.create()
    .channel(ChannelConfig.email()
        .provider("smtp")
        .host("smtp.gmail.com")
        .port("587")
        .username("user@gmail.com")
        .password("pass")
        .from("user@gmail.com"))
    .channel(ChannelConfig.sms()
        .provider("twilio")
        .credential("accountSid", "ACxxx")
        .credential("authToken", "token")
        .credential("fromNumber", "+15551234567"))
    .channel(ChannelConfig.push()
        .provider("firebase")
        .credential("serverKey", "key")
        .credential("projectId", "my-project"));

NotificationFacade facade = NotificationFacade.create(config);
```

---

## Envío en lote

```java
List<Notification> batch = List.of(
    Notification.create(new Recipient("+1234567890", "Ana", Channel.SMS),
        null, "Mensaje 1", Channel.SMS),
    Notification.create(new Recipient("+0987654321", "Luis", Channel.SMS),
        null, "Mensaje 2", Channel.SMS)
);

List<NotificationResult> results = facade.sendBatch(batch).get();
results.forEach(r -> System.out.println(r.status() + ": " + r.message()));
```

---

## Manejo de errores

```java
try {
    NotificationResult result = facade.send(notification).get();
    if (result.isSuccess()) {
        System.out.println("Enviado: " + result.message());
    }
} catch (ExecutionException e) {
    Throwable cause = e.getCause();
    if (cause instanceof ValidationException ve) {
        System.out.println("Error de validación: " + ve.getErrorCode());
    } else if (cause instanceof SendingException se) {
        System.out.println("Error de envío: " + se.getErrorCode());
    } else if (cause instanceof ConfigurationException ce) {
        System.out.println("Error de configuración: " + ce.getErrorCode());
    }
}
```

### Catálogo de errores

| Código | Tipo | Descripción |
|--------|------|-------------|
| V001 | Validación | Destinatario vacío o nulo |
| V002 | Validación | Formato de email inválido |
| V003 | Validación | Cuerpo del mensaje vacío |
| V004 | Validación | Asunto requerido para email |
| V005 | Validación | Formato de teléfono inválido |
| V006 | Validación | Canal no especificado |
| V007 | Validación | Notificación nula |
| V008 | Validación | Dirección de destinatario vacía |
| V009 | Validación | Token de dispositivo inválido |
| V010 | Validación | Metadatos inválidos |
| S001 | Envío | Autenticación del proveedor fallida |
| S002 | Envío | Proveedor no disponible |
| S003 | Envío | Tiempo de espera agotado |
| S004 | Envío | Límite de envíos excedido |
| S005 | Envío | Error de conexión con el proveedor |
| S006 | Envío | Error inesperado durante el envío |
| C001 | Configuración | Ningún sender configurado para el canal |
| C002 | Configuración | Configuración de proveedor inválida |

---

## API Reference

### NotificationFacade

Punto de entrada principal de la librería.

| Método | Retorno | Descripción |
|--------|---------|-------------|
| `create(NotificationConfig)` | `NotificationFacade` | Crea una instancia configurada |
| `send(Notification)` | `CompletableFuture<NotificationResult>` | Envía una notificación |
| `sendBatch(List<Notification>)` | `CompletableFuture<List<NotificationResult>>` | Envía un lote |
| `shutdown()` | `void` | Cierra recursos internos |

### Notification

Record inmutable que representa una notificación.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `id` | `String` | ID único (auto-generado si es null) |
| `recipient` | `Recipient` | Destinatario |
| `subject` | `String` | Asunto (requerido para email) |
| `body` | `String` | Cuerpo del mensaje |
| `channel` | `Channel` | Canal de envío |
| `metadata` | `Map<String,String>` | Metadatos opcionales |

### Recipient

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `address` | `String` | Email, teléfono o token |
| `name` | `String` | Nombre del destinatario |
| `channel` | `Channel` | Canal asociado |

### NotificationResult

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `notificationId` | `String` | ID de la notificación |
| `status` | `NotificationStatus` | SUCCESS o FAILED |
| `providerName` | `String` | Nombre del proveedor usado |
| `message` | `String` | Mensaje descriptivo |
| `timestamp` | `Instant` | Momento del resultado |

### Channel (enum)

`EMAIL`, `SMS`, `PUSH`

### Proveedores disponibles

| Canal | Proveedor | Nombre | Uso |
|-------|-----------|--------|-----|
| Email | SMTP | `"smtp"` | Producción |
| Email | SendGrid | `"sendgrid"` | Producción |
| SMS | Twilio | `"twilio"` | Producción |
| SMS | Mock | `"mock-sms"` | Testing |
| Push | Firebase | `"firebase"` | Producción |
| Push | Mock | `"mock-push"` | Testing |

---

## Sobre este proyecto

Este proyecto fue desarrollado en su totalidad por **Claude Code** (Claude Opus 4.6, 1M context) con la supervisión de **Guillermo David Loyo Gómez** ([@vaigashoyo](https://github.com/vaigashoyo)).

### Herramientas de IA utilizadas

- **Claude Code CLI** — Agente de desarrollo autónomo que escribió todo el código fuente, tests y documentación
- **MCP (Model Context Protocol)** — Integración directa con la API de GitHub para crear el repositorio, gestionar branches y configurar el proyecto remoto
- **Skills** — Sistema de habilidades especializadas que Claude Code consultó antes de cada fase:
  - `java-fundamentals` — Sintaxis, OOP, records, sealed classes
  - `java-concurrency` — CompletableFuture, virtual threads, ExecutorService
  - `java-testing` — JUnit 5, Mockito, @ParameterizedTest, cobertura
  - `java-docker` — Multi-stage builds, non-root user, JVM container settings
  - `git-advanced-workflows` — Gitflow, branches, tags, merge strategies
- **Contexto detallado (CLAUDE.md)** — Archivo de instrucciones del proyecto que define arquitectura, convenciones, patrones, catálogo de errores y restricciones. Claude Code lo consultó en cada decisión para mantener coherencia.
