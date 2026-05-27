package com.Criollos.Producto.infraestructure.entry_points;

import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.domain.useCase.ProductoUseCase;
import com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository.ProductoData;
import com.Criollos.Producto.infraestructure.mapper.ProductoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoUseCase productoUseCase;

    @Mock
    private ProductoMapper productoMapper;

    @InjectMocks
    private ProductoController productoController;

    private Producto producto;
    private ProductoData productoData;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setProductoId(1);
        producto.setNombre("Coca Cola");
        producto.setPrecio(3500.0);
        producto.setStock(100);
        producto.setStockMinimo(10);
        producto.setActivo(true);
        producto.setCategoria("Bebidas");

        productoData = new ProductoData();
        productoData.setNombre("Coca Cola");
        productoData.setPrecio(3500.0);
        productoData.setStock(100);
        productoData.setStockMinimo(10);
        productoData.setActivo(true);
        productoData.setCategoria("Bebidas");
    }

    @Test
    void guardarProducto_exitoso() {
        when(productoMapper.toDomain(productoData)).thenReturn(producto);
        when(productoUseCase.guardarProducto(producto)).thenReturn(producto);

        ResponseEntity<?> response = productoController.guardarProducto(productoData);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(producto, response.getBody());
    }

    @Test
    void guardarProducto_lanzaExcepcion_retornaMensajeError() {
        when(productoMapper.toDomain(productoData)).thenReturn(producto);
        when(productoUseCase.guardarProducto(producto))
                .thenThrow(new IllegalArgumentException("El nombre es obligatorio"));

        ResponseEntity<?> response = productoController.guardarProducto(productoData);

        assertEquals(200, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
        assertEquals("El nombre es obligatorio", body.get("mensaje"));
    }


    @Test
    void obtenerProductoPorId_exitoso() {
        when(productoUseCase.obtenerProductoPorId(1)).thenReturn(producto);

        ResponseEntity<?> response = productoController.obtenerProductoPorId(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(producto, response.getBody());
    }

    @Test
    void obtenerProductoPorId_lanzaExcepcion_retornaMensajeError() {
        when(productoUseCase.obtenerProductoPorId(99))
                .thenThrow(new RuntimeException("Producto no encontrado con id: 99"));

        ResponseEntity<?> response = productoController.obtenerProductoPorId(99);

        assertEquals(200, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
    }


    @Test
    void obtenerTodosLosProductos_retornaLista() {
        when(productoUseCase.obtenerTodosLosProductos()).thenReturn(List.of(producto));

        ResponseEntity<List<Producto>> response = productoController.obtenerTodosLosProductos();

        assertEquals(200, response.getStatusCode().value());
        assertFalse(response.getBody().isEmpty());
    }


    @Test
    void actualizarProducto_exitoso() {
        when(productoMapper.toDomain(productoData)).thenReturn(producto);
        when(productoUseCase.actualizarProducto(1, producto)).thenReturn(producto);

        ResponseEntity<?> response = productoController.actualizarProducto(1, productoData);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(producto, response.getBody());
    }

    @Test
    void actualizarProducto_lanzaExcepcion_retornaMensajeError() {
        when(productoMapper.toDomain(productoData)).thenReturn(producto);
        when(productoUseCase.actualizarProducto(1, producto))
                .thenThrow(new IllegalArgumentException("El precio debe ser mayor a 0"));

        ResponseEntity<?> response = productoController.actualizarProducto(1, productoData);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
        assertEquals("El precio debe ser mayor a 0", body.get("mensaje"));
    }


    @Test
    void eliminarProducto_exitoso() {
        doNothing().when(productoUseCase).eliminarProducto(1);

        ResponseEntity<?> response = productoController.eliminarProducto(1);

        assertEquals(200, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(true, body.get("success"));
        assertEquals("Producto eliminado correctamente", body.get("mensaje"));
    }

    @Test
    void eliminarProducto_lanzaExcepcion_retornaMensajeError() {
        doThrow(new RuntimeException("Producto no encontrado"))
                .when(productoUseCase).eliminarProducto(99);

        ResponseEntity<?> response = productoController.eliminarProducto(99);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
    }


    @Test
    void reducirStock_exitoso() {
        when(productoUseCase.reducirStock(1, 10)).thenReturn(producto);

        ResponseEntity<?> response = productoController.reducirStock(1, 10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(producto, response.getBody());
    }

    @Test
    void reducirStock_lanzaExcepcion_retornaMensajeError() {
        when(productoUseCase.reducirStock(1, 200))
                .thenThrow(new IllegalArgumentException("Stock insuficiente. Disponible: 100"));

        ResponseEntity<?> response = productoController.reducirStock(1, 200);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
    }


    @Test
    void reponerStock_exitoso() {
        when(productoUseCase.reponerStock(1, 50)).thenReturn(producto);

        ResponseEntity<?> response = productoController.reponerStock(1, 50);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(producto, response.getBody());
    }

    @Test
    void reponerStock_lanzaExcepcion_retornaMensajeError() {
        when(productoUseCase.reponerStock(1, -5))
                .thenThrow(new IllegalArgumentException("La cantidad debe ser mayor a 0"));

        ResponseEntity<?> response = productoController.reponerStock(1, -5);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
    }


    @Test
    void buscarPorNombre_exitoso() {
        when(productoUseCase.buscarProductoPorNombre("Coca")).thenReturn(List.of(producto));

        ResponseEntity<?> response = productoController.buscarPorNombre("Coca");

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void buscarPorNombre_lanzaExcepcion_retornaMensajeError() {
        when(productoUseCase.buscarProductoPorNombre(""))
                .thenThrow(new IllegalArgumentException("El nombre de búsqueda es obligatorio"));

        ResponseEntity<?> response = productoController.buscarPorNombre("");

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals(false, body.get("success"));
    }

    @Test
    void productosBajoStock_retornaLista() {
        when(productoUseCase.productosBajoStock()).thenReturn(List.of(producto));

        ResponseEntity<List<Producto>> response = productoController.productosBajoStock();

        assertFalse(response.getBody().isEmpty());
    }
}