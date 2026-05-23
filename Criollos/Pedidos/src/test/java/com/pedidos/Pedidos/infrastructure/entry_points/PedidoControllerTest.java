package com.pedidos.Pedidos.infrastructure.entry_points;

import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.domain.useCase.PedidoUseCase;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.PedidoData;
import com.pedidos.Pedidos.infrastructure.mapper.PedidoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoControllerTest {

    private final PedidoUseCase useCase = mock(PedidoUseCase.class);
    private final PedidoController controller = new PedidoController(useCase, new PedidoMapper());

    @Test
    void guardarPedidoRetornaPedidoGuardado() {
        Pedido pedido = pedido();
        when(useCase.guardarPedido(any(Pedido.class))).thenReturn(pedido);

        ResponseEntity<Pedido> response = controller.guardarPedido(data());

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(pedido);
    }

    @Test
    void obtenerPedidoPorNumeroRetornaPedido() {
        Pedido pedido = pedido();
        when(useCase.buscarPedidoPorNumero("PED-1")).thenReturn(pedido);

        assertThat(controller.obtenerPedidoPorNumero("PED-1").getBody()).isSameAs(pedido);
    }

    @Test
    void listarPedidosRetornaLista() {
        List<Pedido> pedidos = List.of(pedido());
        when(useCase.listarPedidos()).thenReturn(pedidos);

        assertThat(controller.listarPedidos().getBody()).isSameAs(pedidos);
    }

    @Test
    void listarPedidosPorEstadoRetornaLista() {
        List<Pedido> pedidos = List.of(pedido());
        when(useCase.listarPedidosPorEstado("PENDIENTE")).thenReturn(pedidos);

        assertThat(controller.listarPedidosPorEstado("PENDIENTE").getBody()).isSameAs(pedidos);
    }

    @Test
    void actualizarPedidoRetornaActualizado() {
        Pedido pedido = pedido();
        when(useCase.actualizarPedido(eq("PED-1"), any(Pedido.class))).thenReturn(pedido);

        assertThat(controller.actualizarPedido("PED-1", data()).getBody()).isSameAs(pedido);
    }

    @Test
    void actualizarEstadoPedidoUsaEstadoDelBody() {
        Pedido pedido = pedido();
        when(useCase.actualizarEstadoPedido("PED-1", "LISTO")).thenReturn(pedido);

        assertThat(controller.actualizarEstadoPedido("PED-1", Map.of("estado", "LISTO")).getBody()).isSameAs(pedido);
    }

    @Test
    void eliminarPedidoRetornaMensaje() {
        ResponseEntity<String> response = controller.eliminarPedido("PED-1");

        assertThat(response.getBody()).isEqualTo("Pedido eliminado correctamente");
        verify(useCase).eliminarPedidoPorNumero("PED-1");
    }

    private Pedido pedido() {
        return Pedido.builder().numeroPedido("PED-1").build();
    }

    private PedidoData data() {
        return PedidoData.builder().numeroPedido("PED-1").detalles(List.of()).build();
    }
}
