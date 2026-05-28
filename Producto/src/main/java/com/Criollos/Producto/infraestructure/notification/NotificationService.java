package com.Criollos.Producto.infraestructure.notification;

import com.Criollos.Producto.domain.model.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public NotificationService(
            RestTemplate restTemplate,
            @Value("${notification.service.url:http://localhost:8083}") String notificationServiceUrl
    ) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    public void sendProductCreatedNotification(Producto producto) {
        try {
            ProductNotificationRequest request = new ProductNotificationRequest(
                    "PROD-" + producto.getProductoId(),
                    "Sistema Criollos",
                    "admin@criollos.com",
                    null,
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

            restTemplate.postForEntity(notificationServiceUrl + "/api/notifications/orders", entity, Void.class);
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
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
