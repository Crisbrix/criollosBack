package com.auth.Auth.domain.model.gateway;

public interface EncrypterGateway {
    String encrypt(String password);
    boolean matches(String rawPassword, String encryptedPassword);
}
