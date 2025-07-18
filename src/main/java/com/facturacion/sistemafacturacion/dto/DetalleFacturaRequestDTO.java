package com.facturacion.sistemafacturacion.dto;

public class DetalleFacturaRequestDTO {

    private ProductoDTO producto;
    private Integer cantidad;

    public ProductoDTO getProducto() {
        return producto;
    }

    public void setProducto(ProductoDTO producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

