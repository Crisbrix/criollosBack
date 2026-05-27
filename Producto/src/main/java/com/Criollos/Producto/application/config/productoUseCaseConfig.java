package com.Criollos.Producto.application.config;

import com.Criollos.Producto.domain.model.gateway.ProductoGateway;
import com.Criollos.Producto.domain.useCase.ProductoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class productoUseCaseConfig {

    @Bean
    public ProductoUseCase productoUseCase(ProductoGateway productoGateway){
        return new ProductoUseCase(productoGateway);
    }
}
