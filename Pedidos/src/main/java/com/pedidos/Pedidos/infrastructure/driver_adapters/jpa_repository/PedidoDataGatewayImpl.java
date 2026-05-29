package com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository;

import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.domain.model.gateway.PedidoGateway;
import com.pedidos.Pedidos.infrastructure.mapper.PedidoMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PedidoDataGatewayImpl implements PedidoGateway {

    private final PedidoDataJpaRepository repository;
    private final PedidoMapper mapper;

    @Override
    public Pedido guardarPedido(Pedido pedido) {
        PedidoData pedidoData = mapper.toPedidoData(pedido);
        return mapper.toPedido(repository.save(pedidoData));
    }

    @Override
    public Pedido buscarPedidoPorNumero(String numeroPedido) {
        return repository.findByNumeroPedido(numeroPedido)
                .map(mapper::toPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    @Override
    public List<Pedido> listarPedidos() {
        return repository.findAll()
                .stream()
                .map(mapper::toPedido)
                .toList();
    }

    @Override
    public List<Pedido> listarPedidosPorEstado(String estado) {
        return repository.findByEstado(estado)
                .stream()
                .map(mapper::toPedido)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarPedidoPorNumero(String numeroPedido) {
        PedidoData pedido = repository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        repository.delete(pedido);
    }

    @Override
    public Pedido actualizarPedido(String numeroPedido, Pedido pedido) {
        PedidoData existente = repository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        PedidoData actualizado = mapper.toPedidoData(pedido);
        existente.setCedulaCliente(actualizado.getCedulaCliente());
        existente.setNombreCliente(actualizado.getNombreCliente());
        existente.setMesa(actualizado.getMesa());
        existente.setUsuarioId(actualizado.getUsuarioId());
        existente.setMetodoPago(actualizado.getMetodoPago());
        existente.setEstado(actualizado.getEstado());
        existente.setSubtotal(actualizado.getSubtotal());
        existente.setImpuesto(actualizado.getImpuesto());
        existente.setTotal(actualizado.getTotal());
        existente.setFechaActualizacion(actualizado.getFechaActualizacion());
        if (existente.getDetalles() == null) {
            existente.setDetalles(new ArrayList<>());
        }
        existente.getDetalles().clear();
        actualizado.getDetalles().forEach(detalle -> {
            detalle.setId(null);
            detalle.setPedido(existente);
            existente.getDetalles().add(detalle);
        });

        return mapper.toPedido(repository.save(existente));
    }

    @Override
    public Pedido actualizarEstadoPedido(String numeroPedido, String estado) {
        PedidoData existente = repository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        existente.setEstado(estado);
        existente.setFechaActualizacion(LocalDateTime.now());

        return mapper.toPedido(repository.save(existente));
    }
}
