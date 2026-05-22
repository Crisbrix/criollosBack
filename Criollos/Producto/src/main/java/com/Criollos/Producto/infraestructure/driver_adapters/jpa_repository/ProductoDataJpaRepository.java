package com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductoDataJpaRepository extends JpaRepository<ProductoData, Integer> {
    Optional<ProductoData> findByNombre(String nombre);
    Optional<ProductoData> findBySku(String sku);
    Optional<ProductoData> findByCodigoBarras(String codigoBarras);
    List<ProductoData> findByNombreContainingIgnoreCase(String nombre);
    List<ProductoData> findByCategoriaId(Long categoriaId);

    @Query("SELECT p FROM ProductoData p WHERE p.stock <= p.stockMinimo AND p.activo = true")
    List<ProductoData> findProductosBajoStock();
}
