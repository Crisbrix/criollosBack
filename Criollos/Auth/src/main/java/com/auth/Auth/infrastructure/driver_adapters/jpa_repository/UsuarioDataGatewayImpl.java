package com.auth.Auth.infrastructure.driver_adapters.jpa_repository;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.domain.model.gateway.UsuarioGateWay;
import com.auth.Auth.infrastructure.mapper.UsuarioMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UsuarioDataGatewayImpl implements UsuarioGateWay {

    private final UsuarioDataJpaRepository repository;
    private final UsuarioMapper mapper;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        UsuarioData usuarioData = mapper.toUsuarioData(usuario);
        return mapper.toUsuario(repository.save(usuarioData));
    }

    @Override
    public Usuario buscarUsuarioPorCedula(String cedula) {
        return repository.findByCedula(cedula)
                .map(mapper::toUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional
    public void eliminarUsuarioPorEmail(String cedula) {
        if (!repository.existsByCedula(cedula)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repository.deleteByCedula(cedula);
    }

    @Override
    public Usuario actualizarUsuario(String cedula, Usuario usuario) {
        UsuarioData existente = repository.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        existente.setCedula(usuario.getCedula());
        existente.setNombre(usuario.getNombre());
        existente.setApellido(usuario.getApellido());
        existente.setEmail(usuario.getEmail());
        existente.setUsername(usuario.getUsername());
        existente.setPassword(usuario.getPassword());
        existente.setEdad(usuario.getEdad());
        existente.setTelefono(usuario.getTelefono());
        existente.setRole(usuario.getRole());
        existente.setEstado(usuario.getEstado());

        return mapper.toUsuario(repository.save(existente));
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        UsuarioData usuarioData = repository.findByEmail(email);

        if (usuarioData == null) {
            return null;
        }
        return mapper.toUsuario(usuarioData);
    }
}
