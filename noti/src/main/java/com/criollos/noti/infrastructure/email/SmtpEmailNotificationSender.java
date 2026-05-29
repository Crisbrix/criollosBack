package com.criollos.noti.infrastructure.email;

import com.criollos.noti.domain.exception.NotificationException;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.Gateway.EmailNotificationSender;
import com.criollos.noti.infrastructure.config.OrderNotificationProperties;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailNotificationSender implements EmailNotificationSender {

    private final JavaMailSender javaMailSender;
    private final OrderNotificationProperties properties;

    public SmtpEmailNotificationSender(
            JavaMailSender javaMailSender,
            OrderNotificationProperties properties
    ) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    @Override
    public void send(EmailNotification notification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getFromEmail());
        message.setTo(notification.recipientEmail());
        message.setSubject(notification.subject());
        message.setText(notification.body());

        try {
            javaMailSender.send(message);
        } catch (MailException exception) {
            throw new NotificationException("No fue posible enviar la notificacion por correo.", exception);
        }
    }
}
