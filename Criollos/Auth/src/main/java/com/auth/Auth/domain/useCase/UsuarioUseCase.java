package com.auth.Auth.domain.useCase;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.domain.model.gateway.EncrypterGateway;
import com.auth.Auth.domain.model.gateway.UsuarioGateWay;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private final UsuarioGateWay usuarioGateWay;
    private final EncrypterGateway encrypterGateway;

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getEmail() == null || usuario.getPassword() == null) {
            throw new NullPointerException("El email o password no puede ser nulo - guardarUsuario");
        }

        String passEncrypt = encrypterGateway.encrypt(usuario.getPassword());
        usuario.setPassword(passEncrypt);

        return usuarioGateWay.guardarUsuario(usuario);
    }

    public Usuario buscarUsuarioPorEmail(String cedula) {
        try {
            return usuarioGateWay.buscarUsuarioPorCedula(cedula);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new Usuario();
        }
    }

    public void eliminarUsuarioPorEmail(String cedula) {
        try {
            usuarioGateWay.eliminarUsuarioPorEmail(cedula);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public Usuario actualizarUsuario(String cedula, Usuario usuario) {
        return usuarioGateWay.actualizarUsuario(cedula, usuario);
    }

    public Usuario login(String email, String password) {
        if (email == null || password == null) {
            throw new RuntimeException("Email y password son obligatorios");
        }
        if (!email.contains("@")) {
            throw new RuntimeException("El email no tiene el @");
        }

        Usuario usuario = usuarioGateWay.buscarPorEmail(email);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (!encrypterGateway.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Contrasena incorrecta");
        }

        return usuario;
    }
}
