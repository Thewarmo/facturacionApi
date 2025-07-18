package com.facturacion.sistemafacturacion.dto;

import java.util.List;

public class FacturaRequestDTO {

    private ClienteDTO cliente; // Ahora el cliente completo

    private List<DetalleFacturaRequestDTO> detalles;

    // Si ya obtienes el usuario del contexto, puedes omitir esto
    private Long usuarioId;

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<DetalleFacturaRequestDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleFacturaRequestDTO> detalles) {
        this.detalles = detalles;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}
