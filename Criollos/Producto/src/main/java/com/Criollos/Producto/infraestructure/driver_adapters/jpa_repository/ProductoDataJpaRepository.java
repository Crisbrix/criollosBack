package com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductoDataJpaRepository extends JpaRepository<ProductoData, Integer> {
    Optional<ProductoData> findByNombre(String nombre);
    List<ProductoData> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT p FROM ProductoData p WHERE p.stock <= p.stockMinimo")
    List<ProductoData> findProductosBajoStock();
}
