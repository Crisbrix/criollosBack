package com.criollos.noti.infrastructure.email;

import com.criollos.noti.domain.exception.NotificationException;
import com.criollos.noti.domain.model.EmailNotification;
import com.criollos.noti.domain.Gateway.EmailNotificationSender;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class MailjetEmailNotificationSender implements EmailNotificationSender {

    private final RestTemplate restTemplate;

    public MailjetEmailNotificationSender() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(EmailNotification notification) {
        String apiKey = "b268db483ee8fc470209d1ab6aba0484";
        String secretKey = "cb8779310db367b2625ef8f3d9c4f4b4";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, secretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = Map.of(
            "From", Map.of("Email", "cmateoramirez@ucundinamarca.edu.co", "Name", "Criollos"),
            "To", List.of(Map.of("Email", notification.recipientEmail())),
            "Subject", notification.subject(),
            "TextPart", notification.body()
        );

        Map<String, Object> body = Map.of("Messages", List.of(message));
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.mailjet.com/v3.1/send",
                    entity,
                    String.class
            );
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new NotificationException("Mailjet respondio con status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new NotificationException("No fue posible enviar la notificacion por correo.", e);
        }
    }
}
