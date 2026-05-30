package com.auth.Auth.infrastructure.entry_points;

import com.auth.Auth.domain.model.LoginRequest;
import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.domain.useCase.UsuarioUseCase;
import com.auth.Auth.infrastructure.config.UtilidadJWT;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import com.auth.Auth.infrastructure.mapper.UsuarioMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioControllerTest {

    private final UsuarioUseCase usuarioUseCase = mock(UsuarioUseCase.class);
    private final UsuarioMapper mapper = new UsuarioMapper();
    private final UtilidadJWT utilidadJWT = mock(UtilidadJWT.class);
    private final UsuarioController controller = new UsuarioController(usuarioUseCase, mapper, utilidadJWT);

    @Test
    void indexRedireccionaAIndexHtml() {
        assertThat(controller.index()).isEqualTo("forward:/index.html");
    }

    @Test
    void guardarUsuarioRetornaUsuarioGuardado() {
        UsuarioData data = data();
        Usuario guardado = usuario();
        when(usuarioUseCase.guardarUsuario(org.mockito.ArgumentMatchers.any(Usuario.class))).thenReturn(guardado);

        ResponseEntity<Usuario> response = controller.guardarUsuario(data);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isSameAs(guardado);
    }

    @Test
    void obtenerUsuarioPorCedulaRetornaUsuario() {
        Usuario usuario = usuario();
        when(usuarioUseCase.buscarUsuarioPorEmail("123")).thenReturn(usuario);

        ResponseEntity<Usuario> response = controller.obtenerUsuarioPorCedula("123");

        assertThat(response.getBody()).isSameAs(usuario);
    }

    @Test
    @SuppressWarnings("unchecked")
    void obtenerUsuarioInternoRetornaDatosSinPassword() {
        when(usuarioUseCase.buscarUsuarioPorEmail("123")).thenReturn(usuario());

        ResponseEntity<?> response = controller.obtenerUsuarioInternoPorCedula("123");

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(body)
                .containsEntry("id", 1L)
                .containsEntry("cedula", "123")
                .containsEntry("nombre", "Ana")
                .doesNotContainKey("password");
    }

    @Test
    @SuppressWarnings("unchecked")
    void obtenerUsuarioInternoNoEncontradoRetorna404() {
        when(usuarioUseCase.buscarUsuarioPorEmail("999")).thenReturn(new Usuario());

        ResponseEntity<?> response = controller.obtenerUsuarioInternoPorCedula("999");

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(body).containsEntry("success", false);
    }

    @Test
    void eliminarUsuarioRetornaMensaje() {
        ResponseEntity<String> response = (ResponseEntity<String>) controller.eliminarUsuario("123");

        assertThat(response.getBody()).isEqualTo("Usuario eliminado correctamente");
        verify(usuarioUseCase).eliminarUsuarioPorEmail("123");
    }

    @Test
    void actualizarUsuarioRetornaUsuarioActualizado() {
        Usuario usuario = usuario();
        when(usuarioUseCase.actualizarUsuario(org.mockito.ArgumentMatchers.eq("123"),
                org.mockito.ArgumentMatchers.any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<Usuario> response = (ResponseEntity<Usuario>) controller.actualizarUsuario("123", data());

        assertThat(response.getBody()).isSameAs(usuario);
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginExitosoRetornaTokenYUsuario() {
        Usuario usuario = usuario();
        when(usuarioUseCase.login("ana@test.com", "1234")).thenReturn(usuario);
        when(utilidadJWT.generarToken("ana@test.com", "ADMIN")).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(new LoginRequest("ana@test.com", "1234"));

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(body).containsEntry("mensaje", "Login exitoso");
        assertThat(body).containsEntry("token", "jwt-token");
        assertThat(body).containsEntry("tipo", "Bearer");
        assertThat((Map<String, Object>) body.get("usuario"))
                .containsEntry("email", "ana@test.com")
                .containsEntry("nombre", "Ana")
                .containsEntry("role", "ADMIN");
    }

    @Test
    @SuppressWarnings("unchecked")
    void loginFallidoRetornaMensajeDeError() {
        when(usuarioUseCase.login("ana@test.com", "mala")).thenThrow(new RuntimeException("Contrasena incorrecta"));

        ResponseEntity<?> response = controller.login(new LoginRequest("ana@test.com", "mala"));

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(body).containsEntry("error", true);
        assertThat(body).containsEntry("mensaje", "Contrasena incorrecta");
    }

    private Usuario usuario() {
        return new Usuario(1L, "123", "Ana", "Perez", "ana@test.com",
                "ana", "hash", 25, "301", "ADMIN", "ACTIVO");
    }

    private UsuarioData data() {
        return new UsuarioData(1L, "123", "Ana", "Perez", "ana@test.com",
                "ana", "hash", 25, "301", "ADMIN", "ACTIVO");
    }
}
