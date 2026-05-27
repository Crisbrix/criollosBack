package com.auth.Auth.aplication.config;

import com.auth.Auth.domain.model.gateway.EncrypterGateway;
import com.auth.Auth.domain.model.gateway.UsuarioGateWay;
import com.auth.Auth.domain.useCase.UsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioGateWay usuarioGateWay, EncrypterGateway encrypterGateway) {
        return new UsuarioUseCase(usuarioGateWay, encrypterGateway);
    }
}