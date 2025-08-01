package com.facturacion.sistemafacturacion.mapper;

import com.facturacion.sistemafacturacion.dto.ProductoDTO;
import com.facturacion.sistemafacturacion.model.CategoriaProducto;
import com.facturacion.sistemafacturacion.model.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setCodigo(producto.getCodigo());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecioUnitario(producto.getPrecioUnitario());
        dto.setStock(producto.getStock());
        dto.setTipoItem(producto.getTipoItem());

        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
            dto.setCategoriaNombre(producto.getCategoria().getNombre());
        }

        return dto;
    }

    public Producto toEntity(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecioUnitario(dto.getPrecioUnitario());
        producto.setStock(dto.getStock());
        producto.setTipoItem(dto.getTipoItem());

        if (dto.getCategoriaId() != null) {
            CategoriaProducto categoria = new CategoriaProducto();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        }

        return producto;
    }
}
