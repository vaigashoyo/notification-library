package com.notification.infrastructure.adapter.sms;

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

@DisplayName("TwilioSmsSender - Adaptador de envío de SMS por Twilio")
class TwilioSmsSenderTest {

    private TwilioSmsSender sender;

    @BeforeEach
    void setUp() {
        sender = new TwilioSmsSender();
    }

    @Test
    @DisplayName("getChannel() debe retornar Channel.SMS")
    void getChannelDebeRetornarSms() {
        assertEquals(Channel.SMS, sender.getChannel());
    }

    @Test
    @DisplayName("getProviderName() debe retornar 'twilio'")
    void getProviderNameDebeRetornarTwilio() {
        assertEquals("twilio", sender.getProviderName());
    }

    @Test
    @DisplayName("configure() con configuración válida debe completarse sin error")
    void configureConConfigValidaDebeCompletarse() {
        ProviderConfig config = new ProviderConfig("twilio", Map.of(
                "accountSid", "AC_test_sid",
                "authToken", "test_auth_token",
                "fromNumber", "+1987654321"
        ));

        assertDoesNotThrow(() -> sender.configure(config));
    }

    @Test
    @DisplayName("configure() con credenciales faltantes debe lanzar ConfigurationException")
    void configureConCredencialesFaltantesDebeLanzarConfigurationException() {
        ProviderConfig config = new ProviderConfig("twilio", Map.of(
                "accountSid", "AC_test_sid"
        ));

        assertThrows(ConfigurationException.class, () -> sender.configure(config));
    }

    @Test
    @DisplayName("send() después de configurar debe retornar resultado exitoso")
    void sendDespuesDeConfigurarDebeRetornarResultadoExitoso() {
        ProviderConfig config = new ProviderConfig("twilio", Map.of(
                "accountSid", "AC_test_sid",
                "authToken", "test_auth_token",
                "fromNumber", "+1987654321"
        ));
        sender.configure(config);

        Recipient recipient = new Recipient("+1234567890", "Test", Channel.SMS);
        Notification notification = Notification.create(recipient, null, "Test body", Channel.SMS);

        NotificationResult result = sender.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("twilio", result.providerName());
    }

    @Test
    @DisplayName("send() sin configurar debe lanzar SendingException")
    void sendSinConfigurarDebeLanzarSendingException() {
        Recipient recipient = new Recipient("+1234567890", "Test", Channel.SMS);
        Notification notification = Notification.create(recipient, null, "Test body", Channel.SMS);

        assertThrows(SendingException.class, () -> sender.send(notification));
    }
}
