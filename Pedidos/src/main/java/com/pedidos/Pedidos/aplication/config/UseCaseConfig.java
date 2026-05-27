package com.pedidos.Pedidos.aplication.config;

import com.pedidos.Pedidos.domain.model.gateway.PedidoGateway;
import com.pedidos.Pedidos.domain.useCase.PedidoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public PedidoUseCase pedidoUseCase(PedidoGateway pedidoGateway) {
        return new PedidoUseCase(pedidoGateway);
    }
}
