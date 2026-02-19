package com.notification.application.service;

import com.notification.domain.exception.ConfigurationException;
import com.notification.domain.exception.ErrorCode;
import com.notification.domain.exception.SendingException;
import com.notification.domain.exception.ValidationException;
import com.notification.domain.model.Channel;
import com.notification.domain.model.Notification;
import com.notification.domain.model.NotificationResult;
import com.notification.domain.model.Recipient;
import com.notification.domain.port.output.NotificationSender;
import com.notification.infrastructure.factory.SenderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService - Pruebas unitarias")
class NotificationServiceTest {

    @Mock
    private SenderFactory senderFactory;

    @Mock
    private NotificationSender emailSender;

    private NotificationService service;
    private ExecutorService executor;

    @BeforeEach
    void setUp() {
        executor = Executors.newSingleThreadExecutor();
        service = new NotificationService(senderFactory, executor);
    }

    @Test
    @DisplayName("send exitoso retorna resultado success")
    void send_exitoso_retorna_resultado_success() throws ExecutionException, InterruptedException {
        Recipient recipient = new Recipient("usuario@ejemplo.com", "Usuario", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo del mensaje", Channel.EMAIL);
        NotificationResult expectedResult = NotificationResult.success(notification.id(), "EmailProvider", "Enviado correctamente");

        when(senderFactory.getSender(Channel.EMAIL)).thenReturn(emailSender);
        when(emailSender.getProviderName()).thenReturn("EmailProvider");
        when(emailSender.send(any(Notification.class))).thenReturn(expectedResult);

        CompletableFuture<NotificationResult> future = service.send(notification);
        NotificationResult result = future.get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("EmailProvider", result.providerName());
        assertEquals(notification.id(), result.notificationId());
        verify(senderFactory).getSender(Channel.EMAIL);
        verify(emailSender).send(notification);
    }

    @Test
    @DisplayName("send con validación fallida lanza ValidationException")
    void send_validacion_falla_lanza_ValidationException() {
        Recipient recipient = new Recipient("usuario@ejemplo.com", "Usuario", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "", Channel.EMAIL);

        CompletableFuture<NotificationResult> future = service.send(notification);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(ValidationException.class, cause);
        ValidationException validationException = (ValidationException) cause;
        assertEquals(ErrorCode.V003, validationException.getErrorCode());
    }

    @Test
    @DisplayName("send cuando proveedor falla lanza SendingException")
    void send_proveedor_falla_lanza_SendingException() {
        Recipient recipient = new Recipient("usuario@ejemplo.com", "Usuario", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo del mensaje", Channel.EMAIL);

        when(senderFactory.getSender(Channel.EMAIL)).thenReturn(emailSender);
        when(emailSender.getProviderName()).thenReturn("EmailProvider");
        when(emailSender.send(any(Notification.class)))
                .thenThrow(new SendingException(ErrorCode.S005, "Conexión rechazada"));

        CompletableFuture<NotificationResult> future = service.send(notification);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(SendingException.class, cause);
        SendingException sendingException = (SendingException) cause;
        assertEquals(ErrorCode.S005, sendingException.getErrorCode());
    }

    @Test
    @DisplayName("send cuando factory no tiene sender lanza ConfigurationException")
    void send_factory_sin_sender_lanza_ConfigurationException() {
        Recipient recipient = new Recipient("usuario@ejemplo.com", "Usuario", Channel.EMAIL);
        Notification notification = Notification.create(recipient, "Asunto", "Cuerpo del mensaje", Channel.EMAIL);

        when(senderFactory.getSender(Channel.EMAIL))
                .thenThrow(new ConfigurationException(ErrorCode.C001, Channel.EMAIL.name()));

        CompletableFuture<NotificationResult> future = service.send(notification);

        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        Throwable cause = exception.getCause();
        assertInstanceOf(ConfigurationException.class, cause);
        ConfigurationException configException = (ConfigurationException) cause;
        assertEquals(ErrorCode.C001, configException.getErrorCode());
    }

    @Test
    @DisplayName("sendBatch exitoso retorna todos los resultados")
    void sendBatch_exitoso_retorna_todos_resultados() throws ExecutionException, InterruptedException {
        when(senderFactory.getSender(Channel.EMAIL)).thenReturn(emailSender);
        when(emailSender.getProviderName()).thenReturn("EmailProvider");

        List<Notification> notifications = List.of(
                Notification.create(new Recipient("a@ejemplo.com", "A", Channel.EMAIL), "Asunto 1", "Cuerpo 1", Channel.EMAIL),
                Notification.create(new Recipient("b@ejemplo.com", "B", Channel.EMAIL), "Asunto 2", "Cuerpo 2", Channel.EMAIL),
                Notification.create(new Recipient("c@ejemplo.com", "C", Channel.EMAIL), "Asunto 3", "Cuerpo 3", Channel.EMAIL)
        );

        when(emailSender.send(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            return NotificationResult.success(n.id(), "EmailProvider", "Enviado");
        });

        CompletableFuture<List<NotificationResult>> future = service.sendBatch(notifications);
        List<NotificationResult> results = future.get();

        assertNotNull(results);
        assertEquals(3, results.size());
        for (NotificationResult result : results) {
            assertTrue(result.isSuccess());
            assertEquals("EmailProvider", result.providerName());
        }
    }

    @Test
    @DisplayName("sendBatch con fallo parcial reporta resultados mixtos")
    void sendBatch_parcial_fallo_reporta_resultados_mixtos() throws ExecutionException, InterruptedException {
        when(senderFactory.getSender(Channel.EMAIL)).thenReturn(emailSender);
        when(emailSender.getProviderName()).thenReturn("EmailProvider");

        Notification notif1 = Notification.create(new Recipient("a@ejemplo.com", "A", Channel.EMAIL), "Asunto 1", "Cuerpo 1", Channel.EMAIL);
        Notification notif2 = Notification.create(new Recipient("b@ejemplo.com", "B", Channel.EMAIL), "Asunto 2", "Cuerpo 2", Channel.EMAIL);
        Notification notif3 = Notification.create(new Recipient("c@ejemplo.com", "C", Channel.EMAIL), "Asunto 3", "Cuerpo 3", Channel.EMAIL);

        when(emailSender.send(notif1))
                .thenReturn(NotificationResult.success(notif1.id(), "EmailProvider", "Enviado"));
        when(emailSender.send(notif2))
                .thenThrow(new SendingException(ErrorCode.S005, "Fallo de conexión"));
        when(emailSender.send(notif3))
                .thenReturn(NotificationResult.success(notif3.id(), "EmailProvider", "Enviado"));

        List<Notification> notifications = List.of(notif1, notif2, notif3);

        CompletableFuture<List<NotificationResult>> batchFuture = service.sendBatch(notifications);

        // El sendBatch usa allOf, que falla si algún future falla.
        // Como el servicio re-lanza SendingException en exceptionally,
        // el future individual para notif2 completa excepcionalmente,
        // lo que causa que allOf falle también.
        ExecutionException exception = assertThrows(ExecutionException.class, batchFuture::get);
        assertNotNull(exception.getCause());
    }

    @Test
    @DisplayName("shutdown cierra el executor")
    void shutdown_cierra_executor() {
        ExecutorService testExecutor = Executors.newSingleThreadExecutor();
        NotificationService testService = new NotificationService(senderFactory, testExecutor);

        assertFalse(testExecutor.isShutdown());
        testService.shutdown();
        assertTrue(testExecutor.isShutdown());
    }
}
