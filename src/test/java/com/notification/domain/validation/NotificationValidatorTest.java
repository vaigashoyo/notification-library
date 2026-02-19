package com.notification.domain.validation;

import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.ValidationException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.Recipient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("NotificationValidator - Validación de notificaciones")
class NotificationValidatorTest {

    @Nested
    @DisplayName("Validaciones generales")
    class ValidacionesGenerales {

        @Test
        @DisplayName("V007: Debe lanzar excepción cuando la notificación es nula")
        void debeLanzarExcepcionCuandoNotificacionEsNula() {
            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(null));
            assertEquals(ErrorCode.V007, exception.getErrorCode());
        }

        @Test
        @DisplayName("V003: Debe lanzar excepción cuando el cuerpo es nulo")
        void debeLanzarExcepcionCuandoCuerpoEsNulo() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);
            Notification notification = new Notification(null, recipient, "Asunto", null, Channel.EMAIL, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V003, exception.getErrorCode());
        }

        @Test
        @DisplayName("V003: Debe lanzar excepción cuando el cuerpo está en blanco")
        void debeLanzarExcepcionCuandoCuerpoEstaEnBlanco() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);
            Notification notification = new Notification(null, recipient, "Asunto", "   ", Channel.EMAIL, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V003, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Validaciones de Email")
    class ValidacionesEmail {

        @Test
        @DisplayName("V004: Debe lanzar excepción cuando el asunto del email es nulo")
        void debeLanzarExcepcionCuandoAsuntoEsNulo() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);
            Notification notification = new Notification(null, recipient, null, "Cuerpo del mensaje", Channel.EMAIL, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V004, exception.getErrorCode());
        }

        @Test
        @DisplayName("V004: Debe lanzar excepción cuando el asunto del email está en blanco")
        void debeLanzarExcepcionCuandoAsuntoEstaEnBlanco() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);
            Notification notification = new Notification(null, recipient, "   ", "Cuerpo del mensaje", Channel.EMAIL, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V004, exception.getErrorCode());
        }

        @ParameterizedTest(name = "V002: Debe rechazar email con formato inválido: {0}")
        @ValueSource(strings = {"invalid", "no-at-sign", "@nodomain", "spaces in@email.com"})
        @DisplayName("V002: Debe lanzar excepción para formatos de email inválidos")
        void debeLanzarExcepcionParaFormatosDeEmailInvalidos(String email) {
            Recipient recipient = new Recipient(email, "Usuario", Channel.EMAIL);
            Notification notification = new Notification(null, recipient, "Asunto", "Cuerpo", Channel.EMAIL, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V002, exception.getErrorCode());
        }

        @Test
        @DisplayName("Debe pasar validación con un email válido")
        void debePasarValidacionConEmailValido() {
            Recipient recipient = new Recipient("user@example.com", "Usuario", Channel.EMAIL);
            Notification notification = Notification.create(recipient, "Asunto", "Cuerpo del mensaje", Channel.EMAIL);

            assertDoesNotThrow(() -> NotificationValidator.validate(notification));
        }
    }

    @Nested
    @DisplayName("Validaciones de SMS")
    class ValidacionesSms {

        @ParameterizedTest(name = "V005: Debe rechazar teléfono con formato inválido: {0}")
        @ValueSource(strings = {"abc", "123", "+0invalidphone"})
        @DisplayName("V005: Debe lanzar excepción para formatos de teléfono inválidos")
        void debeLanzarExcepcionParaFormatosDeTelefonoInvalidos(String phone) {
            Recipient recipient = new Recipient(phone, "Usuario", Channel.SMS);
            Notification notification = new Notification(null, recipient, null, "Cuerpo", Channel.SMS, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V005, exception.getErrorCode());
        }

        @Test
        @DisplayName("Debe pasar validación con un SMS válido")
        void debePasarValidacionConSmsValido() {
            Recipient recipient = new Recipient("+1234567890", "Usuario", Channel.SMS);
            Notification notification = Notification.create(recipient, null, "Cuerpo del mensaje", Channel.SMS);

            assertDoesNotThrow(() -> NotificationValidator.validate(notification));
        }
    }

    @Nested
    @DisplayName("Validaciones de Push")
    class ValidacionesPush {

        @ParameterizedTest(name = "V009: Debe rechazar token de push con formato inválido: {0}")
        @ValueSource(strings = {"short", "ab!@#$%^&*()"})
        @DisplayName("V009: Debe lanzar excepción para tokens de push inválidos")
        void debeLanzarExcepcionParaTokensDePushInvalidos(String token) {
            Recipient recipient = new Recipient(token, "Usuario", Channel.PUSH);
            Notification notification = new Notification(null, recipient, null, "Cuerpo", Channel.PUSH, null);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> NotificationValidator.validate(notification));
            assertEquals(ErrorCode.V009, exception.getErrorCode());
        }

        @Test
        @DisplayName("Debe pasar validación con un push válido")
        void debePasarValidacionConPushValido() {
            Recipient recipient = new Recipient("abcdefghij1234567890", "Usuario", Channel.PUSH);
            Notification notification = Notification.create(recipient, null, "Cuerpo del mensaje", Channel.PUSH);

            assertDoesNotThrow(() -> NotificationValidator.validate(notification));
        }
    }
}
