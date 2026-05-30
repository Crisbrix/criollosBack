package com.Criollos.Producto.infraestructure.notification;

import com.Criollos.Producto.domain.model.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;
    private final String defaultRecipientEmail;

    public NotificationService(
            RestTemplate restTemplate,
            @Value("${notification.service.url:https://noti-b116.onrender.com}") String notificationServiceUrl,
            @Value("${notification.recipient.email:cmateoramirez@ucundinamarca.edu.co}") String defaultRecipientEmail
    ) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
        this.defaultRecipientEmail = defaultRecipientEmail;
        log.info("NotificationService inicializado con URL: {}", notificationServiceUrl);
    }

    public void sendProductCreatedNotification(Producto producto) {
        try {
            log.info("Enviando notificación para producto: {}", producto.getNombre());
            String url = notificationServiceUrl + "/api/notifications/orders";
            log.info("URL de notificación: {}", url);

            ProductNotificationRequest request = new ProductNotificationRequest(
                    "PROD-" + producto.getProductoId(),
                    "Sistema Criollos",
                    "admin@criollos.com",
                    defaultRecipientEmail,
                    BigDecimal.valueOf(producto.getPrecio()),
                    List.of(new ProductItemRequest(
                            producto.getNombre(),
                            producto.getStock(),
                            BigDecimal.valueOf(producto.getPrecio())
                    ))
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ProductNotificationRequest> entity = new HttpEntity<>(request, headers);

            log.info("Enviando request a notificaciones...");
            ResponseEntity<Void> response = restTemplate.postForEntity(url, entity, Void.class);
            log.info("Notificación enviada exitosamente. Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("Error enviando notificación: {}", e.getMessage(), e);
        }
    }

    private record ProductNotificationRequest(
            String orderId,
            String customerName,
            String customerEmail,
            String recipientEmail,
            BigDecimal total,
            List<ProductItemRequest> items
    ) {}

    private record ProductItemRequest(
            String name,
            Integer quantity,
            BigDecimal unitPrice
    ) {}
}
