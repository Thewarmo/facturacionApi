package com.facturacion.sistemafacturacion.dto;

import java.math.BigDecimal;

public interface VentaReporteDTO {
    String getPeriodo();
    Long getTotalFacturas();
    BigDecimal getTotalVentas();
}
