package com.facturacion.sistemafacturacion.dto;

import com.facturacion.sistemafacturacion.model.MetodoPago;
import java.math.BigDecimal;

public class PagoRequestDTO {
    private Long facturaId;
    private BigDecimal monto;
    private MetodoPago metodoPago;
    private String referencia;

    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
