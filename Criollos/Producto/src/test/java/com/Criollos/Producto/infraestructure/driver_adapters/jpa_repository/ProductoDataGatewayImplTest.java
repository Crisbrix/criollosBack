package com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository;

import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.infraestructure.mapper.ProductoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoDataGatewayImplTest {

    @Mock
    private ProductoDataJpaRepository repository;

    @Mock
    private ProductoMapper mapper;

    @InjectMocks
    private ProductoDataGatewayImpl gateway;

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
        when(mapper.toData(producto)).thenReturn(productoData);
        when(repository.save(productoData)).thenReturn(productoData);
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.guardarProducto(producto);

        assertNotNull(resultado);
        assertEquals("Coca Cola", resultado.getNombre());
    }

    @Test
    void buscarProductoPorId_exitoso() {
        when(repository.findById(1)).thenReturn(Optional.of(productoData));
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.buscarProductoPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getProductoId());
    }

    @Test
    void buscarProductoPorId_noEncontrado_lanzaExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gateway.buscarProductoPorId(99));
    }

    @Test
    void obtenerTodosLosProductos_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(productoData));
        when(mapper.toDomain(productoData)).thenReturn(producto);

        List<Producto> resultado = gateway.obtenerTodosLosProductos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    void actualizarProducto_exitoso() {
        when(mapper.toData(producto)).thenReturn(productoData);
        when(repository.save(productoData)).thenReturn(productoData);
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.actualizarProducto(1, producto);

        assertNotNull(resultado);
        verify(repository, times(1)).save(productoData);
    }

    @Test
    void eliminarProductoPorId_exitoso() {
        doNothing().when(repository).deleteById(1);

        gateway.eliminarProductoPorId(1);

        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void reducirStock_exitoso() {
        productoData.setStock(100);
        when(repository.findById(1)).thenReturn(Optional.of(productoData));
        when(repository.save(productoData)).thenReturn(productoData);
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.reducirStock(1L, 10);

        assertNotNull(resultado);
        verify(repository, times(1)).save(productoData);
    }

    @Test
    void reducirStock_noEncontrado_lanzaExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gateway.reducirStock(99L, 10));
    }

    @Test
    void reponerStock_exitoso() {
        productoData.setStock(50);
        when(repository.findById(1)).thenReturn(Optional.of(productoData));
        when(repository.save(productoData)).thenReturn(productoData);
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.reponerStock(1L, 50);

        assertNotNull(resultado);
        verify(repository, times(1)).save(productoData);
    }

    @Test
    void reponerStock_noEncontrado_lanzaExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> gateway.reponerStock(99L, 50));
    }

    @Test
    void buscarPorNombre_retornaLista() {
        when(repository.findByNombreContainingIgnoreCase("Coca")).thenReturn(List.of(productoData));
        when(mapper.toDomain(productoData)).thenReturn(producto);

        List<Producto> resultado = gateway.buscarPorNombre("Coca");

        assertFalse(resultado.isEmpty());
    }

    @Test
    void obtenerProductosBajoStock_retornaLista() {
        when(repository.findProductosBajoStock()).thenReturn(List.of(productoData));
        when(mapper.toDomain(productoData)).thenReturn(producto);

        List<Producto> resultado = gateway.obtenerProductosBajoStock();

        assertFalse(resultado.isEmpty());
    }

    @Test
    void validarProductoPorNombre_encontrado() {
        when(repository.findByNombre("Coca Cola")).thenReturn(Optional.of(productoData));
        when(mapper.toDomain(productoData)).thenReturn(producto);

        Producto resultado = gateway.validarProductoPorNombre("Coca Cola");

        assertNotNull(resultado);
    }

    @Test
    void validarProductoPorNombre_noEncontrado() {
        when(repository.findByNombre("Inexistente")).thenReturn(Optional.empty());

        Producto resultado = gateway.validarProductoPorNombre("Inexistente");

        assertNull(resultado);
    }
}