package com.auth.Auth.infrastructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cedula;

    private String nombre;

    private String apellido;

    private String email;

    private String username;

    private String password;

    private Integer edad;

    private String telefono;

    private String role;

    private String estado;
}
