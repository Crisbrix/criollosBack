package com.auth.Auth.infrastructure.config;

import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioDataJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServicioDetallesUsuarioImplTest {

    private final UsuarioDataJpaRepository repository = mock(UsuarioDataJpaRepository.class);
    private final ServicioDetallesUsuarioImpl service = new ServicioDetallesUsuarioImpl(repository);

    @Test
    void cargaUsuarioPorEmailConRoleNormalizado() {
        UsuarioData usuario = UsuarioData.builder()
                .email("ana@test.com")
                .password("hash")
                .role("ROLE_ADMIN")
                .build();
        when(repository.findByEmail("ana@test.com")).thenReturn(usuario);

        UserDetails userDetails = service.loadUserByUsername("ana@test.com");

        assertThat(userDetails.getUsername()).isEqualTo("ana@test.com");
        assertThat(userDetails.getPassword()).isEqualTo("hash");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void usaRoleUserSiNoHayRole() {
        UsuarioData usuario = UsuarioData.builder()
                .email("ana@test.com")
                .password("hash")
                .build();
        when(repository.findByEmail("ana@test.com")).thenReturn(usuario);

        UserDetails userDetails = service.loadUserByUsername("ana@test.com");

        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @Test
    void lanzaErrorSiUsuarioNoExiste() {
        when(repository.findByEmail("nadie@test.com")).thenReturn(null);

        assertThatThrownBy(() -> service.loadUserByUsername("nadie@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuario no encontrado");
    }
}
