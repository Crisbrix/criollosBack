package com.pedidos.Pedidos.aplication.config;

import com.pedidos.Pedidos.domain.model.gateway.PedidoGateway;
import com.pedidos.Pedidos.domain.useCase.PedidoUseCase;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCaseConfigTest {

    @Test
    void creaPedidoUseCaseConGateway() {
        PedidoUseCase useCase = new UseCaseConfig().pedidoUseCase(mock(PedidoGateway.class));

        assertThat(useCase).isNotNull();
    }
}
