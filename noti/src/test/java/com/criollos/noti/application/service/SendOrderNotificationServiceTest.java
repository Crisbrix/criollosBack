package com.criollos.noti.application.service;

import com.criollos.noti.application.dto.OrderItemCommand;
import com.criollos.noti.application.dto.SendOrderNotificationCommand;
import com.criollos.noti.domain.exception.NotificationValidationException;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.port.EmailNotificationSender;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SendOrderNotificationServiceTest {

    private final FakeEmailNotificationSender emailNotificationSender = new FakeEmailNotificationSender();
    private final SendOrderNotificationService service = new SendOrderNotificationService(emailNotificationSender);

    @Test
    void sendsOrderNotificationEmail() {
        SendOrderNotificationCommand command = new SendOrderNotificationCommand(
                "PED-100",
                "Ana Perez",
                "ana@example.com",
                "ventas@example.com",
                new BigDecimal("32000"),
                List.of(new OrderItemCommand("Arepa", 2, new BigDecimal("16000")))
        );

        service.send(command);

        EmailNotification notification = emailNotificationSender.lastNotification;
        assertEquals("ventas@example.com", notification.recipientEmail());
        assertEquals("Nuevo pedido recibido #PED-100", notification.subject());
        assertTrue(notification.body().contains("Cliente: Ana Perez"));
        assertTrue(notification.body().contains("2 x Arepa"));
    }

    @Test
    void failsWhenRecipientEmailIsInvalid() {
        SendOrderNotificationCommand command = new SendOrderNotificationCommand(
                "PED-101",
                "Ana Perez",
                null,
                "correo-invalido",
                BigDecimal.ZERO,
                List.of()
        );

        assertThrows(NotificationValidationException.class, () -> service.send(command));
    }

    private static class FakeEmailNotificationSender implements EmailNotificationSender {

        private EmailNotification lastNotification;

        @Override
        public void send(EmailNotification notification) {
            this.lastNotification = notification;
        }
    }
}
