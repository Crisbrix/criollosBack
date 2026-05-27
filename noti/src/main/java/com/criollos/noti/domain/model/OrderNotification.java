package com.criollos.noti.domain.model;

import com.criollos.noti.domain.exception.NotificationValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

public record OrderNotification(
        String orderId,
        String customerName,
        String customerEmail,
        String recipientEmail,
        BigDecimal total,
        List<OrderItem> items
) {

    private static final Pattern SIMPLE_EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public OrderNotification {
        if (isBlank(orderId)) {
            throw new NotificationValidationException("El identificador del pedido es obligatorio.");
        }

        if (isBlank(customerName)) {
            throw new NotificationValidationException("El nombre del cliente es obligatorio.");
        }

        if (isBlank(recipientEmail)) {
            throw new NotificationValidationException("El correo destino de la notificacion es obligatorio.");
        }

        if (!isBlank(customerEmail) && !isValidEmail(customerEmail)) {
            throw new NotificationValidationException("El correo del cliente no tiene un formato valido.");
        }

        if (!isValidEmail(recipientEmail)) {
            throw new NotificationValidationException("El correo destino no tiene un formato valido.");
        }

        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotificationValidationException("El total del pedido no puede ser negativo.");
        }

        items = items == null ? List.of() : List.copyOf(items);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static boolean isValidEmail(String value) {
        return SIMPLE_EMAIL_PATTERN.matcher(value).matches();
    }
}
