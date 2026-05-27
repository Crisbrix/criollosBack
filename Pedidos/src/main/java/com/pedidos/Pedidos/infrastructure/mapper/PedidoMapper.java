package com.pedidos.Pedidos.infrastructure.mapper;

import com.pedidos.Pedidos.domain.model.DetallePedido;
import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.DetallePedidoData;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.PedidoData;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PedidoMapper {

    public Pedido toPedido(PedidoData pedidoData) {
        if (pedidoData == null) {
            return null;
        }

        return new Pedido(
                pedidoData.getId(),
                pedidoData.getNumeroPedido(),
                pedidoData.getCedulaCliente(),
                pedidoData.getNombreCliente(),
                pedidoData.getMesa(),
                pedidoData.getUsuarioId(),
                pedidoData.getMetodoPago(),
                pedidoData.getEstado(),
                pedidoData.getSubtotal(),
                pedidoData.getImpuesto(),
                pedidoData.getTotal(),
                pedidoData.getFechaCreacion(),
                pedidoData.getFechaActualizacion(),
                toDetallePedidoList(pedidoData.getDetalles())
        );
    }

    public PedidoData toPedidoData(Pedido pedido) {
        if (pedido == null) {
            return null;
        }

        PedidoData pedidoData = new PedidoData(
                pedido.getId(),
                pedido.getNumeroPedido(),
                pedido.getCedulaCliente(),
                pedido.getNombreCliente(),
                pedido.getMesa(),
                pedido.getUsuarioId(),
                pedido.getMetodoPago(),
                pedido.getEstado(),
                pedido.getSubtotal(),
                pedido.getImpuesto(),
                pedido.getTotal(),
                pedido.getFechaCreacion(),
                pedido.getFechaActualizacion(),
                Collections.emptyList()
        );
        List<DetallePedidoData> detalles = toDetallePedidoDataList(pedido.getDetalles(), pedidoData);
        pedidoData.setDetalles(detalles);
        return pedidoData;
    }

    private List<DetallePedido> toDetallePedidoList(List<DetallePedidoData> detallesData) {
        if (detallesData == null) {
            return Collections.emptyList();
        }

        return detallesData.stream()
                .map(detalleData -> new DetallePedido(
                        detalleData.getId(),
                        detalleData.getProductoId(),
                        detalleData.getNombreProducto(),
                        detalleData.getCantidad(),
                        detalleData.getPrecioUnitario(),
                        detalleData.getSubtotal(),
                        detalleData.getNotas()
                ))
                .toList();
    }

    private List<DetallePedidoData> toDetallePedidoDataList(List<DetallePedido> detalles, PedidoData pedidoData) {
        if (detalles == null) {
            return Collections.emptyList();
        }

        return detalles.stream()
                .map(detalle -> new DetallePedidoData(
                        detalle.getId(),
                        detalle.getProductoId(),
                        detalle.getNombreProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal(),
                        detalle.getNotas(),
                        pedidoData
                ))
                .toList();
    }
}
