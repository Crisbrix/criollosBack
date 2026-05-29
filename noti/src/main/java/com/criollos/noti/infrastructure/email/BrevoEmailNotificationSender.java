package com.criollos.noti.infrastructure.email;

import com.criollos.noti.domain.exception.NotificationException;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.Gateway.EmailNotificationSender;
import com.criollos.noti.infrastructure.config.OrderNotificationProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class BrevoEmailNotificationSender implements EmailNotificationSender {

    private final RestTemplate restTemplate;
    private final OrderNotificationProperties properties;

    public BrevoEmailNotificationSender(OrderNotificationProperties properties) {
        this.restTemplate = new RestTemplate();
        this.properties = properties;
    }

    @Override
    public void send(EmailNotification notification) {
        String apiKey = System.getenv("BREVO_API_KEY");

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "sender", Map.of("email", properties.getFromEmail(), "name", "Criollos"),
            "to", List.of(Map.of("email", notification.recipientEmail())),
            "subject", notification.subject(),
            "textContent", notification.body()
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    entity,
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new NotificationException("Brevo respondio con status: " + response.getStatusCode());
            }
        } catch (NotificationException e) {
            throw e;
        } catch (Exception e) {
            throw new NotificationException("No fue posible enviar la notificacion por correo.", e);
        }
    }
}
