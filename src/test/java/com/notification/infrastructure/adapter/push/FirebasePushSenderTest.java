package com.notification.infrastructure.adapter.push;

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

@DisplayName("FirebasePushSender - Adaptador de envío de push por Firebase")
class FirebasePushSenderTest {

    private FirebasePushSender sender;

    @BeforeEach
    void setUp() {
        sender = new FirebasePushSender();
    }

    @Test
    @DisplayName("getChannel() debe retornar Channel.PUSH")
    void getChannelDebeRetornarPush() {
        assertEquals(Channel.PUSH, sender.getChannel());
    }

    @Test
    @DisplayName("getProviderName() debe retornar 'firebase'")
    void getProviderNameDebeRetornarFirebase() {
        assertEquals("firebase", sender.getProviderName());
    }

    @Test
    @DisplayName("configure() con configuración válida debe completarse sin error")
    void configureConConfigValidaDebeCompletarse() {
        ProviderConfig config = new ProviderConfig("firebase", Map.of(
                "serverKey", "test-server-key",
                "projectId", "test-project-id"
        ));

        assertDoesNotThrow(() -> sender.configure(config));
    }

    @Test
    @DisplayName("configure() con credenciales faltantes debe lanzar ConfigurationException")
    void configureConCredencialesFaltantesDebeLanzarConfigurationException() {
        ProviderConfig config = new ProviderConfig("firebase", Map.of(
                "serverKey", "test-server-key"
        ));

        assertThrows(ConfigurationException.class, () -> sender.configure(config));
    }

    @Test
    @DisplayName("send() después de configurar debe retornar resultado exitoso")
    void sendDespuesDeConfigurarDebeRetornarResultadoExitoso() {
        ProviderConfig config = new ProviderConfig("firebase", Map.of(
                "serverKey", "test-server-key",
                "projectId", "test-project-id"
        ));
        sender.configure(config);

        Recipient recipient = new Recipient("device_token_abc123", "Test", Channel.PUSH);
        Notification notification = Notification.create(recipient, "Title", "Test body", Channel.PUSH);

        NotificationResult result = sender.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("firebase", result.providerName());
    }

    @Test
    @DisplayName("send() sin configurar debe lanzar SendingException")
    void sendSinConfigurarDebeLanzarSendingException() {
        Recipient recipient = new Recipient("device_token_abc123", "Test", Channel.PUSH);
        Notification notification = Notification.create(recipient, "Title", "Test body", Channel.PUSH);

        assertThrows(SendingException.class, () -> sender.send(notification));
    }
}
