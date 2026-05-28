package com.pedidos.Pedidos.infrastructure.client;

import com.pedidos.Pedidos.domain.model.DetallePedido;
import com.pedidos.Pedidos.domain.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoIntegracionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PedidoIntegracionService.class);

    private final RestClient authClient;
    private final RestClient productoClient;
    private final RestClient notiClient;

    public PedidoIntegracionService(
            RestClient.Builder restClientBuilder
    ) {
        this.authClient = restClientBuilder.baseUrl("https://auth-j0i2.onrender.com").build();
        this.productoClient = restClientBuilder.baseUrl("https://producto-2fxd.onrender.com").build();
        this.notiClient = restClientBuilder.baseUrl("https://pedidos-dg22.onrender.com").build();
    }

    public void validarPedidoConApis(Pedido pedido) {
        validarUsuario(pedido);
        validarProductos(pedido);
    }

    public void reducirStock(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Integer productoId = validarProductoId(detalle.getProductoId());
            try {
                ProductoResponse producto = productoClient.patch()
                        .uri("/productos/reducir-stock/{productoId}?cantidad={cantidad}",
                                productoId,
                                detalle.getCantidad())
                        .retrieve()
                        .body(ProductoResponse.class);
                if (producto == null || producto.productoId() == null) {
                    throw new RuntimeException("No se pudo reducir el stock del producto " + productoId);
                }
            } catch (RestClientException e) {
                throw new RuntimeException("No se pudo reducir el stock del producto " + productoId, e);
            }
        }
    }

    public void notificarPedidoCreado(Pedido pedido) {
        try {
            notiClient.post()
                    .uri("/api/notifications/orders")
                    .body(toNotificationRequest(pedido))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException exception) {
            LOGGER.warn("No se pudo enviar la notificacion del pedido {}", pedido.getNumeroPedido(), exception);
        }
    }

    private void validarUsuario(Pedido pedido) {
        if (pedido.getCedulaCliente() == null || pedido.getCedulaCliente().isBlank()) {
            throw new RuntimeException("La cedula del cliente es obligatoria");
        }

        UsuarioResponse usuario;
        try {
            usuario = authClient.get()
                    .uri("/api/criollos/usuarios/interno/buscar/{cedula}", pedido.getCedulaCliente())
                    .retrieve()
                    .body(UsuarioResponse.class);
        } catch (RestClientResponseException e) {
            throw new RuntimeException("El usuario con cedula " + pedido.getCedulaCliente() + " no existe");
        } catch (RestClientException e) {
            throw new RuntimeException("No se pudo conectar con la API de usuarios", e);
        }

        if (usuario == null || usuario.id() == null) {
            throw new RuntimeException("El usuario con cedula " + pedido.getCedulaCliente() + " no existe");
        }

        pedido.setUsuarioId(usuario.id());
        if (pedido.getNombreCliente() == null || pedido.getNombreCliente().isBlank()) {
            pedido.setNombreCliente((usuario.nombre() + " " + usuario.apellido()).trim());
        }
        pedido.setEmailCliente(usuario.email());
    }

    private void validarProductos(Pedido pedido) {
        for (DetallePedido detalle : pedido.getDetalles()) {
            Integer productoId = validarProductoId(detalle.getProductoId());
            ProductoResponse producto;

            try {
                producto = productoClient.get()
                        .uri("/productos/buscar/{productoId}", productoId)
                        .retrieve()
                        .body(ProductoResponse.class);
            } catch (RestClientException e) {
                throw new RuntimeException("No se pudo conectar con la API de productos", e);
            }

            if (producto == null || producto.productoId() == null) {
                throw new RuntimeException("El producto " + productoId + " no existe");
            }
            if (Boolean.FALSE.equals(producto.activo())) {
                throw new RuntimeException("El producto " + producto.nombre() + " no esta activo");
            }
            if (producto.stock() == null || producto.stock() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + producto.nombre());
            }

            detalle.setNombreProducto(producto.nombre());
            detalle.setPrecioUnitario(BigDecimal.valueOf(producto.precio()));
        }
    }

    private Integer validarProductoId(Integer productoId) {
        if (productoId == null) {
            throw new RuntimeException("El productoId es obligatorio");
        }
        return productoId;
    }

    private OrderNotificationRequest toNotificationRequest(Pedido pedido) {
        return new OrderNotificationRequest(
                pedido.getNumeroPedido(),
                pedido.getNombreCliente(),
                pedido.getEmailCliente(),
                pedido.getEmailCliente(),
                pedido.getTotal(),
                toNotificationItems(pedido.getDetalles())
        );
    }

    private List<OrderItemRequest> toNotificationItems(List<DetallePedido> detalles) {
        if (detalles == null) {
            return List.of();
        }

        return detalles.stream()
                .map(detalle -> new OrderItemRequest(
                        detalle.getNombreProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario()
                ))
                .toList();
    }

    private record UsuarioResponse(
            Long id,
            String cedula,
            String nombre,
            String apellido,
            String email,
            String estado
    ) {
    }

    private record ProductoResponse(
            Integer productoId,
            String nombre,
            String descripcion,
            Double precio,
            Integer stock,
            Integer stockMinimo,
            Boolean activo,
            String categoria
    ) {
    }

    private record OrderNotificationRequest(
            String orderId,
            String customerName,
            String customerEmail,
            String recipientEmail,
            BigDecimal total,
            List<OrderItemRequest> items
    ) {
    }

    private record OrderItemRequest(
            String name,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }
}
