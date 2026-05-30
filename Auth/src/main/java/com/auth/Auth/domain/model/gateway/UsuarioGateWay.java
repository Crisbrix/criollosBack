package com.auth.Auth.domain.model.gateway;

import com.auth.Auth.domain.model.Usuario;

import java.util.List;

public interface UsuarioGateWay{
    Usuario guardarUsuario(Usuario usuario);
    Usuario buscarUsuarioPorCedula(String cedula);
    void eliminarUsuarioPorEmail(String cedula);
    Usuario actualizarUsuario(String cedula, Usuario usuario);
    Usuario buscarPorEmail(String email);
    List<Usuario> listarUsuarios();
}