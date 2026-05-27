package com.criollos.noti.domain.model;

import com.criollos.noti.domain.exception.NotificationValidationException;

import java.math.BigDecimal;

public record OrderItem(
        String name,
        Integer quantity,
        BigDecimal unitPrice
) {

    public OrderItem {
        if (isBlank(name)) {
            throw new NotificationValidationException("El nombre del producto es obligatorio.");
        }

        if (quantity == null || quantity <= 0) {
            throw new NotificationValidationException("La cantidad del producto debe ser mayor a cero.");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotificationValidationException("El precio del producto no puede ser negativo.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
