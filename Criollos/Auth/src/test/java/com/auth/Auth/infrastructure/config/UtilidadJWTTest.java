package com.auth.Auth.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UtilidadJWTTest {

    @Test
    void generaYValidaTokenConEmailYRole() {
        UtilidadJWT utilidadJWT = utilidadJwt(3600);

        String token = utilidadJWT.generarToken("ana@test.com", "ADMIN");

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(utilidadJWT.validarToken(token)).isTrue();
        assertThat(utilidadJWT.obtenerEmail(token)).isEqualTo("ana@test.com");
        assertThat(utilidadJWT.obtenerRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void rechazaTokenMalFormadoAlteradoOExpirado() {
        UtilidadJWT utilidadJWT = utilidadJwt(3600);
        String token = utilidadJWT.generarToken("ana@test.com", "USER");
        String alterado = token.substring(0, token.length() - 1) + "x";

        assertThat(utilidadJWT.validarToken("token-invalido")).isFalse();
        assertThat(utilidadJWT.validarToken(alterado)).isFalse();

        UtilidadJWT expirado = utilidadJwt(-1);
        assertThat(expirado.validarToken(expirado.generarToken("ana@test.com", "USER"))).isFalse();
    }

    @Test
    void generaTokenConValoresNulosSinRomper() {
        UtilidadJWT utilidadJWT = utilidadJwt(3600);

        String token = utilidadJWT.generarToken(null, null);

        assertThat(utilidadJWT.validarToken(token)).isTrue();
        assertThat(utilidadJWT.obtenerEmail(token)).isEmpty();
        assertThat(utilidadJWT.obtenerRole(token)).isEmpty();
    }

    private UtilidadJWT utilidadJwt(long expirationSeconds) {
        UtilidadJWT utilidadJWT = new UtilidadJWT();
        ReflectionTestUtils.setField(utilidadJWT, "secret", "test-secret");
        ReflectionTestUtils.setField(utilidadJWT, "expirationSeconds", expirationSeconds);
        return utilidadJWT;
    }
}
