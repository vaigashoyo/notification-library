package com.notification.facade;

import com.notification.domain.model.Channel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("NotificationConfig y ChannelConfig - Configuración de notificaciones")
class NotificationConfigTest {

    @Test
    @DisplayName("NotificationConfig.create() debe retornar instancia no nula")
    void createDebeRetornarInstanciaNoNula() {
        NotificationConfig config = NotificationConfig.create();

        assertNotNull(config);
    }

    @Test
    @DisplayName("El encadenamiento de builders debe funcionar con múltiples canales")
    void encadenamientoDeBuilderDebeFuncionarConMultiplesCanales() {
        ChannelConfig config1 = ChannelConfig.sms().provider("mock-sms");
        ChannelConfig config2 = ChannelConfig.push().provider("mock-push");

        NotificationConfig notificationConfig = NotificationConfig.create()
                .channel(config1)
                .channel(config2);

        assertNotNull(notificationConfig);
        assertEquals(2, notificationConfig.getChannels().size());
    }

    @Test
    @DisplayName("getChannels() debe retornar todos los canales agregados")
    void getChannelsDebeRetornarTodosLosCanalesAgregados() {
        NotificationConfig config = NotificationConfig.create()
                .channel(ChannelConfig.email().provider("smtp"))
                .channel(ChannelConfig.sms().provider("mock-sms"))
                .channel(ChannelConfig.push().provider("mock-push"));

        assertEquals(3, config.getChannels().size());
    }

    @Test
    @DisplayName("ChannelConfig.email() debe establecer el canal como EMAIL")
    void channelConfigEmailDebeEstablecerCanalEmail() {
        ChannelConfig config = ChannelConfig.email();

        assertEquals(Channel.EMAIL, config.getChannel());
    }

    @Test
    @DisplayName("ChannelConfig.sms() debe establecer el canal como SMS")
    void channelConfigSmsDebeEstablecerCanalSms() {
        ChannelConfig config = ChannelConfig.sms();

        assertEquals(Channel.SMS, config.getChannel());
    }

    @Test
    @DisplayName("ChannelConfig.push() debe establecer el canal como PUSH")
    void channelConfigPushDebeEstablecerCanalPush() {
        ChannelConfig config = ChannelConfig.push();

        assertEquals(Channel.PUSH, config.getChannel());
    }

    @Test
    @DisplayName("El builder fluido debe permitir configurar proveedor, host, puerto y credenciales")
    void builderFluidoDebePermitirConfigurarProveedorHostPuertoYCredenciales() {
        ChannelConfig config = ChannelConfig.email()
                .provider("smtp")
                .host("h")
                .port("587")
                .credential("k", "v");

        assertEquals("smtp", config.getProviderName());
        assertTrue(config.getCredentials().containsKey("host"));
        assertEquals("h", config.getCredentials().get("host"));
        assertTrue(config.getCredentials().containsKey("port"));
        assertEquals("587", config.getCredentials().get("port"));
        assertTrue(config.getCredentials().containsKey("k"));
        assertEquals("v", config.getCredentials().get("k"));
    }
}
