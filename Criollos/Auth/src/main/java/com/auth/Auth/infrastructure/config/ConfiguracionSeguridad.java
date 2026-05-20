package com.auth.Auth.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class ConfiguracionSeguridad {

    private final FiltroAutenticacionJWT filtroAutenticacionJWT;

    @Bean
    public SecurityFilterChain cadenaFiltrosSeguridad(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(fuenteConfiguracionCORS()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // PUBLICOS
                        .requestMatchers("/api/criollos/usuarios/login").permitAll()
                        .requestMatchers("/api/criollos/usuarios/guardar").permitAll()

                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/").permitAll()

                        // PROTEGIDOS
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        filtroAutenticacionJWT,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource fuenteConfiguracionCORS() {
        org.springframework.web.cors.CorsConfiguration configuracion = new org.springframework.web.cors.CorsConfiguration();
        configuracion.setAllowCredentials(true);
        configuracion.addAllowedOriginPattern("*");
        configuracion.addAllowedHeader("*");
        configuracion.addAllowedMethod("*");

        org.springframework.web.cors.UrlBasedCorsConfigurationSource fuente = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        fuente.registerCorsConfiguration("/**", configuracion);
        return fuente;
    }

    @Bean
    public PasswordEncoder codificadorContrasena() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager gestorAutenticacion(AuthenticationConfiguration configuracion) throws Exception {
        return configuracion.getAuthenticationManager();
    }

}
