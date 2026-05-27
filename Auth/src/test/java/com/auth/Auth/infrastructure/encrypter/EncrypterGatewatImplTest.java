package com.auth.Auth.infrastructure.encrypter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EncrypterGatewatImplTest {

    private final EncrypterGatewatImpl encrypter = new EncrypterGatewatImpl();

    @Test
    void encriptaYComparaPasswordsConBCrypt() {
        String encrypted = encrypter.encrypt("1234");

        assertThat(encrypted).isNotEqualTo("1234");
        assertThat(encrypter.matches("1234", encrypted)).isTrue();
        assertThat(encrypter.matches("mala", encrypted)).isFalse();
    }
}
