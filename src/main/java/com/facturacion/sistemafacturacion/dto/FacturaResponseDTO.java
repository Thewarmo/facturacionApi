package com.facturacion.sistemafacturacion.dto;

import com.facturacion.sistemafacturacion.model.EstadoFactura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
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

}

