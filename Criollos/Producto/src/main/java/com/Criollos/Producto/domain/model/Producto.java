package com.Criollos.Producto.domain.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Producto {

    private Integer productoId;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean activo;
    private Long categoriaId;
}
