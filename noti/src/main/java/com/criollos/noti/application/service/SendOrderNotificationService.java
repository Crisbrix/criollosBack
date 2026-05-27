package com.criollos.noti.application.service;

import com.criollos.noti.application.dto.OrderItemCommand;
import com.criollos.noti.application.dto.SendOrderNotificationCommand;
import com.criollos.noti.application.usecase.SendOrderNotificationUseCase;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.model.OrderItem;
import com.criollos.noti.domain.model.OrderNotification;
import com.criollos.noti.domain.port.EmailNotificationSender;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SendOrderNotificationService implements SendOrderNotificationUseCase {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    private final EmailNotificationSender emailNotificationSender;

    public SendOrderNotificationService(EmailNotificationSender emailNotificationSender) {
        this.emailNotificationSender = emailNotificationSender;
    }

    @Override
    public void send(SendOrderNotificationCommand command) {
        OrderNotification orderNotification = toDomain(command);
        EmailNotification emailNotification = new EmailNotification(
                orderNotification.recipientEmail(),
                buildSubject(orderNotification),
                buildBody(orderNotification)
        );

        emailNotificationSender.send(emailNotification);
    }

    private OrderNotification toDomain(SendOrderNotificationCommand command) {
        List<OrderItem> items = command.items() == null
                ? List.of()
                : command.items().stream()
                .map(this::toDomain)
                .toList();

        return new OrderNotification(
                command.orderId(),
                command.customerName(),
                command.customerEmail(),
                command.recipientEmail(),
                command.total(),
                items
        );
    }

    private OrderItem toDomain(OrderItemCommand command) {
        return new OrderItem(command.name(), command.quantity(), command.unitPrice());
    }

    private String buildSubject(OrderNotification orderNotification) {
        return "Nuevo pedido recibido #" + orderNotification.orderId();
    }

    private String buildBody(OrderNotification orderNotification) {
        StringBuilder body = new StringBuilder();
        body.append("Se ha registrado un nuevo pedido.\n\n");
        body.append("Pedido: ").append(orderNotification.orderId()).append("\n");
        body.append("Cliente: ").append(orderNotification.customerName()).append("\n");

        if (orderNotification.customerEmail() != null && !orderNotification.customerEmail().isBlank()) {
            body.append("Correo cliente: ").append(orderNotification.customerEmail()).append("\n");
        }

        body.append("Total: ").append(formatCurrency(orderNotification.total())).append("\n");

        if (!orderNotification.items().isEmpty()) {
            body.append("\nProductos:\n");
            orderNotification.items().forEach(item -> body
                    .append("- ")
                    .append(item.quantity())
                    .append(" x ")
                    .append(item.name())
                    .append(" (")
                    .append(formatCurrency(item.unitPrice()))
                    .append(")\n"));
        }

        return body.toString();
    }

    private String formatCurrency(BigDecimal value) {
        return NumberFormat.getCurrencyInstance(COLOMBIA).format(value);
    }
}
