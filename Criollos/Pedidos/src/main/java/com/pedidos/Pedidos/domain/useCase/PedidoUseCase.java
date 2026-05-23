package com.pedidos.Pedidos.domain.useCase;

import com.pedidos.Pedidos.domain.model.DetallePedido;
import com.pedidos.Pedidos.domain.model.EstadoPedido;
import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.domain.model.gateway.PedidoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
public class PedidoUseCase {

    private final PedidoGateway pedidoGateway;

    public Pedido guardarPedido(Pedido pedido) {
        validarPedido(pedido);

        LocalDateTime ahora = LocalDateTime.now();
        pedido.setNumeroPedido(generarNumeroPedido());
        pedido.setEstado(EstadoPedido.PENDIENTE.name());
        pedido.setFechaCreacion(ahora);
        pedido.setFechaActualizacion(ahora);
        calcularTotales(pedido);

        return pedidoGateway.guardarPedido(pedido);
    }

    public Pedido buscarPedidoPorNumero(String numeroPedido) {
        return pedidoGateway.buscarPedidoPorNumero(numeroPedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoGateway.listarPedidos();
    }

    public List<Pedido> listarPedidosPorEstado(String estado) {
        validarEstado(estado);
        return pedidoGateway.listarPedidosPorEstado(estado);
    }

    public void eliminarPedidoPorNumero(String numeroPedido) {
        pedidoGateway.eliminarPedidoPorNumero(numeroPedido);
    }

    public Pedido actualizarPedido(String numeroPedido, Pedido pedido) {
        validarPedido(pedido);
        validarEstado(pedido.getEstado());
        pedido.setFechaActualizacion(LocalDateTime.now());
        calcularTotales(pedido);
        return pedidoGateway.actualizarPedido(numeroPedido, pedido);
    }

    public Pedido actualizarEstadoPedido(String numeroPedido, String estado) {
        validarEstado(estado);
        return pedidoGateway.actualizarEstadoPedido(numeroPedido, estado);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido == null || pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new RuntimeException("El pedido debe tener al menos un producto");
        }

        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProductoId() == null || detalle.getNombreProducto() == null) {
                throw new RuntimeException("Cada detalle debe tener productoId y nombreProducto");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new RuntimeException("La cantidad del producto debe ser mayor a cero");
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("El precio unitario no puede ser negativo");
            }
        }
    }

    private void validarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("El estado del pedido es obligatorio");
        }
        EstadoPedido.valueOf(estado);
    }

    private void calcularTotales(Pedido pedido) {
        BigDecimal subtotal = BigDecimal.ZERO;

        for (DetallePedido detalle : pedido.getDetalles()) {
            BigDecimal detalleSubtotal = detalle.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(detalleSubtotal);
            subtotal = subtotal.add(detalleSubtotal);
        }

        BigDecimal impuesto = pedido.getImpuesto() == null ? BigDecimal.ZERO : pedido.getImpuesto();
        pedido.setSubtotal(subtotal);
        pedido.setImpuesto(impuesto);
        pedido.setTotal(subtotal.add(impuesto));
    }

    private String generarNumeroPedido() {
        return "PED-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmssSSS"));
    }
}
