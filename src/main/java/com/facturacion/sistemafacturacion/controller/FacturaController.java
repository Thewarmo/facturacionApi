package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.FacturaRequestDTO;
import com.facturacion.sistemafacturacion.dto.FacturaResponseDTO;
import com.facturacion.sistemafacturacion.mapper.FacturaMapper;
import com.facturacion.sistemafacturacion.model.Factura;
import com.facturacion.sistemafacturacion.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private FacturaMapper facturaMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Factura> getAllFacturas(){
        return facturaService.getAllFacturas();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Factura> getFacturaById(@PathVariable Long id){
        Factura factura = facturaService.getFacturaById(id);
        return ResponseEntity.ok(factura);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacturaResponseDTO> createFactura(@RequestBody FacturaRequestDTO facturaDTO) {
        if (facturaDTO.getCliente().getRucCedula() == null || facturaDTO.getDetalles() == null || facturaDTO.getDetalles().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Factura factura = facturaMapper.toEntity(facturaDTO);
        Factura nuevaFactura = facturaService.createFactura(factura);
        FacturaResponseDTO response = facturaMapper.toResponseDTO(nuevaFactura);

        URI location = URI.create("/api/facturas/" + nuevaFactura.getId());
        return ResponseEntity.created(location).body(response);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long id){
        facturaService.deleteFactura(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacturaResponseDTO>> getFacturasPorCliente(@PathVariable Long clienteId) {
        List<Factura> facturas = facturaService.getFacturasByClienteId(clienteId);
        List<FacturaResponseDTO> response = facturas.stream()
                .map(facturaMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(response);
    }


}
