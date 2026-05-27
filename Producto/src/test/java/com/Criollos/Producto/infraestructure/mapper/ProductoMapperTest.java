package com.Criollos.Producto.infraestructure.mapper;

import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository.ProductoData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductoMapperTest {

    private ProductoMapper mapper;
    private Producto producto;
    private ProductoData productoData;

    @BeforeEach
    void setUp() {
        mapper = new ProductoMapper();

        producto = new Producto();
        producto.setProductoId(1);
        producto.setNombre("Coca Cola");
        producto.setDescripcion("Bebida gaseosa");
        producto.setPrecio(3500.0);
        producto.setStock(100);
        producto.setStockMinimo(10);
        producto.setActivo(true);
        producto.setCategoria("Bebidas");

        productoData = new ProductoData();
        productoData.setProductoId(1);
        productoData.setNombre("Coca Cola");
        productoData.setDescripcion("Bebida gaseosa");
        productoData.setPrecio(3500.0);
        productoData.setStock(100);
        productoData.setStockMinimo(10);
        productoData.setActivo(true);
        productoData.setCategoria("Bebidas");
    }


    @Test
    void toDomain_exitoso() {
        Producto resultado = mapper.toDomain(productoData);

        assertNotNull(resultado);
        assertEquals(productoData.getProductoId(), resultado.getProductoId());
        assertEquals(productoData.getNombre(), resultado.getNombre());
        assertEquals(productoData.getDescripcion(), resultado.getDescripcion());
        assertEquals(productoData.getPrecio(), resultado.getPrecio());
        assertEquals(productoData.getStock(), resultado.getStock());
        assertEquals(productoData.getStockMinimo(), resultado.getStockMinimo());
        assertEquals(productoData.getActivo(), resultado.getActivo());
        assertEquals(productoData.getCategoria(), resultado.getCategoria());
    }

    @Test
    void toDomain_dataNull_retornaNull() {
        Producto resultado = mapper.toDomain(null);
        assertNull(resultado);
    }

    @Test
    void toData_exitoso() {
        ProductoData resultado = mapper.toData(producto);

        assertNotNull(resultado);
        assertEquals(producto.getProductoId(), resultado.getProductoId());
        assertEquals(producto.getNombre(), resultado.getNombre());
        assertEquals(producto.getDescripcion(), resultado.getDescripcion());
        assertEquals(producto.getPrecio(), resultado.getPrecio());
        assertEquals(producto.getStock(), resultado.getStock());
        assertEquals(producto.getStockMinimo(), resultado.getStockMinimo());
        assertEquals(producto.getActivo(), resultado.getActivo());
        assertEquals(producto.getCategoria(), resultado.getCategoria());
    }

    @Test
    void toData_dominioNull_retornaNull() {
        ProductoData resultado = mapper.toData(null);
        assertNull(resultado);
    }
}