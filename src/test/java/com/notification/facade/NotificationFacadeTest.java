package com.notification.facade;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.Recipient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("NotificationFacade - Fachada principal de notificaciones")
class NotificationFacadeTest {

    @Test
    @DisplayName("create() con configuración válida debe crear la fachada exitosamente")
    void createConConfigValidaDebeCrearFachadaExitosamente() {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.sms().provider("mock-sms"));

        NotificationFacade facade = NotificationFacade.create(config);

        assertNotNull(facade);
        facade.shutdown();
    }

    @Test
    @DisplayName("send() debe delegar y retornar resultado")
    void sendDebeDelgarYRetornarResultado() throws Exception {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.sms().provider("mock-sms"));

        NotificationFacade facade = NotificationFacade.create(config);

        Recipient recipient = new Recipient("+1234567890", "Test", Channel.SMS);
        Notification notification = Notification.create(recipient, null, "Test body", Channel.SMS);

        CompletableFuture<NotificationResult> future = facade.send(notification);
        NotificationResult result = future.get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.notificationId());
        assertNotNull(result.providerName());

        facade.shutdown();
    }

    @Test
    @DisplayName("sendBatch() debe retornar lista de resultados")
    void sendBatchDebeRetornarListaDeResultados() throws Exception {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.sms().provider("mock-sms"));

        NotificationFacade facade = NotificationFacade.create(config);

        Recipient recipient1 = new Recipient("+1234567890", "Test1", Channel.SMS);
        Recipient recipient2 = new Recipient("+9876543210", "Test2", Channel.SMS);
        Notification notification1 = Notification.create(recipient1, null, "Test body 1", Channel.SMS);
        Notification notification2 = Notification.create(recipient2, null, "Test body 2", Channel.SMS);

        CompletableFuture<List<NotificationResult>> future = facade.sendBatch(List.of(notification1, notification2));
        List<NotificationResult> results = future.get();

        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(NotificationResult::isSuccess));

        facade.shutdown();
    }

    @Test
    @DisplayName("create() con proveedor desconocido debe lanzar ConfigurationException")
    void createConProveedorDesconocidoDebeLanzarConfigurationException() {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.sms().provider("proveedor-inexistente"));

        assertThrows(ConfigurationException.class, () -> NotificationFacade.create(config));
    }

    @Test
    @DisplayName("Integración completa: enviar SMS y push con mock providers")
    void integracionCompletaEnviarSmsYPush() throws Exception {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.sms().provider("mock-sms"))
                .channel(ChannelConfig.push().provider("mock-push"));

        NotificationFacade facade = NotificationFacade.create(config);

        // Enviar SMS
        Recipient smsRecipient = new Recipient("+1234567890", "Test", Channel.SMS);
        Notification smsNotification = Notification.create(smsRecipient, null, "Test SMS body", Channel.SMS);
        NotificationResult smsResult = facade.send(smsNotification).get();

        assertNotNull(smsResult);
        assertTrue(smsResult.isSuccess());

        // Enviar Push
        Recipient pushRecipient = new Recipient("device_token_abc123", "Test", Channel.PUSH);
        Notification pushNotification = Notification.create(pushRecipient, "Title", "Test push body", Channel.PUSH);
        NotificationResult pushResult = facade.send(pushNotification).get();

        assertNotNull(pushResult);
        assertTrue(pushResult.isSuccess());

        facade.shutdown();
    }
}
