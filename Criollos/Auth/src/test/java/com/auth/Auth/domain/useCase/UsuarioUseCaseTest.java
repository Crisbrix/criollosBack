package com.auth.Auth.domain.useCase;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.domain.model.gateway.EncrypterGateway;
import com.auth.Auth.domain.model.gateway.UsuarioGateWay;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioUseCaseTest {

    private final UsuarioGateWay usuarioGateWay = mock(UsuarioGateWay.class);
    private final EncrypterGateway encrypterGateway = mock(EncrypterGateway.class);
    private final UsuarioUseCase useCase = new UsuarioUseCase(usuarioGateWay, encrypterGateway);

    @Test
    void guardarUsuarioEncriptaPasswordAntesDeGuardar() {
        Usuario usuario = Usuario.builder()
                .email("ana@test.com")
                .password("1234")
                .build();
        Usuario guardado = Usuario.builder()
                .email("ana@test.com")
                .password("hash")
                .build();

        when(encrypterGateway.encrypt("1234")).thenReturn("hash");
        when(usuarioGateWay.guardarUsuario(usuario)).thenReturn(guardado);

        Usuario resultado = useCase.guardarUsuario(usuario);

        assertThat(resultado).isSameAs(guardado);
        assertThat(usuario.getPassword()).isEqualTo("hash");
        verify(usuarioGateWay).guardarUsuario(usuario);
    }

    @Test
    void guardarUsuarioRechazaEmailOPasswordNulos() {
        Usuario usuario = Usuario.builder().email("ana@test.com").build();

        assertThatThrownBy(() -> useCase.guardarUsuario(usuario))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("email o password");
    }

    @Test
    void buscarUsuarioPorEmailRetornaUsuarioSiExiste() {
        Usuario usuario = Usuario.builder().cedula("123").email("ana@test.com").build();
        when(usuarioGateWay.buscarUsuarioPorCedula("123")).thenReturn(usuario);

        assertThat(useCase.buscarUsuarioPorEmail("123")).isSameAs(usuario);
    }

    @Test
    void buscarUsuarioPorEmailRetornaUsuarioVacioSiNoExiste() {
        when(usuarioGateWay.buscarUsuarioPorCedula("123")).thenThrow(new RuntimeException("no existe"));

        Usuario resultado = useCase.buscarUsuarioPorEmail("123");

        assertThat(resultado.getEmail()).isNull();
    }

    @Test
    void eliminarUsuarioPorEmailDelegaEnGateway() {
        useCase.eliminarUsuarioPorEmail("123");

        verify(usuarioGateWay).eliminarUsuarioPorEmail("123");
    }

    @Test
    void actualizarUsuarioDelegaEnGateway() {
        Usuario usuario = Usuario.builder().nombre("Ana").build();
        when(usuarioGateWay.actualizarUsuario("123", usuario)).thenReturn(usuario);

        assertThat(useCase.actualizarUsuario("123", usuario)).isSameAs(usuario);
    }

    @Test
    void loginValidoRetornaUsuario() {
        Usuario usuario = Usuario.builder()
                .email("ana@test.com")
                .password("hash")
                .build();

        when(usuarioGateWay.buscarPorEmail("ana@test.com")).thenReturn(usuario);
        when(encrypterGateway.matches("1234", "hash")).thenReturn(true);

        Usuario resultado = useCase.login("ana@test.com", "1234");

        assertThat(resultado).isSameAs(usuario);
    }

    @Test
    void loginRechazaDatosInvalidos() {
        assertThatThrownBy(() -> useCase.login(null, "1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email y password son obligatorios");

        assertThatThrownBy(() -> useCase.login("correo-sin-arroba", "1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El email no tiene el @");
    }

    @Test
    void loginRechazaUsuarioNoEncontradoOPasswordIncorrecto() {
        when(usuarioGateWay.buscarPorEmail("ana@test.com")).thenReturn(null);

        assertThatThrownBy(() -> useCase.login("ana@test.com", "1234"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuario no encontrado");

        Usuario usuario = Usuario.builder().email("ana@test.com").password("hash").build();
        when(usuarioGateWay.buscarPorEmail("ana@test.com")).thenReturn(usuario);
        when(encrypterGateway.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.login("ana@test.com", "mala"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Contrasena incorrecta");
    }
}
