package com.pedidos.Pedidos.infrastructure.mapper;

import com.pedidos.Pedidos.domain.model.DetallePedido;
import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.DetallePedidoData;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.PedidoData;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PedidoMapperTest {

    private final PedidoMapper mapper = new PedidoMapper();

    @Test
    void retornaNullCuandoLaEntradaEsNull() {
        assertThat(mapper.toPedido(null)).isNull();
        assertThat(mapper.toPedidoData(null)).isNull();
    }

    @Test
    void conviertePedidoDataAPedidoConDetalles() {
        LocalDateTime fecha = LocalDateTime.now();
        PedidoData data = new PedidoData(1L, "PED-1", "123", "Ana", "ana@example.com", "2", 9L, "TARJETA",
                "PENDIENTE", BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("11"), fecha, fecha,
                List.of(new DetallePedidoData(5L, 3, "Jugo", 2, new BigDecimal("5"), BigDecimal.TEN, "Frio", null)));

        Pedido pedido = mapper.toPedido(data);

        assertThat(pedido.getId()).isEqualTo(1L);
        assertThat(pedido.getNumeroPedido()).isEqualTo("PED-1");
        assertThat(pedido.getCedulaCliente()).isEqualTo("123");
        assertThat(pedido.getNombreCliente()).isEqualTo("Ana");
        assertThat(pedido.getEmailCliente()).isEqualTo("ana@example.com");
        assertThat(pedido.getMesa()).isEqualTo("2");
        assertThat(pedido.getUsuarioId()).isEqualTo(9L);
        assertThat(pedido.getMetodoPago()).isEqualTo("TARJETA");
        assertThat(pedido.getEstado()).isEqualTo("PENDIENTE");
        assertThat(pedido.getSubtotal()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(pedido.getImpuesto()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(pedido.getTotal()).isEqualByComparingTo("11");
        assertThat(pedido.getFechaCreacion()).isEqualTo(fecha);
        assertThat(pedido.getFechaActualizacion()).isEqualTo(fecha);
        assertThat(pedido.getDetalles()).singleElement()
                .satisfies(detalle -> {
                    assertThat(detalle.getId()).isEqualTo(5L);
                    assertThat(detalle.getProductoId()).isEqualTo(3L);
                    assertThat(detalle.getNombreProducto()).isEqualTo("Jugo");
                    assertThat(detalle.getCantidad()).isEqualTo(2);
                    assertThat(detalle.getPrecioUnitario()).isEqualByComparingTo("5");
                    assertThat(detalle.getSubtotal()).isEqualByComparingTo(BigDecimal.TEN);
                    assertThat(detalle.getNotas()).isEqualTo("Frio");
                });
    }

    @Test
    void conviertePedidoAPedidoDataConRelacionEnDetalles() {
        LocalDateTime fecha = LocalDateTime.now();
        Pedido pedido = new Pedido(1L, "PED-1", "123", "Ana", "ana@example.com", "2", 9L, "TARJETA",
                "PENDIENTE", BigDecimal.TEN, BigDecimal.ONE, new BigDecimal("11"), fecha, fecha,
                List.of(new DetallePedido(5L, 3, "Jugo", 2, new BigDecimal("5"), BigDecimal.TEN, "Frio")));

        PedidoData data = mapper.toPedidoData(pedido);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getNumeroPedido()).isEqualTo("PED-1");
        assertThat(data.getCedulaCliente()).isEqualTo("123");
        assertThat(data.getNombreCliente()).isEqualTo("Ana");
        assertThat(data.getEmailCliente()).isEqualTo("ana@example.com");
        assertThat(data.getMesa()).isEqualTo("2");
        assertThat(data.getUsuarioId()).isEqualTo(9L);
        assertThat(data.getMetodoPago()).isEqualTo("TARJETA");
        assertThat(data.getEstado()).isEqualTo("PENDIENTE");
        assertThat(data.getTotal()).isEqualByComparingTo("11");
        assertThat(data.getFechaCreacion()).isEqualTo(fecha);
        assertThat(data.getFechaActualizacion()).isEqualTo(fecha);
        assertThat(data.getDetalles()).singleElement()
                .satisfies(detalle -> {
                    assertThat(detalle.getPedido()).isSameAs(data);
                    assertThat(detalle.getId()).isEqualTo(5L);
                    assertThat(detalle.getProductoId()).isEqualTo(3L);
                    assertThat(detalle.getNombreProducto()).isEqualTo("Jugo");
                    assertThat(detalle.getCantidad()).isEqualTo(2);
                    assertThat(detalle.getPrecioUnitario()).isEqualByComparingTo("5");
                    assertThat(detalle.getSubtotal()).isEqualByComparingTo(BigDecimal.TEN);
                    assertThat(detalle.getNotas()).isEqualTo("Frio");
                });
    }

    @Test
    void convierteListasDeDetallesNulasComoVacias() {
        assertThat(mapper.toPedido(PedidoData.builder().detalles(null).build()).getDetalles()).isEmpty();
        assertThat(mapper.toPedidoData(Pedido.builder().detalles(null).build()).getDetalles()).isEmpty();
    }
}
