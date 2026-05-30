package com.criollos.noti.infrastructure.email;

import com.criollos.noti.domain.exception.NotificationException;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.Gateway.EmailNotificationSender;
import com.criollos.noti.infrastructure.config.OrderNotificationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class BrevoEmailNotificationSender implements EmailNotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrevoEmailNotificationSender.class);

    private final RestTemplate restTemplate;
    private final OrderNotificationProperties properties;

    public BrevoEmailNotificationSender(OrderNotificationProperties properties) {
        this.restTemplate = new RestTemplate();
        this.properties = properties;
    }

    @Override
    public void send(EmailNotification notification) {
        String apiKey = System.getenv("BREVO_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.error("BREVO_API_KEY no configurada en variables de entorno");
            throw new NotificationException("BREVO_API_KEY no configurada en variables de entorno");
        }

        String fromEmail = properties.getFromEmail();
        if (fromEmail == null || fromEmail.isBlank()) {
            LOGGER.error("from-email no configurado en notifications.orders.from-email");
            throw new NotificationException("from-email no configurado en notifications.orders.from-email");
        }

        String recipientEmail = notification.recipientEmail();
        if (recipientEmail == null || recipientEmail.isBlank()) {
            LOGGER.error("recipientEmail vacio para notificacion: {}", notification.subject());
            throw new NotificationException("recipientEmail vacio para notificacion");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "sender", Map.of("email", fromEmail, "name", "Criollos"),
            "to", List.of(Map.of("email", recipientEmail)),
            "subject", notification.subject(),
            "textContent", notification.body()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        LOGGER.info("Enviando email via Brevo a {} desde {} (subject: {})",
                recipientEmail, fromEmail, notification.subject());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    entity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                LOGGER.error("Brevo respondio con status {}: {}", response.getStatusCode(), response.getBody());
                throw new NotificationException("Brevo respondio con status: " + response.getStatusCode());
            }
            LOGGER.info("Email enviado exitosamente a {}", recipientEmail);
        } catch (NotificationException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error al enviar email via Brevo: {}", e.getMessage(), e);
            throw new NotificationException("No fue posible enviar la notificacion por correo.", e);
        }
    }
}
