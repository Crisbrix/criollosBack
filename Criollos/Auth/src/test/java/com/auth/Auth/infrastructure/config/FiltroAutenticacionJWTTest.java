package com.auth.Auth.infrastructure.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FiltroAutenticacionJWTTest {

    private final UtilidadJWT utilidadJWT = mock(UtilidadJWT.class);
    private final ServicioDetallesUsuarioImpl servicioDetallesUsuario = mock(ServicioDetallesUsuarioImpl.class);
    private final FiltroAutenticacionJWT filtro = new FiltroAutenticacionJWT(utilidadJWT, servicioDetallesUsuario);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuaSinAutenticarCuandoNoHayHeaderBearer() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filtro.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isSameAs(request);
        verify(servicioDetallesUsuario, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void continuaSinAutenticarCuandoTokenEsInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        when(utilidadJWT.validarToken("token")).thenReturn(false);

        filtro.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(servicioDetallesUsuario, never()).loadUserByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void autenticaCuandoTokenEsValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        UserDetails userDetails = User.withUsername("ana@test.com")
                .password("hash")
                .roles("ADMIN")
                .build();

        when(utilidadJWT.validarToken("token")).thenReturn(true);
        when(utilidadJWT.obtenerEmail("token")).thenReturn("ana@test.com");
        when(servicioDetallesUsuario.loadUserByUsername("ana@test.com")).thenReturn(userDetails);

        filtro.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("ana@test.com");
    }
}
