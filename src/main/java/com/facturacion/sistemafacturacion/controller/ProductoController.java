package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.ProductoDTO;
import com.facturacion.sistemafacturacion.mapper.ProductoMapper;
import com.facturacion.sistemafacturacion.model.CategoriaProducto;
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

    @Autowired
    private ProductoMapper productoMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ProductoDTO> getAllProductos() {
        return productoService.getAllProductos()
                .stream()
                .map(productoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id){
        Producto producto = productoService.getProductoById(id);
        return ResponseEntity.ok(productoMapper.toDTO(producto));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> createProducto(@RequestBody ProductoDTO productoDTO){
        Producto producto = productoMapper.toEntity(productoDTO);
        Producto guardado = productoService.createOrUpdateProducto(producto);
        return new ResponseEntity<>(productoMapper.toDTO(guardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductoDTO> updateProducto(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        Producto productoExistente = productoService.getProductoById(id);

        productoExistente.setNombre(productoDTO.getNombre());
        productoExistente.setDescripcion(productoDTO.getDescripcion());
        productoExistente.setPrecioUnitario(productoDTO.getPrecioUnitario());
        productoExistente.setStock(productoDTO.getStock());

        if (productoDTO.getCategoriaId() != null) {
            CategoriaProducto categoria = new CategoriaProducto();
            categoria.setId(productoDTO.getCategoriaId());
            productoExistente.setCategoria(categoria);
        }

        Producto actualizado = productoService.createOrUpdateProducto(productoExistente);
        return ResponseEntity.ok(productoMapper.toDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id){
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @PreAuthorize("isAuthenticated()")
    public List<ProductoDTO> searchProductos(@RequestParam String nombre) {
        return productoService.searchProductosByNombre(nombre)
                .stream()
                .map(productoMapper::toDTO)
                .toList();
    }

}
