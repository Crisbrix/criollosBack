package com.auth.Auth.infrastructure.mapper;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {
    public Usuario toUsuario(UsuarioData usuarioData) {
        return new Usuario(
                usuarioData.getId(),
                usuarioData.getCedula(),
                usuarioData.getNombre(),
                usuarioData.getApellido(),
                usuarioData.getEmail(),
                usuarioData.getUsername(),
                usuarioData.getPassword(),
                usuarioData.getEdad(),
                usuarioData.getTelefono(),
                usuarioData.getRole(),
                usuarioData.getEstado()
        );
    }

    public UsuarioData toUsuarioData(Usuario usuario) {
        return new UsuarioData(
                usuario.getId(),
                usuario.getCedula(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getEdad(),
                usuario.getTelefono(),
                usuario.getRole(),
                usuario.getEstado()
        );
    }
}
