package com.Criollos.Producto.domain.useCase;

import com.Criollos.Producto.domain.model.gateway.ProductoGateway;
import com.Criollos.Producto.domain.model.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoUseCaseTest {

    @Mock
    private ProductoGateway productoGateway;

    @InjectMocks
    private ProductoUseCase productoUseCase;

    private Producto productoValido;

    @BeforeEach
    void setUp() {
        productoValido = new Producto();
        productoValido.setProductoId(1);
        productoValido.setNombre("Coca Cola");
        productoValido.setDescripcion("Bebida gaseosa");
        productoValido.setPrecio(3500.0);
        productoValido.setStock(100);
        productoValido.setStockMinimo(10);
        productoValido.setActivo(true);
        productoValido.setCategoria("Bebidas");
    }

    @Test
    void guardarProducto_exitoso() {
        when(productoGateway.validarProductoPorNombre("Coca Cola")).thenReturn(null);
        when(productoGateway.guardarProducto(productoValido)).thenReturn(productoValido);

        Producto resultado = productoUseCase.guardarProducto(productoValido);

        assertNotNull(resultado);
        assertEquals("Coca Cola", resultado.getNombre());
        verify(productoGateway, times(1)).guardarProducto(productoValido);
    }

    @Test
    void guardarProducto_nombreNulo_lanzaExcepcion() {
        productoValido.setNombre(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El nombre es obligatorio", ex.getMessage());
        verify(productoGateway, never()).guardarProducto(any());
    }

    @Test
    void guardarProducto_nombreVacio_lanzaExcepcion() {
        productoValido.setNombre("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    void guardarProducto_nombreMenosDeTresCaracteres_lanzaExcepcion() {
        productoValido.setNombre("AB");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El nombre debe tener al menos 3 caracteres", ex.getMessage());
    }

    @Test
    void guardarProducto_precioNulo_lanzaExcepcion() {
        productoValido.setPrecio(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El precio es obligatorio", ex.getMessage());
    }

    @Test
    void guardarProducto_precioMenorOIgualACero_lanzaExcepcion() {
        productoValido.setPrecio(-1.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El precio debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void guardarProducto_stockNulo_lanzaExcepcion() {
        productoValido.setStock(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El stock es obligatorio", ex.getMessage());
    }

    @Test
    void guardarProducto_stockNegativo_lanzaExcepcion() {
        productoValido.setStock(-5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El stock no debe ser negativo", ex.getMessage());
    }

    @Test
    void guardarProducto_stockMinimoNulo_lanzaExcepcion() {
        productoValido.setStockMinimo(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El stock mínimo es obligatorio", ex.getMessage());
    }

    @Test
    void guardarProducto_stockMinimoNegativo_lanzaExcepcion() {
        productoValido.setStockMinimo(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El stock mínimo no debe ser negativo", ex.getMessage());
    }

    @Test
    void guardarProducto_activoNulo_lanzaExcepcion() {
        productoValido.setActivo(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("El estado activo es obligatorio", ex.getMessage());
    }

    @Test
    void guardarProducto_descripcionMayorA255_lanzaExcepcion() {
        productoValido.setDescripcion("a".repeat(256));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("La descripción es demasiado larga", ex.getMessage());
    }

    @Test
    void guardarProducto_categoriaNula_lanzaExcepcion() {
        productoValido.setCategoria(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("La categoría es obligatoria", ex.getMessage());
    }

    @Test
    void guardarProducto_categoriaConNumeros_lanzaExcepcion() {
        productoValido.setCategoria("Bebidas123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("La categoría no debe contener números", ex.getMessage());
    }

    @Test
    void guardarProducto_nombreDuplicado_lanzaExcepcion() {
        when(productoGateway.validarProductoPorNombre("Coca Cola")).thenReturn(productoValido);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.guardarProducto(productoValido));

        assertEquals("Ya existe un producto con ese nombre", ex.getMessage());
        verify(productoGateway, never()).guardarProducto(any());
    }


    @Test
    void obtenerProductoPorId_exitoso() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);

        Producto resultado = productoUseCase.obtenerProductoPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getProductoId());
    }

    @Test
    void obtenerProductoPorId_noEncontrado_retornaVacio() {
        when(productoGateway.buscarProductoPorId(99))
                .thenThrow(new RuntimeException("Producto no encontrado con id: 99"));

        Producto resultado = productoUseCase.obtenerProductoPorId(99);

        assertNotNull(resultado);
        assertNull(resultado.getNombre());
    }

    @Test
    void obtenerTodosLosProductos_retornaLista() {
        when(productoGateway.obtenerTodosLosProductos()).thenReturn(List.of(productoValido));

        List<Producto> resultado = productoUseCase.obtenerTodosLosProductos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void actualizarProducto_exitoso() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        when(productoGateway.actualizarProducto(1, productoValido)).thenReturn(productoValido);

        Producto resultado = productoUseCase.actualizarProducto(1, productoValido);

        assertNotNull(resultado);
        verify(productoGateway, times(1)).actualizarProducto(1, productoValido);
    }

    @Test
    void actualizarProducto_nombreInvalido_lanzaExcepcion() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        productoValido.setNombre(null);

        assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.actualizarProducto(1, productoValido));
    }

    @Test
    void actualizarProducto_precioInvalido_lanzaExcepcion() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        productoValido.setPrecio(0.0);

        assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.actualizarProducto(1, productoValido));
    }

    @Test
    void actualizarProducto_stockNegativo_lanzaExcepcion() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        productoValido.setStock(-1);

        assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.actualizarProducto(1, productoValido));
    }

    @Test
    void eliminarProducto_exitoso() {
        doNothing().when(productoGateway).eliminarProductoPorId(1);

        productoUseCase.eliminarProducto(1);

        verify(productoGateway, times(1)).eliminarProductoPorId(1);
    }

    @Test
    void reducirStock_exitoso() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        when(productoGateway.reducirStock(1L, 10)).thenReturn(productoValido);

        Producto resultado = productoUseCase.reducirStock(1, 10);

        assertNotNull(resultado);
        verify(productoGateway, times(1)).reducirStock(1L, 10);
    }

    @Test
    void reducirStock_cantidadNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.reducirStock(1, null));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void reducirStock_cantidadMenorACero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.reducirStock(1, -5));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void reducirStock_stockInsuficiente_lanzaExcepcion() {
        productoValido.setStock(5);
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.reducirStock(1, 10));

        assertEquals("Stock insuficiente. Disponible: 5", ex.getMessage());
    }

    @Test
    void reponerStock_exitoso() {
        when(productoGateway.buscarProductoPorId(1)).thenReturn(productoValido);
        when(productoGateway.reponerStock(1L, 50)).thenReturn(productoValido);

        Producto resultado = productoUseCase.reponerStock(1, 50);

        assertNotNull(resultado);
        verify(productoGateway, times(1)).reponerStock(1L, 50);
    }

    @Test
    void reponerStock_cantidadNula_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.reponerStock(1, null));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void reponerStock_cantidadMenorACero_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.reponerStock(1, 0));

        assertEquals("La cantidad debe ser mayor a 0", ex.getMessage());
    }

    @Test
    void buscarProductoPorNombre_exitoso() {
        when(productoGateway.buscarPorNombre("Coca")).thenReturn(List.of(productoValido));

        List<Producto> resultado = productoUseCase.buscarProductoPorNombre("Coca");

        assertFalse(resultado.isEmpty());
    }

    @Test
    void buscarProductoPorNombre_nombreNulo_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.buscarProductoPorNombre(null));

        assertEquals("El nombre de búsqueda es obligatorio", ex.getMessage());
    }

    @Test
    void buscarProductoPorNombre_nombreVacio_lanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> productoUseCase.buscarProductoPorNombre("  "));

        assertEquals("El nombre de búsqueda es obligatorio", ex.getMessage());
    }

    @Test
    void productosBajoStock_retornaLista() {
        when(productoGateway.obtenerProductosBajoStock()).thenReturn(List.of(productoValido));

        List<Producto> resultado = productoUseCase.productosBajoStock();

        assertFalse(resultado.isEmpty());
        verify(productoGateway, times(1)).obtenerProductosBajoStock();
    }
}