package com.notification.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Modelos del dominio - Records de notificación")
class NotificationTest {

    @Nested
    @DisplayName("Notification - Record de notificación")
    class NotificationRecord {

        @Test
        @DisplayName("El método create debe crear una notificación válida")
        void createDebeCrearNotificacionValida() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);

            Notification notification = Notification.create(recipient, "Asunto", "Cuerpo", Channel.EMAIL);

            assertNotNull(notification);
            assertNotNull(notification.id());
            assertEquals(recipient, notification.recipient());
            assertEquals("Asunto", notification.subject());
            assertEquals("Cuerpo", notification.body());
            assertEquals(Channel.EMAIL, notification.channel());
        }

        @Test
        @DisplayName("Debe auto-generar UUID cuando el id es nulo")
        void debeAutoGenerarUuidCuandoIdEsNulo() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);

            Notification notification = new Notification(null, recipient, "Asunto", "Cuerpo", Channel.EMAIL, null);

            assertNotNull(notification.id());
            assertFalse(notification.id().isBlank());
        }

        @Test
        @DisplayName("Debe rechazar recipient nulo")
        void debeRechazarRecipientNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Notification(null, null, "Asunto", "Cuerpo", Channel.EMAIL, null));
        }

        @Test
        @DisplayName("Debe rechazar canal nulo")
        void debeRechazarCanalNulo() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);

            assertThrows(IllegalArgumentException.class,
                    () -> new Notification(null, recipient, "Asunto", "Cuerpo", null, null));
        }
    }

    @Nested
    @DisplayName("Recipient - Record de destinatario")
    class RecipientRecord {

        @Test
        @DisplayName("Debe rechazar dirección nula")
        void debeRechazarDireccionNula() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Recipient(null, "Usuario", Channel.EMAIL));
        }

        @Test
        @DisplayName("Debe rechazar dirección en blanco")
        void debeRechazarDireccionEnBlanco() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Recipient("   ", "Usuario", Channel.EMAIL));
        }

        @Test
        @DisplayName("Debe rechazar canal nulo")
        void debeRechazarCanalNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Recipient("user@example.com", "Usuario", null));
        }
    }

    @Nested
    @DisplayName("NotificationResult - Resultado de notificación")
    class NotificationResultRecord {

        @Test
        @DisplayName("success() debe crear un resultado exitoso")
        void successDebeCrearResultadoExitoso() {
            NotificationResult result = NotificationResult.success("id-123", "provider", "Enviado correctamente");

            assertNotNull(result);
            assertEquals("id-123", result.notificationId());
            assertEquals(NotificationStatus.SUCCESS, result.status());
            assertEquals("provider", result.providerName());
            assertEquals("Enviado correctamente", result.message());
            assertNotNull(result.timestamp());
        }

        @Test
        @DisplayName("failed() debe crear un resultado fallido")
        void failedDebeCrearResultadoFallido() {
            NotificationResult result = NotificationResult.failed("id-456", "provider", "Error de envío");

            assertNotNull(result);
            assertEquals("id-456", result.notificationId());
            assertEquals(NotificationStatus.FAILED, result.status());
            assertEquals("provider", result.providerName());
            assertEquals("Error de envío", result.message());
            assertNotNull(result.timestamp());
        }

        @Test
        @DisplayName("isSuccess() debe retornar true para resultado exitoso")
        void isSuccessDebeRetornarTrueParaExitoso() {
            NotificationResult result = NotificationResult.success("id-123", "provider", "OK");

            assertTrue(result.isSuccess());
        }

        @Test
        @DisplayName("isSuccess() debe retornar false para resultado fallido")
        void isSuccessDebeRetornarFalseParaFallido() {
            NotificationResult result = NotificationResult.failed("id-456", "provider", "Error");

            assertFalse(result.isSuccess());
        }
    }

    @Nested
    @DisplayName("ProviderConfig - Configuración de proveedor")
    class ProviderConfigRecord {

        @Test
        @DisplayName("Debe rechazar nombre de proveedor nulo")
        void debeRechazarNombreDeProveedorNulo() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProviderConfig(null, Map.of("key", "value")));
        }

        @Test
        @DisplayName("Debe rechazar nombre de proveedor en blanco")
        void debeRechazarNombreDeProveedorEnBlanco() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ProviderConfig("   ", Map.of("key", "value")));
        }

        @Test
        @DisplayName("Debe retornar copia defensiva de las credenciales")
        void debeRetornarCopiaDefensivaDeLasCredenciales() {
            Map<String, String> credenciales = new java.util.HashMap<>();
            credenciales.put("apiKey", "secret-123");

            ProviderConfig config = new ProviderConfig("sendgrid", credenciales);

            Map<String, String> credencialesObtenidas = config.credentials();
            assertThrows(UnsupportedOperationException.class,
                    () -> credencialesObtenidas.put("nuevaClave", "nuevoValor"));
        }
    }
}
