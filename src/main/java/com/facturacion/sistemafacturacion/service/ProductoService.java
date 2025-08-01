package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.model.CategoriaProducto;
import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.model.TipoItem;
import com.facturacion.sistemafacturacion.repository.CategoriaProductoRepository;
import com.facturacion.sistemafacturacion.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaProductoRepository categoriaProductoRepository;

    public List<Producto> getAllProductos(){
        return productoRepository.findByActivoTrue();
    }

    public Producto getProductoById(Long id){
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(()->new ResourceNotFoundException("Producto no encontrado con el Id: "+id));
    }

    public Producto createOrUpdateProducto(Producto producto){
        if (producto.getCodigo() == null || producto.getCodigo().isBlank()) {
            producto.setCodigo(generarCodigoUnicoPorTipo(producto.getTipoItem()));
        }

        // Asignar categoría por defecto si no se especifica
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            CategoriaProducto categoriaGeneral = categoriaProductoRepository.findByNombre("General")
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría 'General' no encontrada. Asegúrate de que exista en la base de datos."));
            producto.setCategoria(categoriaGeneral);
        }

        return productoRepository.save(producto);
    }


    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public List<Producto> searchProductosByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    public String generarCodigoUnicoPorTipo(TipoItem tipo) {
        String prefijo = tipo == TipoItem.SERVICIO ? "SRV" : "PRD";

        Pageable pageable = PageRequest.of(0, 1); // ✅ Página 0, solo 1 resultado
        List<String> ultimosCodigos = productoRepository.findUltimoCodigoByTipo(tipo, pageable);

        String ultimo = ultimosCodigos.isEmpty() ? null : ultimosCodigos.get(0);

        int siguiente = 1;

        if (ultimo != null && ultimo.startsWith(prefijo + "-")) {
            try {
                siguiente = Integer.parseInt(ultimo.split("-")[1]) + 1;
            } catch (NumberFormatException e) {
                // log opcional
            }
        }

        String codigoGenerado;
        do {
            codigoGenerado = String.format("%s-%03d", prefijo, siguiente++);
        } while (productoRepository.existsByCodigo(codigoGenerado));

        return codigoGenerado;
    }

    public long totalProductos(){
        return productoRepository.count();
    }


}
