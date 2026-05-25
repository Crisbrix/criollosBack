package com.Criollos.Producto.domain.model.gateway;

import com.Criollos.Producto.domain.model.Producto;

import java.util.List;

public interface ProductoGateway {

Producto guardarProducto(Producto producto);
Producto buscarProductoPorId(Integer idProducto);
List<Producto> obtenerTodosLosProductos();
Producto actualizarProducto(Integer idProducto, Producto producto);
void  eliminarProductoPorId(Integer idProducto);
Producto reducirStock(Long id, Integer cantidad);
Producto reponerStock(Long id, Integer cantidad);
List<Producto> buscarPorNombre(String nombre);
List<Producto> obtenerProductosBajoStock();
Producto validarProductoPorNombre(String nombre);
}
