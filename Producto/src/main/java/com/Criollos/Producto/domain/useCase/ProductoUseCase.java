package com.Criollos.Producto.domain.useCase;

import com.Criollos.Producto.domain.model.gateway.ProductoGateway;
import com.Criollos.Producto.domain.model.Producto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoUseCase {
 private final ProductoGateway productoGateway;

 public Producto guardarProducto(Producto producto){

  if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
   throw new IllegalArgumentException("El nombre es obligatorio");
  }
  if (producto.getNombre().trim().length() < 3) {
   throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
  }
  if (producto.getPrecio() == null) {
   throw new IllegalArgumentException("El precio es obligatorio");
  }
  if (producto.getPrecio() <= 0) {
   throw new IllegalArgumentException("El precio debe ser mayor a 0");
  }
  if(producto.getStock() == null) {
   throw new IllegalArgumentException("El stock es obligatorio");
  }
  if(producto.getStock() < 0) {
   throw new IllegalArgumentException("El stock no debe ser negativo");
  }
  if (producto.getDescripcion() != null && producto.getDescripcion().length() > 255) {
   throw new IllegalArgumentException("La descripción es demasiado larga");
  }
  if (productoGateway.validarProductoPorNombre(producto.getNombre()) != null) {
   throw new IllegalArgumentException("Ya existe un producto con ese nombre");
  }
  if (producto.getCategoria() == null || producto.getCategoria().trim().isEmpty()) {
   throw new IllegalArgumentException("La categoría es obligatoria");
  }
  if (producto.getCategoria().trim().matches(".*\\d.*")) {
   throw new IllegalArgumentException("La categoría no debe contener números");
  }
  if (producto.getStockMinimo() == null) {
   throw new IllegalArgumentException("El stock mínimo es obligatorio");
  }
  if (producto.getStockMinimo() < 0) {
   throw new IllegalArgumentException("El stock mínimo no debe ser negativo");
  }
  if (producto.getActivo() == null) {
   throw new IllegalArgumentException("El estado activo es obligatorio");
  }
  return productoGateway.guardarProducto(producto);
 }

 public Producto obtenerProductoPorId(Integer idProducto){
  try {
   return productoGateway.buscarProductoPorId(idProducto);
  } catch (Exception e) {
   System.out.println(e.getMessage());
   return new Producto();
  }
 }

 public List<Producto> obtenerTodosLosProductos(){

  return productoGateway.obtenerTodosLosProductos();
 }

 public Producto actualizarProducto(Integer id, Producto producto) {
  obtenerProductoPorId(id);
  if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
   throw new IllegalArgumentException("El nombre es obligatorio");
  }
  if (producto.getPrecio() == null || producto.getPrecio() <= 0) {
   throw new IllegalArgumentException("El precio debe ser mayor a 0");
  }
  if (producto.getStock() == null || producto.getStock() < 0) {
   throw new IllegalArgumentException("El stock no debe ser negativo");
  }

  producto.setProductoId(id);
  return productoGateway.actualizarProducto(id, producto);
 }

 public void eliminarProducto(Integer productoId){
  productoGateway.eliminarProductoPorId(productoId);
 }

 public Producto reducirStock(Integer id, Integer cantidad) {
  if (cantidad == null || cantidad <= 0) {
   throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
  }
  Producto p = obtenerProductoPorId(id);
  if (p.getStock() < cantidad) {
   throw new IllegalArgumentException("Stock insuficiente. Disponible: " + p.getStock());
  }
  return productoGateway.reducirStock(id.longValue(), cantidad);
 }

 public Producto reponerStock(Integer productoId, Integer cantidad){
  if (cantidad == null || cantidad <= 0) {
   throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
  }
  obtenerProductoPorId(productoId);
  return productoGateway.reponerStock(productoId.longValue(), cantidad);
 }

 public List<Producto> buscarProductoPorNombre(String nombre){
  if (nombre == null || nombre.trim().isEmpty()) {
   throw new IllegalArgumentException("El nombre de búsqueda es obligatorio");
  }
  return productoGateway.buscarPorNombre(nombre);
 }

 public List<Producto> productosBajoStock(){

  return productoGateway.obtenerProductosBajoStock();
 }
}