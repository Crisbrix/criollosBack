package com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PedidoDataJpaRepository extends JpaRepository<PedidoData, Long> {
    Optional<PedidoData> findByNumeroPedido(String numeroPedido);
    boolean existsByNumeroPedido(String numeroPedido);
    void deleteByNumeroPedido(String numeroPedido);
    List<PedidoData> findByEstado(String estado);
}
