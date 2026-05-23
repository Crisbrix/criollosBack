package com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository;

import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.infrastructure.mapper.PedidoMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoDataGatewayImplTest {

    private final PedidoDataJpaRepository repository = mock(PedidoDataJpaRepository.class);
    private final PedidoDataGatewayImpl gateway = new PedidoDataGatewayImpl(repository, new PedidoMapper());

    @Test
    void guardarPedidoPersisteEntidadMapeada() {
        when(repository.save(any(PedidoData.class))).thenReturn(data("PED-1"));

        Pedido resultado = gateway.guardarPedido(pedido("PED-1"));

        assertThat(resultado.getNumeroPedido()).isEqualTo("PED-1");
        assertThat(resultado.getDetalles()).singleElement()
                .satisfies(detalle -> assertThat(detalle.getNombreProducto()).isEqualTo("Arepa"));
    }

    @Test
    void buscarPedidoPorNumeroRetornaPedidoOError() {
        when(repository.findByNumeroPedido("PED-1")).thenReturn(Optional.of(data("PED-1")));
        when(repository.findByNumeroPedido("PED-X")).thenReturn(Optional.empty());

        assertThat(gateway.buscarPedidoPorNumero("PED-1").getNumeroPedido()).isEqualTo("PED-1");
        assertThatThrownBy(() -> gateway.buscarPedidoPorNumero("PED-X"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido no encontrado");
    }

    @Test
    void listarPedidosMapeaTodosLosRegistros() {
        when(repository.findAll()).thenReturn(List.of(data("PED-1"), data("PED-2")));

        assertThat(gateway.listarPedidos())
                .extracting(Pedido::getNumeroPedido)
                .containsExactly("PED-1", "PED-2");
    }

    @Test
    void listarPedidosPorEstadoMapeaRegistrosDelEstado() {
        when(repository.findByEstado("PENDIENTE")).thenReturn(List.of(data("PED-1")));

        assertThat(gateway.listarPedidosPorEstado("PENDIENTE"))
                .singleElement()
                .extracting(Pedido::getNumeroPedido)
                .isEqualTo("PED-1");
    }

    @Test
    void eliminarPedidoValidaExistencia() {
        when(repository.existsByNumeroPedido("PED-1")).thenReturn(true);
        when(repository.existsByNumeroPedido("PED-X")).thenReturn(false);

        gateway.eliminarPedidoPorNumero("PED-1");

        verify(repository).deleteByNumeroPedido("PED-1");
        assertThatThrownBy(() -> gateway.eliminarPedidoPorNumero("PED-X"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido no encontrado");
    }

    @Test
    void actualizarPedidoModificaCamposDetallesYGuarda() {
        PedidoData existente = data("PED-1");
        existente.setDetalles(new ArrayList<>(existente.getDetalles()));
        Pedido cambios = pedido("PED-1");
        cambios.setNombreCliente("Laura");
        when(repository.findByNumeroPedido("PED-1")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Pedido resultado = gateway.actualizarPedido("PED-1", cambios);

        assertThat(resultado.getNombreCliente()).isEqualTo("Laura");
        assertThat(existente.getDetalles()).singleElement()
                .satisfies(detalle -> {
                    assertThat(detalle.getId()).isNull();
                    assertThat(detalle.getPedido()).isSameAs(existente);
                });
    }

    @Test
    void actualizarPedidoInicializaDetallesSiSonNulosYLanzaSiNoExiste() {
        PedidoData existente = data("PED-1");
        existente.setDetalles(null);
        when(repository.findByNumeroPedido("PED-1")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);
        when(repository.findByNumeroPedido("PED-X")).thenReturn(Optional.empty());

        assertThat(gateway.actualizarPedido("PED-1", pedido("PED-1")).getDetalles()).hasSize(1);
        assertThatThrownBy(() -> gateway.actualizarPedido("PED-X", pedido("PED-X")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido no encontrado");
    }

    @Test
    void actualizarEstadoPedidoActualizaFechaYLanzaSiNoExiste() {
        PedidoData existente = data("PED-1");
        when(repository.findByNumeroPedido("PED-1")).thenReturn(Optional.of(existente));
        when(repository.findByNumeroPedido("PED-X")).thenReturn(Optional.empty());
        when(repository.save(existente)).thenReturn(existente);

        Pedido resultado = gateway.actualizarEstadoPedido("PED-1", "LISTO");

        assertThat(resultado.getEstado()).isEqualTo("LISTO");
        assertThat(resultado.getFechaActualizacion()).isNotNull();
        assertThatThrownBy(() -> gateway.actualizarEstadoPedido("PED-X", "LISTO"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Pedido no encontrado");
    }

    private Pedido pedido(String numero) {
        return Pedido.builder()
                .numeroPedido(numero)
                .cedulaCliente("123")
                .nombreCliente("Ana")
                .mesa("1")
                .usuarioId(2L)
                .metodoPago("EFECTIVO")
                .estado("PENDIENTE")
                .subtotal(BigDecimal.TEN)
                .impuesto(BigDecimal.ONE)
                .total(new BigDecimal("11"))
                .detalles(List.of(com.pedidos.Pedidos.domain.model.DetallePedido.builder()
                        .id(5L)
                        .productoId(9L)
                        .nombreProducto("Arepa")
                        .cantidad(1)
                        .precioUnitario(BigDecimal.TEN)
                        .subtotal(BigDecimal.TEN)
                        .notas("Sin queso")
                        .build()))
                .build();
    }

    private PedidoData data(String numero) {
        PedidoData pedidoData = PedidoData.builder()
                .id(1L)
                .numeroPedido(numero)
                .cedulaCliente("123")
                .nombreCliente("Ana")
                .mesa("1")
                .usuarioId(2L)
                .metodoPago("EFECTIVO")
                .estado("PENDIENTE")
                .subtotal(BigDecimal.TEN)
                .impuesto(BigDecimal.ONE)
                .total(new BigDecimal("11"))
                .build();
        pedidoData.setDetalles(List.of(new DetallePedidoData(5L, 9L, "Arepa", 1, BigDecimal.TEN, BigDecimal.TEN,
                "Sin queso", pedidoData)));
        return pedidoData;
    }
}
