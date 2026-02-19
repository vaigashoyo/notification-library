package com.notification.infrastructure.adapter.email;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.SendingException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.model.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("SmtpEmailSender - Adaptador de envío de email por SMTP")
class SmtpEmailSenderTest {

    private SmtpEmailSender sender;

    @BeforeEach
    void setUp() {
        sender = new SmtpEmailSender();
    }

    @Test
    @DisplayName("getChannel() debe retornar Channel.EMAIL")
    void getChannelDebeRetornarEmail() {
        assertEquals(Channel.EMAIL, sender.getChannel());
    }

    @Test
    @DisplayName("getProviderName() debe retornar 'smtp'")
    void getProviderNameDebeRetornarSmtp() {
        assertEquals("smtp", sender.getProviderName());
    }

    @Test
    @DisplayName("supports(Channel.EMAIL) debe retornar true")
    void supportsEmailDebeRetornarTrue() {
        assertTrue(sender.supports(Channel.EMAIL));
    }

    @Test
    @DisplayName("supports(Channel.SMS) debe retornar false")
    void supportsSmsDebeRetornarFalse() {
        assertFalse(sender.supports(Channel.SMS));
    }

    @Test
    @DisplayName("configure() con configuración válida debe completarse sin error")
    void configureConConfigValidaDebeCompletarse() {
        ProviderConfig config = new ProviderConfig("smtp", Map.of(
                "host", "smtp.test.com",
                "port", "587",
                "username", "user",
                "password", "pass",
                "from", "test@test.com"
        ));

        assertDoesNotThrow(() -> sender.configure(config));
    }

    @Test
    @DisplayName("configure() con credenciales faltantes debe lanzar ConfigurationException con C002")
    void configureConCredencialesFaltantesDebeLanzarConfigurationException() {
        ProviderConfig config = new ProviderConfig("smtp", Map.of(
                "host", "smtp.test.com"
        ));

        ConfigurationException exception = assertThrows(ConfigurationException.class,
                () -> sender.configure(config));
        assertEquals(ErrorCode.C002, exception.getErrorCode());
    }

    @Test
    @DisplayName("send() sin configurar debe lanzar SendingException con S002")
    void sendSinConfigurarDebeLanzarSendingException() {
        Recipient recipient = new Recipient("user@example.com", "Test", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo", Channel.EMAIL);

        SendingException exception = assertThrows(SendingException.class,
                () -> sender.send(notification));
        assertEquals(ErrorCode.S002, exception.getErrorCode());
    }
}
