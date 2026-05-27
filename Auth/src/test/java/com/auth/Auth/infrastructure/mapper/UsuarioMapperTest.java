package com.auth.Auth.infrastructure.mapper;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = new UsuarioMapper();

    @Test
    void convierteUsuarioDataADominio() {
        UsuarioData data = new UsuarioData(1L, "123", "Ana", "Rios", "ana@test.com",
                "ana", "hash", 25, "300", "ADMIN", "ACTIVO");

        Usuario usuario = mapper.toUsuario(data);

        assertThat(usuario.getId()).isEqualTo(1L);
        assertThat(usuario.getCedula()).isEqualTo("123");
        assertThat(usuario.getNombre()).isEqualTo("Ana");
        assertThat(usuario.getApellido()).isEqualTo("Rios");
        assertThat(usuario.getEmail()).isEqualTo("ana@test.com");
        assertThat(usuario.getUsername()).isEqualTo("ana");
        assertThat(usuario.getPassword()).isEqualTo("hash");
        assertThat(usuario.getEdad()).isEqualTo(25);
        assertThat(usuario.getTelefono()).isEqualTo("300");
        assertThat(usuario.getRole()).isEqualTo("ADMIN");
        assertThat(usuario.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    void convierteDominioAUsuarioData() {
        Usuario usuario = new Usuario(1L, "123", "Ana", "Rios", "ana@test.com",
                "ana", "hash", 25, "300", "ADMIN", "ACTIVO");

        UsuarioData data = mapper.toUsuarioData(usuario);

        assertThat(data.getId()).isEqualTo(1L);
        assertThat(data.getCedula()).isEqualTo("123");
        assertThat(data.getNombre()).isEqualTo("Ana");
        assertThat(data.getApellido()).isEqualTo("Rios");
        assertThat(data.getEmail()).isEqualTo("ana@test.com");
        assertThat(data.getUsername()).isEqualTo("ana");
        assertThat(data.getPassword()).isEqualTo("hash");
        assertThat(data.getEdad()).isEqualTo(25);
        assertThat(data.getTelefono()).isEqualTo("300");
        assertThat(data.getRole()).isEqualTo("ADMIN");
        assertThat(data.getEstado()).isEqualTo("ACTIVO");
    }
}
