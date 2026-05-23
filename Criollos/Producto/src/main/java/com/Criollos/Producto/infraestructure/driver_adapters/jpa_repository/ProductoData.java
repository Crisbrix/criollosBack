package com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto")
@Data
public class ProductoData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productoId;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    private Boolean activo;

    @Column(name = "categoria_id")
    private Long categoriaId;
}