package com.auth.Auth.infrastructure.driver_adapters.jpa_repository;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.infrastructure.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioDataGatewayImplTest {

    private final UsuarioDataJpaRepository repository = mock(UsuarioDataJpaRepository.class);
    private final UsuarioDataGatewayImpl gateway = new UsuarioDataGatewayImpl(repository, new UsuarioMapper());

    @Test
    void guardarUsuarioPersisteEntidadMapeada() {
        Usuario usuario = usuario("123", "Ana");
        UsuarioData guardado = data("123", "Ana");
        guardado.setId(1L);

        when(repository.save(org.mockito.ArgumentMatchers.any(UsuarioData.class))).thenReturn(guardado);

        Usuario resultado = gateway.guardarUsuario(usuario);

        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getCedula()).isEqualTo("123");
        assertThat(resultado.getNombre()).isEqualTo("Ana");
    }

    @Test
    void buscarUsuarioPorEmailBuscaPorCedula() {
        when(repository.findByCedula("123")).thenReturn(Optional.of(data("123", "Ana")));

        Usuario resultado = gateway.buscarUsuarioPorCedula("123");

        assertThat(resultado.getCedula()).isEqualTo("123");
        assertThat(resultado.getNombre()).isEqualTo("Ana");
    }

    @Test
    void buscarUsuarioPorEmailLanzaErrorSiNoExiste() {
        when(repository.findByCedula("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateway.buscarUsuarioPorCedula("123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void eliminarUsuarioPorEmailEliminaSiExiste() {
        when(repository.existsByCedula("123")).thenReturn(true);

        gateway.eliminarUsuarioPorEmail("123");

        verify(repository).deleteByCedula("123");
    }

    @Test
    void eliminarUsuarioPorEmailLanzaErrorSiNoExiste() {
        when(repository.existsByCedula("123")).thenReturn(false);

        assertThatThrownBy(() -> gateway.eliminarUsuarioPorEmail("123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void actualizarUsuarioModificaCamposYGuarda() {
        UsuarioData existente = data("123", "Ana");
        Usuario cambios = usuario("456", "Laura");
        when(repository.findByCedula("123")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Usuario resultado = gateway.actualizarUsuario("123", cambios);

        assertThat(resultado.getCedula()).isEqualTo("456");
        assertThat(resultado.getNombre()).isEqualTo("Laura");
        assertThat(resultado.getApellido()).isEqualTo("Perez");
        assertThat(resultado.getEmail()).isEqualTo("laura@test.com");
        assertThat(resultado.getUsername()).isEqualTo("laura");
        assertThat(resultado.getTelefono()).isEqualTo("301");
        assertThat(resultado.getRole()).isEqualTo("ADMIN");
        assertThat(resultado.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    void actualizarUsuarioLanzaErrorSiNoExiste() {
        when(repository.findByCedula("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gateway.actualizarUsuario("123", usuario("456", "Laura")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");
    }

    @Test
    void buscarPorEmailRetornaUsuarioONull() {
        when(repository.findByEmail("ana@test.com")).thenReturn(data("123", "Ana"));
        when(repository.findByEmail("nadie@test.com")).thenReturn(null);

        assertThat(gateway.buscarPorEmail("ana@test.com").getNombre()).isEqualTo("Ana");
        assertThat(gateway.buscarPorEmail("nadie@test.com")).isNull();
    }

    private Usuario usuario(String cedula, String nombre) {
        return new Usuario(1L, cedula, nombre, "Perez", nombre.toLowerCase() + "@test.com",
                nombre.toLowerCase(), "hash", 25, "301", "ADMIN", "ACTIVO");
    }

    private UsuarioData data(String cedula, String nombre) {
        return new UsuarioData(1L, cedula, nombre, "Perez", nombre.toLowerCase() + "@test.com",
                nombre.toLowerCase(), "hash", 25, "301", "ADMIN", "ACTIVO");
    }
}
