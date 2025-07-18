package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> getAllProductos(){
        return productoRepository.findAll();
    }

    public Producto getProductoById(Long id){
        return productoRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Producto no encontrado con el Id: "+id));
    }

    public Producto createOrUpdateProducto(Producto producto){
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            producto.setCodigo(generarCodigoUnico());
        }
        return productoRepository.save(producto);
    }


    public void deleteProducto(Long id){
        if(!productoRepository.existsById(id)){
            throw new ResourceNotFoundException("No se puede eliminar. Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    public List<Producto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public String generarCodigoUnico() {
        long total = productoRepository.count() + 1;
        return String.format("PRD-%03d", total); // PRD-001, PRD-002, etc.
    }

}
