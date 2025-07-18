package com.facturacion.sistemafacturacion.controller;


import com.facturacion.sistemafacturacion.dto.PagoRequestDTO;
import com.facturacion.sistemafacturacion.dto.PagoResponseDTO;
import com.facturacion.sistemafacturacion.model.Pago;
import com.facturacion.sistemafacturacion.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagoResponseDTO> registrarPago(@RequestBody Pago pago) {
        PagoResponseDTO response = pagoService.registrarPago(pago);
        return ResponseEntity.ok(response);
    }


}
