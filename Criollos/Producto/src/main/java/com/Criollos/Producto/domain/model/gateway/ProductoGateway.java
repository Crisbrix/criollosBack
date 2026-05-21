package com.Criollos.Producto.domain.model.gateway;

import com.Criollos.Producto.domain.model.producto;

import java.util.List;

public interface ProductoGateway {

producto guardarProducto(producto producto);
producto buscarProductoPorId(Integer idProducto);
List<producto> obtenerTodosLosProductos();
producto actualizarProducto(Integer idProducto, producto producto);
void  eliminarProductoPorId(Integer idProducto);
producto reducirStock(Long id, Integer cantidad);
producto reponerStock(Long id, Integer cantidad);
List<producto> buscarPorNombre(String nombre);
List<producto> obtenerPorCategoria(Long categoriaId);
List<producto> obtenerProductosBajoStock();
producto validarProductoPorNombre(String nombre);
}
