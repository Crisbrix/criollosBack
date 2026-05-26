package com.pedidos.Pedidos.domain.useCase;

import com.pedidos.Pedidos.domain.model.DetallePedido;
import com.pedidos.Pedidos.domain.model.EstadoPedido;
import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.domain.model.gateway.PedidoGateway;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoUseCaseTest {

    private final PedidoGateway gateway = mock(PedidoGateway.class);
    private final PedidoUseCase useCase = new PedidoUseCase(gateway);

    @Test
    void guardarPedidoValidaCalculaTotalesYDelega() {
        Pedido pedido = pedidoValido();
        pedido.setImpuesto(new BigDecimal("1.50"));
        when(gateway.guardarPedido(pedido)).thenReturn(pedido);

        Pedido resultado = useCase.guardarPedido(pedido);

        assertThat(resultado).isSameAs(pedido);
        assertThat(pedido.getNumeroPedido()).startsWith("PED-");
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.PENDIENTE.name());
        assertThat(pedido.getFechaCreacion()).isNotNull();
        assertThat(pedido.getFechaActualizacion()).isNotNull();
        assertThat(pedido.getSubtotal()).isEqualByComparingTo("20.00");
        assertThat(pedido.getTotal()).isEqualByComparingTo("21.50");
        assertThat(pedido.getDetalles().getFirst().getSubtotal()).isEqualByComparingTo("20.00");
        verify(gateway).guardarPedido(pedido);
    }

    @Test
    void actualizarPedidoValidaEstadoCalculaTotalesYDelega() {
        Pedido pedido = pedidoValido();
        pedido.setEstado(EstadoPedido.LISTO.name());
        when(gateway.actualizarPedido("PED-1", pedido)).thenReturn(pedido);

        Pedido resultado = useCase.actualizarPedido("PED-1", pedido);

        assertThat(resultado).isSameAs(pedido);
        assertThat(pedido.getFechaActualizacion()).isNotNull();
        assertThat(pedido.getImpuesto()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(pedido.getTotal()).isEqualByComparingTo("20.00");
        verify(gateway).actualizarPedido("PED-1", pedido);
    }

    @Test
    void consultasYEliminacionDeleganEnGateway() {
        Pedido pedido = pedidoValido();
        List<Pedido> pedidos = List.of(pedido);
        when(gateway.buscarPedidoPorNumero("PED-1")).thenReturn(pedido);
        when(gateway.listarPedidos()).thenReturn(pedidos);
        when(gateway.listarPedidosPorEstado(EstadoPedido.PENDIENTE.name())).thenReturn(pedidos);
        when(gateway.actualizarEstadoPedido("PED-1", EstadoPedido.ENTREGADO.name())).thenReturn(pedido);

        assertThat(useCase.buscarPedidoPorNumero("PED-1")).isSameAs(pedido);
        assertThat(useCase.listarPedidos()).isSameAs(pedidos);
        assertThat(useCase.listarPedidosPorEstado(EstadoPedido.PENDIENTE.name())).isSameAs(pedidos);
        assertThat(useCase.actualizarEstadoPedido("PED-1", EstadoPedido.ENTREGADO.name())).isSameAs(pedido);

        useCase.eliminarPedidoPorNumero("PED-1");
        verify(gateway).eliminarPedidoPorNumero("PED-1");
    }

    @Test
    void rechazaPedidosSinDetalles() {
        assertThatThrownBy(() -> useCase.guardarPedido(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El pedido debe tener al menos un producto");

        assertThatThrownBy(() -> useCase.guardarPedido(Pedido.builder().detalles(null).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El pedido debe tener al menos un producto");

        assertThatThrownBy(() -> useCase.guardarPedido(Pedido.builder().detalles(List.of()).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El pedido debe tener al menos un producto");
    }

    @Test
    void rechazaDetallesInvalidos() {
        assertDetalleInvalido(DetallePedido.builder().nombreProducto("Arepa").cantidad(1).precioUnitario(BigDecimal.ONE).build(),
                "Cada detalle debe tener productoId y nombreProducto");
        assertDetalleInvalido(DetallePedido.builder().productoId(1).cantidad(1).precioUnitario(BigDecimal.ONE).build(),
                "Cada detalle debe tener productoId y nombreProducto");
        assertDetalleInvalido(DetallePedido.builder().productoId(1).nombreProducto("Arepa").precioUnitario(BigDecimal.ONE).build(),
                "La cantidad del producto debe ser mayor a cero");
        assertDetalleInvalido(DetallePedido.builder().productoId(1).nombreProducto("Arepa").cantidad(0).precioUnitario(BigDecimal.ONE).build(),
                "La cantidad del producto debe ser mayor a cero");
        assertDetalleInvalido(DetallePedido.builder().productoId(1).nombreProducto("Arepa").cantidad(1).build(),
                "El precio unitario no puede ser negativo");
        assertDetalleInvalido(DetallePedido.builder().productoId(1).nombreProducto("Arepa").cantidad(1).precioUnitario(new BigDecimal("-1")).build(),
                "El precio unitario no puede ser negativo");
    }

    @Test
    void rechazaEstadosInvalidos() {
        assertThatThrownBy(() -> useCase.listarPedidosPorEstado(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El estado del pedido es obligatorio");

        assertThatThrownBy(() -> useCase.actualizarEstadoPedido("PED-1", " "))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El estado del pedido es obligatorio");

        assertThatThrownBy(() -> useCase.listarPedidosPorEstado("NUEVO"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private void assertDetalleInvalido(DetallePedido detalle, String mensaje) {
        assertThatThrownBy(() -> useCase.guardarPedido(Pedido.builder().detalles(List.of(detalle)).build()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(mensaje);
    }

    private Pedido pedidoValido() {
        return Pedido.builder()
                .cedulaCliente("123")
                .nombreCliente("Ana")
                .mesa("1")
                .usuarioId(7L)
                .metodoPago("EFECTIVO")
                .detalles(List.of(DetallePedido.builder()
                        .productoId(1)
                        .nombreProducto("Arepa")
                        .cantidad(2)
                        .precioUnitario(new BigDecimal("10.00"))
                        .notas("Sin salsa")
                        .build()))
                .build();
    }
}
