package com.pedidos.Pedidos.domain.model.gateway;

import com.pedidos.Pedidos.domain.model.Pedido;

import java.util.List;

public interface PedidoGateway {
    Pedido guardarPedido(Pedido pedido);
    Pedido buscarPedidoPorNumero(String numeroPedido);
    List<Pedido> listarPedidos();
    List<Pedido> listarPedidosPorEstado(String estado);
    void eliminarPedidoPorNumero(String numeroPedido);
    Pedido actualizarPedido(String numeroPedido, Pedido pedido);
    Pedido actualizarEstadoPedido(String numeroPedido, String estado);
}
