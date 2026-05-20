package com.auth.Auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Usuario {

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