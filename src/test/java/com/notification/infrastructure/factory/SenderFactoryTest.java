package com.notification.infrastructure.factory;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.model.Channel;
import com.notification.domain.port.output.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("SenderFactory - Pruebas unitarias")
class SenderFactoryTest {

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationSender smsSender;

    @Mock
    private NotificationSender pushSender;

    private SenderFactory senderFactory;

    @BeforeEach
    void setUp() {
        senderFactory = new SenderFactory();
    }

    @Test
    @DisplayName("registerSender y getSender funcionan correctamente")
    void registerSender_y_getSender_funcionan_correctamente() {
        when(emailSender.getProviderName()).thenReturn("EmailProvider");

        senderFactory.registerSender(Channel.EMAIL, emailSender);
        NotificationSender result = senderFactory.getSender(Channel.EMAIL);

        assertNotNull(result);
        assertEquals(emailSender, result);
        assertEquals("EmailProvider", result.getProviderName());
    }

    @Test
    @DisplayName("getSender lanza ConfigurationException con ErrorCode C001 cuando no hay sender registrado")
    void getSender_lanza_ConfigurationException_cuando_no_hay_sender() {
        ConfigurationException exception = assertThrows(
                ConfigurationException.class,
                () -> senderFactory.getSender(Channel.EMAIL)
        );

        assertEquals(ErrorCode.C001, exception.getErrorCode());
    }

    @Test
    @DisplayName("hasSender retorna true cuando hay sender registrado")
    void hasSender_retorna_true_cuando_hay_sender() {
        when(emailSender.getProviderName()).thenReturn("EmailProvider");

        senderFactory.registerSender(Channel.EMAIL, emailSender);

        assertTrue(senderFactory.hasSender(Channel.EMAIL));
    }

    @Test
    @DisplayName("hasSender retorna false cuando no hay sender registrado")
    void hasSender_retorna_false_cuando_no_hay_sender() {
        assertFalse(senderFactory.hasSender(Channel.EMAIL));
    }

    @Test
    @DisplayName("Puede registrar senders para diferentes canales")
    void puede_registrar_senders_para_diferentes_canales() {
        when(emailSender.getProviderName()).thenReturn("EmailProvider");
        when(smsSender.getProviderName()).thenReturn("SmsProvider");
        when(pushSender.getProviderName()).thenReturn("PushProvider");

        senderFactory.registerSender(Channel.EMAIL, emailSender);
        senderFactory.registerSender(Channel.SMS, smsSender);
        senderFactory.registerSender(Channel.PUSH, pushSender);

        assertTrue(senderFactory.hasSender(Channel.EMAIL));
        assertTrue(senderFactory.hasSender(Channel.SMS));
        assertTrue(senderFactory.hasSender(Channel.PUSH));

        assertEquals(emailSender, senderFactory.getSender(Channel.EMAIL));
        assertEquals(smsSender, senderFactory.getSender(Channel.SMS));
        assertEquals(pushSender, senderFactory.getSender(Channel.PUSH));
    }
}
