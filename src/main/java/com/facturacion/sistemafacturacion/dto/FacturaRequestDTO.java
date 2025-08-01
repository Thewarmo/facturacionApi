package com.facturacion.sistemafacturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacturaRequestDTO {

    private ClienteDTO cliente; // Ahora el cliente completo

    private List<DetalleFacturaRequestDTO> detalles;

    // Si ya obtienes el usuario del contexto, puedes omitir esto
    private Long usuarioId;
}
