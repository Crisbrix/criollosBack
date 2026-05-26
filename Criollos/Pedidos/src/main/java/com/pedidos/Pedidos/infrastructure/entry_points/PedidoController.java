package com.pedidos.Pedidos.infrastructure.entry_points;

import com.pedidos.Pedidos.domain.model.Pedido;
import com.pedidos.Pedidos.domain.useCase.PedidoUseCase;
import com.pedidos.Pedidos.infrastructure.client.PedidoIntegracionService;
import com.pedidos.Pedidos.infrastructure.driver_adapters.jpa_repository.PedidoData;
import com.pedidos.Pedidos.infrastructure.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/criollos/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoUseCase pedidoUseCase;
    private final PedidoMapper pedidoMapper;
    private final PedidoIntegracionService pedidoIntegracionService;

    @PostMapping("/guardar")
    public ResponseEntity<Pedido> guardarPedido(@RequestBody PedidoData pedidoData) {
        Pedido pedido = pedidoMapper.toPedido(pedidoData);
        pedidoIntegracionService.validarPedidoConApis(pedido);
        Pedido pedidoGuardado = pedidoUseCase.guardarPedido(pedido);
        pedidoIntegracionService.reducirStock(pedidoGuardado);
        return new ResponseEntity<>(pedidoGuardado, HttpStatus.OK);
    }

    @GetMapping("/buscar/{numeroPedido}")
    public ResponseEntity<Pedido> obtenerPedidoPorNumero(@PathVariable String numeroPedido) {
        Pedido pedido = pedidoUseCase.buscarPedidoPorNumero(numeroPedido);
        return new ResponseEntity<>(pedido, HttpStatus.OK);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoUseCase.listarPedidos());
    }

    @GetMapping("/listar/estado/{estado}")
    public ResponseEntity<List<Pedido>> listarPedidosPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(pedidoUseCase.listarPedidosPorEstado(estado));
    }

    @PutMapping("/actualizar/{numeroPedido}")
    public ResponseEntity<Pedido> actualizarPedido(
            @PathVariable String numeroPedido,
            @RequestBody PedidoData pedidoData
    ) {
        Pedido pedidoActualizado = pedidoUseCase.actualizarPedido(numeroPedido, pedidoMapper.toPedido(pedidoData));
        return ResponseEntity.ok(pedidoActualizado);
    }

    @PutMapping("/estado/{numeroPedido}")
    public ResponseEntity<Pedido> actualizarEstadoPedido(
            @PathVariable String numeroPedido,
            @RequestBody Map<String, String> estadoRequest
    ) {
        Pedido pedidoActualizado = pedidoUseCase.actualizarEstadoPedido(numeroPedido, estadoRequest.get("estado"));
        return ResponseEntity.ok(pedidoActualizado);
    }

    @DeleteMapping("/borrar/{numeroPedido}")
    public ResponseEntity<String> eliminarPedido(@PathVariable String numeroPedido) {
        pedidoUseCase.eliminarPedidoPorNumero(numeroPedido);
        return ResponseEntity.ok("Pedido eliminado correctamente");
    }
}
