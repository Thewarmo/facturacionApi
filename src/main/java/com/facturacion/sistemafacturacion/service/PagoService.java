package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.dto.PagoRequestDTO;
import com.facturacion.sistemafacturacion.dto.PagoResponseDTO;
import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.model.EstadoFactura;
import com.facturacion.sistemafacturacion.model.Factura;
import com.facturacion.sistemafacturacion.repository.FacturaRepository;
import com.facturacion.sistemafacturacion.repository.PagoRepository;
import com.facturacion.sistemafacturacion.model.Pago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class PagoService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Transactional
    public PagoResponseDTO registrarPago(Pago pago) {
        Factura factura = facturaRepository.findById(pago.getFactura().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

        BigDecimal totalPagado = pagoRepository
                .findByFacturaId(factura.getId())
                .stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal nuevoTotalPagado = totalPagado.add(pago.getMonto());
        BigDecimal excedente = BigDecimal.ZERO;

        // Calcular excedente si el pago supera el total
        if (nuevoTotalPagado.compareTo(factura.getTotal()) > 0) {
            excedente = nuevoTotalPagado.subtract(factura.getTotal());
            pago.setMonto(pago.getMonto().subtract(excedente)); // Solo se registra lo necesario
        }

        // Registrar el pago ajustado
        pago.setFactura(factura);
        Pago pagoRegistrado = pagoRepository.save(pago);

        // Cambiar estado si la factura queda completamente pagada
        if (nuevoTotalPagado.subtract(excedente).compareTo(factura.getTotal()) == 0) {
            factura.setEstado(EstadoFactura.PAGADA);
            facturaRepository.save(factura);
        }

        // Construir respuesta
        PagoResponseDTO response = new PagoResponseDTO();
        response.setFacturaId(factura.getId());
        response.setNumeroFactura(factura.getNumeroFactura());
        response.setMontoPagado(pago.getMonto());
        response.setExcedente(excedente);
        response.setFechaPago(pagoRegistrado.getFechaPago());
        response.setEstadoFactura(factura.getEstado().name());
        response.setUsuario( // reemplaza por lógica real
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        return response;
    }
}
