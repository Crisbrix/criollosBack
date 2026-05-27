package com.criollos.noti.infrastructure.rest;

import com.criollos.noti.application.dto.OrderItemCommand;
import com.criollos.noti.application.dto.SendOrderNotificationCommand;
import com.criollos.noti.application.usecase.SendOrderNotificationUseCase;
import com.criollos.noti.domain.exception.NotificationValidationException;
import com.criollos.noti.infrastructure.config.OrderNotificationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class OrderNotificationController {

    private final SendOrderNotificationUseCase sendOrderNotificationUseCase;
    private final OrderNotificationProperties properties;

    public OrderNotificationController(
            SendOrderNotificationUseCase sendOrderNotificationUseCase,
            OrderNotificationProperties properties
    ) {
        this.sendOrderNotificationUseCase = sendOrderNotificationUseCase;
        this.properties = properties;
    }

    @PostMapping("/orders")
    public ResponseEntity<NotificationResponse> notifyOrderCreated(
            @RequestBody OrderNotificationRequest request
    ) {
        SendOrderNotificationCommand command = new SendOrderNotificationCommand(
                request.orderId(),
                request.customerName(),
                request.customerEmail(),
                resolveRecipientEmail(request.recipientEmail()),
                request.total(),
                toCommands(request.items())
        );

        sendOrderNotificationUseCase.send(command);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new NotificationResponse(
                "Notificacion de pedido enviada correctamente.",
                Instant.now()
        ));
    }

    private String resolveRecipientEmail(String requestRecipientEmail) {
        if (requestRecipientEmail != null && !requestRecipientEmail.isBlank()) {
            return requestRecipientEmail;
        }

        if (properties.getRecipientEmail() != null && !properties.getRecipientEmail().isBlank()) {
            return properties.getRecipientEmail();
        }

        throw new NotificationValidationException(
                "Debes enviar recipientEmail o configurar notifications.orders.recipient-email."
        );
    }

    private List<OrderItemCommand> toCommands(List<OrderItemRequest> items) {
        if (items == null) {
            return List.of();
        }

        return items.stream()
                .map(item -> new OrderItemCommand(item.name(), item.quantity(), item.unitPrice()))
                .toList();
    }

    public record OrderNotificationRequest(
            String orderId,
            String customerName,
            String customerEmail,
            String recipientEmail,
            BigDecimal total,
            List<OrderItemRequest> items
    ) {
    }

    public record OrderItemRequest(
            String name,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }

    public record NotificationResponse(
            String message,
            Instant timestamp
    ) {
    }
}
