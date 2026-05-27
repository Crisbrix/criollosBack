package com.criollos.noti.domain.model;

public record EmailNotification(
        String recipientEmail,
        String subject,
        String body
) {
}
