package com.Criollos.Producto.application.config;

import com.Criollos.Producto.domain.model.gateway.ProductoGateway;
import com.Criollos.Producto.domain.useCase.ProductoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class productoUseCaseConfigTest {

    @Mock
    private ProductoGateway productoGateway;

    @Test
    void productoUseCase_creaInstanciaCorrectamente() {
        productoUseCaseConfig config = new productoUseCaseConfig();
        ProductoUseCase useCase = config.productoUseCase(productoGateway);
        assertNotNull(useCase);
    }
}