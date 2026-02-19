package com.notification.infrastructure.adapter.email;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.SendingException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.ProviderConfig;
import com.notification.domain.model.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SendGridEmailSender - Adaptador de envío de email por SendGrid")
class SendGridEmailSenderTest {

    private SendGridEmailSender sender;

    @BeforeEach
    void setUp() {
        sender = new SendGridEmailSender();
    }

    @Test
    @DisplayName("getChannel() debe retornar Channel.EMAIL")
    void getChannelDebeRetornarEmail() {
        assertEquals(Channel.EMAIL, sender.getChannel());
    }

    @Test
    @DisplayName("getProviderName() debe retornar 'sendgrid'")
    void getProviderNameDebeRetornarSendgrid() {
        assertEquals("sendgrid", sender.getProviderName());
    }

    @Test
    @DisplayName("configure() con configuración válida debe completarse sin error")
    void configureConConfigValidaDebeCompletarse() {
        ProviderConfig config = new ProviderConfig("sendgrid", Map.of(
                "apiKey", "SG.test-api-key",
                "from", "test@test.com"
        ));

        assertDoesNotThrow(() -> sender.configure(config));
    }

    @Test
    @DisplayName("configure() con apiKey faltante debe lanzar ConfigurationException")
    void configureConApiKeyFaltanteDebeLanzarConfigurationException() {
        ProviderConfig config = new ProviderConfig("sendgrid", Map.of(
                "from", "test@test.com"
        ));

        assertThrows(ConfigurationException.class, () -> sender.configure(config));
    }

    @Test
    @DisplayName("send() después de configurar debe retornar resultado exitoso")
    void sendDespuesDeConfigurarDebeRetornarResultadoExitoso() {
        ProviderConfig config = new ProviderConfig("sendgrid", Map.of(
                "apiKey", "SG.test-api-key",
                "from", "test@test.com"
        ));
        sender.configure(config);

        Recipient recipient = new Recipient("user@example.com", "Test", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo", Channel.EMAIL);

        NotificationResult result = sender.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("sendgrid", result.providerName());
    }

    @Test
    @DisplayName("send() sin configurar debe lanzar SendingException")
    void sendSinConfigurarDebeLanzarSendingException() {
        Recipient recipient = new Recipient("user@example.com", "Test", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo", Channel.EMAIL);

        assertThrows(SendingException.class, () -> sender.send(notification));
    }
}
