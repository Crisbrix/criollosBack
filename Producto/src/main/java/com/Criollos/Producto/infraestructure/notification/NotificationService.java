package com.Criollos.Producto.infraestructure.notification;

import com.Criollos.Producto.domain.model.Producto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class NotificationService {

    private final WebClient webClient;
    private final String notificationServiceUrl;

    public NotificationService(
            WebClient.Builder webClientBuilder,
            @Value("${notification.service.url:http://localhost:8083}") String notificationServiceUrl
    ) {
        this.webClient = webClientBuilder.build();
        this.notificationServiceUrl = notificationServiceUrl;
    }

    public void sendProductCreatedNotification(Producto producto) {
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

        webClient.post()
                .uri(notificationServiceUrl + "/api/notifications/orders")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(e -> {
                    System.err.println("Error enviando notificación: " + e.getMessage());
                    return Mono.empty();
                })
                .subscribe();
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
