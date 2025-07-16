package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.model.Factura;
import com.facturacion.sistemafacturacion.service.FacturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

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
    public ResponseEntity<Factura> createFactura(@RequestBody Factura factura){
        Factura nuevaFactura = facturaService.createFactura(factura);
        return new ResponseEntity<>(nuevaFactura,HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteFactura(@PathVariable Long id){
        facturaService.deleteFactura(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("isAuthenticated()")
    public List<Factura> getFacturasPorCliente(@PathVariable Long clienteId) {
        return facturaService.getFacturasByClienteId(clienteId);
    }

}
