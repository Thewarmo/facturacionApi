package com.facturacion.sistemafacturacion.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class FacturaDTO {

        private Long id;
        private String numeroFactura;
        private ZonedDateTime fechaEmision;
        private String estado;
        private BigDecimal subtotal;
        private BigDecimal totalImpuestos;
        private BigDecimal total;
        private ClientFacturaDTO cliente;
        private List<DetalleFacturaDTO> detalles;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
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

    public ClientFacturaDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClientFacturaDTO cliente) {
        this.cliente = cliente;
    }

    public List<DetalleFacturaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaDTO> detalles) {
        this.detalles = detalles;
    }
}
