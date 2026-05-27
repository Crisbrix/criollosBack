package com.criollos.noti.application.dto;

import java.math.BigDecimal;

public record OrderItemCommand(
        String name,
        Integer quantity,
        BigDecimal unitPrice
) {
}
