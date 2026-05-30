package com.Criollos.Producto.infraestructure.entry_points;
import com.Criollos.Producto.infraestructure.mapper.ProductoMapper;
import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.domain.useCase.ProductoUseCase;
import com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository.ProductoData;
import com.Criollos.Producto.infraestructure.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.DELETE,
        RequestMethod.OPTIONS
})
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoUseCase productoUseCase;
    private final ProductoMapper productoMapper;
    private final NotificationService notificationService;

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarProducto(@RequestBody ProductoData productoData) {
        try {
            Producto producto = productoUseCase.guardarProducto(
                    productoMapper.toDomain(productoData)
            );
            notificationService.sendProductCreatedNotification(producto);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @GetMapping("/buscar/{productoId}")
    public ResponseEntity<?> obtenerProductoPorId(@PathVariable Integer productoId) {
        try {
            Producto producto = productoUseCase.obtenerProductoPorId(productoId);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<?> obtenerTodosLosProductos() {
        try {
            return ResponseEntity.ok(productoUseCase.obtenerTodosLosProductos());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Integer productoId,
                                                @RequestBody ProductoData productoData) {
        try {
            Producto producto = productoUseCase.actualizarProducto(
                    productoId,
                    productoMapper.toDomain(productoData)
            );
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer productoId) {
        try {
            productoUseCase.eliminarProducto(productoId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "mensaje", "Producto eliminado correctamente"
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", "El producto no se puede eliminar porque tiene pedidos asociados."
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @PatchMapping("/reducir-stock/{productoId}")
    public ResponseEntity<?> reducirStock(@PathVariable Integer productoId,
                                          @RequestParam Integer cantidad) {
        try {
            Producto producto = productoUseCase.reducirStock(productoId, cantidad);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @PatchMapping("/reponer-stock/{productoId}")
    public ResponseEntity<?> reponerStock(@PathVariable Integer productoId,
                                          @RequestParam Integer cantidad) {
        try {
            Producto producto = productoUseCase.reponerStock(productoId, cantidad);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre) {
        try {
            return ResponseEntity.ok(productoUseCase.buscarProductoPorNombre(nombre));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<?> productosBajoStock() {
        try {
            return ResponseEntity.ok(productoUseCase.productosBajoStock());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "mensaje", e.getMessage()
            ));
        }
    }
}