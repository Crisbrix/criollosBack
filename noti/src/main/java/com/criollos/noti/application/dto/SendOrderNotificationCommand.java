package com.criollos.noti.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record SendOrderNotificationCommand(
        String orderId,
        String customerName,
        String customerEmail,
        String recipientEmail,
        BigDecimal total,
        List<OrderItemCommand> items
) {
}
