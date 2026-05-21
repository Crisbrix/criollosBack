package com.auth.Auth.infrastructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioDataJpaRepository extends JpaRepository<UsuarioData, Long> {
    UsuarioData findByEmail(String email);
    Optional<UsuarioData> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
    void deleteByCedula(String cedula);
}
