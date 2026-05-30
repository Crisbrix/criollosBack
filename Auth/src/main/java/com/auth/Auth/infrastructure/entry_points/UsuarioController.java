package com.auth.Auth.infrastructure.entry_points;

import com.auth.Auth.domain.model.Usuario;
import com.auth.Auth.domain.model.LoginRequest;
import com.auth.Auth.domain.useCase.UsuarioUseCase;
import com.auth.Auth.infrastructure.config.UtilidadJWT;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import com.auth.Auth.infrastructure.mapper.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/criollos/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioMapper usuarioMapper;
    private final UtilidadJWT utilidadJWT;

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @PostMapping("/guardar")
    public ResponseEntity <Usuario> guardarUsuario(@RequestBody UsuarioData usuarioData) {
        Usuario usuarioValidadoGuardado = usuarioUseCase.guardarUsuario(usuarioMapper.toUsuario(usuarioData));
        return new ResponseEntity<>(usuarioValidadoGuardado, HttpStatus.OK);
    }
    @GetMapping("/buscar/{cedula}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Usuario> obtenerUsuarioPorCedula(@PathVariable String cedula) {
        Usuario usuario = usuarioUseCase.buscarUsuarioPorEmail(cedula);
        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @GetMapping("/interno/buscar/{cedula}")
    public ResponseEntity<?> obtenerUsuarioInternoPorCedula(@PathVariable String cedula) {
        Usuario usuario = usuarioUseCase.buscarUsuarioPorEmail(cedula);
        if (usuario.getId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "mensaje", "Usuario no encontrado"
            ));
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", usuario.getId());
        response.put("cedula", usuario.getCedula());
        response.put("nombre", usuario.getNombre());
        response.put("apellido", usuario.getApellido());
        response.put("email", usuario.getEmail());
        response.put("estado", usuario.getEstado());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/borrar/{cedula}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String cedula) {
        try {
            usuarioUseCase.eliminarUsuarioPorEmail(cedula);
            return ResponseEntity.ok(Map.of("success", true, "mensaje", "Usuario eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }
    @PutMapping("/actualizar/{cedula}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable String cedula, @RequestBody UsuarioData usuarioData) {
        try {
            Usuario usuarioActualizado = usuarioUseCase.actualizarUsuario(cedula, usuarioMapper.toUsuario(usuarioData));
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            Usuario usuario = usuarioUseCase.login(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            );
            String token = utilidadJWT.generarToken(usuario.getEmail(), usuario.getRole());

            return ResponseEntity.ok(
                    Map.of(
                            "mensaje", "Login exitoso",
                            "token", token,
                            "tipo", "Bearer",
                            "usuario", Map.of(
                                    "email", usuario.getEmail(),
                                    "nombre", usuario.getNombre(),
                                    "role", usuario.getRole()
                            )
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }
    @GetMapping("/listar")
    public ResponseEntity<?> listarUsuarios() {
        try {
            return ResponseEntity.ok(usuarioUseCase.listarUsuarios());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("success", false, "mensaje", e.getMessage()));
        }
    }
}
