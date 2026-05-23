package com.Criollos.Producto.infraestructure.mapper;

import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository.ProductoData;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toDomain(ProductoData data) {
        if (data == null) return null;

        Producto p = new Producto();
        p.setProductoId(data.getProductoId());
        p.setNombre(data.getNombre());
        p.setDescripcion(data.getDescripcion());
        p.setPrecio(data.getPrecio());
        p.setStock(data.getStock());
        p.setStockMinimo(data.getStockMinimo());
        p.setActivo(data.getActivo());
        p.setCategoria(data.getCategoria());
        return p;
    }

    public ProductoData toData(Producto domain) {
        if (domain == null) return null;

        ProductoData data = new ProductoData();
        data.setProductoId(domain.getProductoId());
        data.setNombre(domain.getNombre());
        data.setDescripcion(domain.getDescripcion());
        data.setPrecio(domain.getPrecio());
        data.setStock(domain.getStock());
        data.setStockMinimo(domain.getStockMinimo());
        data.setActivo(domain.getActivo());
        data.setCategoria(domain.getCategoria());
        return data;
    }
}