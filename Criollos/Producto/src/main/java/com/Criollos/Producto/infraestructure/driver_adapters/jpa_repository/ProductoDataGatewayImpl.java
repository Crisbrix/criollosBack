package com.Criollos.Producto.infraestructure.driver_adapters.jpa_repository;

import java.util.List;
import com.Criollos.Producto.domain.model.gateway.ProductoGateway;
import com.Criollos.Producto.domain.model.Producto;
import com.Criollos.Producto.infraestructure.mapper.ProductoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductoDataGatewayImpl implements ProductoGateway {

    private final ProductoDataJpaRepository repository;
    private final ProductoMapper mapper;

    @Override
    public Producto guardarProducto (Producto producto){
        return mapper.toDomain(repository.save(mapper.toData(producto)));
    }
    @Override
    public Producto buscarProductoPorId(Integer id){
        return repository.findById(id)
                .map(mapper::toDomain)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"+ id));
    }
    @Override
    public List<Producto> obtenerTodosLosProductos(){
        return repository.findAll()
                .stream()
                .map(mapper::toDomain).toList();
    }
    @Override
    public Producto actualizarProducto(Integer id, Producto producto){
        producto.setProductoId(id);
        return mapper.toDomain(repository.save(mapper.toData(producto)));
    }
    @Override
    public void eliminarProductoPorId(Integer id){
        repository.deleteById(id);
    }
    @Override
    public Producto reducirStock(Long id, Integer cantidad){
        ProductoData data = repository.findById(id.intValue())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        data.setStock(data.getStock() - cantidad);
        return mapper.toDomain(repository.save(data));
    }
    @Override
    public Producto reponerStock(Long id, Integer cantidad){
        ProductoData data = repository.findById(id.intValue())
                .orElseThrow(()-> new RuntimeException("Producto no encontrado con id: " + id));
        data.setStock(data.getStock() + cantidad);
        return mapper.toDomain(repository.save(data));
    }
    @Override
    public List<Producto> buscarPorNombre(String nombre){
        return repository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(mapper::toDomain).toList();
    }
    @Override
    public List<Producto> obtenerProductosBajoStock(){
        return repository.findProductosBajoStock()
                .stream()
                .map(mapper::toDomain).toList();
    }
    @Override
    public Producto validarProductoPorNombre(String nombre){
        return repository.findByNombre(nombre)
                .map(mapper::toDomain)
                .orElse(null);
    }
}
