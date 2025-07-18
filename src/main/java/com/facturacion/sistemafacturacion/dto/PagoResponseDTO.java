package com.facturacion.sistemafacturacion.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PagoResponseDTO {

    private Long facturaId;
    private String numeroFactura;
    private BigDecimal montoPagado;
    private BigDecimal excedente;
    private ZonedDateTime fechaPago;
    private String estadoFactura;
    private String usuario;

    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public String getNumeroFactura() {
        return numeroFactura;
    }

    public void setNumeroFactura(String numeroFactura) {
        this.numeroFactura = numeroFactura;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public BigDecimal getExcedente() {
        return excedente;
    }

    public void setExcedente(BigDecimal excedente) {
        this.excedente = excedente;
    }

    public ZonedDateTime getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(ZonedDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getEstadoFactura() {
        return estadoFactura;
    }

    public void setEstadoFactura(String estadoFactura) {
        this.estadoFactura = estadoFactura;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
