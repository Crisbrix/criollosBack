package com.auth.Auth.infrastructure.config;

import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioData;
import com.auth.Auth.infrastructure.driver_adapters.jpa_repository.UsuarioDataJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServicioDetallesUsuarioImpl implements UserDetailsService {

    private final UsuarioDataJpaRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioData usuario = repository.findByEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        String role = usuario.getRole() == null ? "USER" : usuario.getRole();
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(role.replace("ROLE_", ""))
                .build();
    }
}
