package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Producto> getAllProductos(){
        return productoService.getAllProductos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Producto> getProductoById(@PathVariable Long id){
        Producto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto){
        Producto nuevoProducto = productoService.createOrUpdateProducto(producto);

        return new ResponseEntity<>(nuevoProducto,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Producto> updateProducto(@PathVariable Long id,@RequestBody Producto productoDetails){
        Producto producto = productoService.getProductoById(id);
        producto.setNombre(productoDetails.getNombre());
        producto.setCategoria(productoDetails.getCategoria());
        producto.setCodigo(productoDetails.getCodigo());
        producto.setDescripcion(productoDetails.getDescripcion());
        producto.setPrecioUnitario(productoDetails.getPrecioUnitario());
        producto.setStock(productoDetails.getStock());
        Producto productoActualizado = productoService.createOrUpdateProducto(producto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id){
        productoService.deleteProducto(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    public List<Producto> searchProductos(@RequestParam String nombre) {
        return productoService.searchProductosByNombre(nombre);
    }

}
