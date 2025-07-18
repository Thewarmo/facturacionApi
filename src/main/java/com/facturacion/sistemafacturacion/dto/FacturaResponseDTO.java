package com.facturacion.sistemafacturacion.dto;

import com.facturacion.sistemafacturacion.model.EstadoFactura;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class FacturaResponseDTO {

    private Long id;
    private String numeroFactura;
    private ZonedDateTime fechaEmision;
    private EstadoFactura estado;
    private BigDecimal subtotal;
    private BigDecimal totalImpuestos;
    private BigDecimal total;
    private ClienteDTO cliente;
    private List<DetalleFacturaResponseDTO> detalles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public ZonedDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(ZonedDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public EstadoFactura getEstado() {
        return estado;
    }

    public void setEstado(EstadoFactura estado) {
        this.estado = estado;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalImpuestos() {
        return totalImpuestos;
    }

    public void setTotalImpuestos(BigDecimal totalImpuestos) {
        this.totalImpuestos = totalImpuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<DetalleFacturaResponseDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaResponseDTO> detalles) {
        this.detalles = detalles;
    }
}

