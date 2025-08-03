package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.FacturaRequestDTO;
import com.facturacion.sistemafacturacion.dto.FacturaResponseDTO;
import com.facturacion.sistemafacturacion.dto.VentaReporteDTO;
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
    public List<FacturaResponseDTO> getAllFacturas() {
        return facturaService.getAllFacturas()
                .stream()
                .map(facturaMapper::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacturaResponseDTO> getFacturaById(@PathVariable Long id){
        FacturaResponseDTO factura = facturaService.getFacturaById(id);
        return ResponseEntity.ok(factura);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createFactura(@RequestBody FacturaRequestDTO facturaDTO) {
        if (facturaDTO.getCliente().getRucCedula() == null || facturaDTO.getDetalles() == null || facturaDTO.getDetalles().isEmpty()) {
            java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", "El RUC/cédula del cliente y los detalles de la factura son obligatorios.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        Factura factura = facturaMapper.toEntity(facturaDTO);
        Factura nuevaFactura = facturaService.createFactura(factura);
        FacturaResponseDTO response = facturaMapper.toResponseDTO(nuevaFactura);

        URI location = URI.create("/api/facturas/" + nuevaFactura.getId());
        return ResponseEntity.created(location).body(response);
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

   @PutMapping("/{id}/anular")
   @PreAuthorize("isAuthenticated()")
   public ResponseEntity<Void> anularFactura(@PathVariable Long id) {
        facturaService.anularFactura(id);
        return ResponseEntity.noContent().build();
   }

    @GetMapping("/reportes/ventas-diarias")
    public ResponseEntity<List<VentaReporteDTO>> getVentasDiarias() {
        return ResponseEntity.ok(facturaService.getVentasAgrupadasPor("DAY"));
    }

    @GetMapping("/reportes/ventas-mensuales")
    public ResponseEntity<List<VentaReporteDTO>> getVentasMensuales() {
        return ResponseEntity.ok(facturaService.getVentasAgrupadasPor("MONTH"));
    }

    @GetMapping("/reportes/ventas-anuales")
    public ResponseEntity<List<VentaReporteDTO>> getVentasAnuales() {
        return ResponseEntity.ok(facturaService.getVentasAgrupadasPor("YEAR"));
    }

    @GetMapping("/reportes/{tipo}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacturaResponseDTO>> obtenerReporte(
            @PathVariable String tipo,
            @RequestParam String fecha) {

        try {
            // Validate report type
            if (!List.of("diario", "mensual", "anual").contains(tipo.toLowerCase())) {
                return ResponseEntity.badRequest().build();
            }

            List<FacturaResponseDTO> reportes = facturaService.obtenerReportePor(tipo, fecha);
            return ResponseEntity.ok(reportes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Long> obtenerTotalFacturas() {
        long total = facturaService.contarFacturas();
        return ResponseEntity.ok(total);
    }

}
