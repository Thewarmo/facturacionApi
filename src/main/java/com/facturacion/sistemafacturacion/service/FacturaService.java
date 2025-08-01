package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.dto.FacturaResponseDTO;
import com.facturacion.sistemafacturacion.dto.VentaReporteDTO;
import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.mapper.FacturaMapper;
import com.facturacion.sistemafacturacion.model.*;
import com.facturacion.sistemafacturacion.repository.FacturaRepository;
import com.facturacion.sistemafacturacion.repository.ProductoRepository;
import com.facturacion.sistemafacturacion.repository.ClienteRepository;
import com.facturacion.sistemafacturacion.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FacturaMapper facturaMapper;

    public List<Factura> getAllFacturas(){
        return facturaRepository.findAll();
    }

    public FacturaResponseDTO getFacturaById(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada con id: " + id));
        return facturaMapper.toResponseDTO(factura);
    }


    @Transactional
    public Factura createFactura(Factura factura) {
        // Genera número de factura automático
        factura.setNumeroFactura(generarNumeroFactura());

        // Cliente: validar y persistir si es nuevo
        Cliente clienteInfo = factura.getCliente();
        if (clienteInfo == null || clienteInfo.getRucCedula() == null) {
            throw new RuntimeException("La información del cliente y su RUC/Cédula son obligatorios.");
        }

        Cliente clientePersistido = clienteRepository.findByRucCedula(clienteInfo.getRucCedula())
                .orElseGet(() -> {
                    if (clienteInfo.getNombre() == null || clienteInfo.getApellido() == null) {
                        throw new RuntimeException("Para un nuevo cliente, el nombre y el apellido son obligatorios.");
                    }
                    return clienteRepository.save(clienteInfo);
                });

        factura.setCliente(clientePersistido);

        BigDecimal subtotalFactura = BigDecimal.ZERO;

        for (DetalleFactura detalle : factura.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + detalle.getProducto().getId()));

            if (producto.getTipoItem() == TipoItem.PRODUCTO) {
               if (producto.getStock() == null || producto.getStock() < detalle.getCantidad()) {
                  throw new RuntimeException("No hay stock suficiente para el producto: " + producto.getNombre());
               }

              producto.setStock(producto.getStock() - detalle.getCantidad());
              productoRepository.save(producto);
            }
            detalle.setProducto(producto);
            detalle.setPrecioUnitario(producto.getPrecioUnitario());
            BigDecimal subtotalLinea = producto.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubtotalLinea(subtotalLinea);
            detalle.setFactura(factura);
            subtotalFactura = subtotalFactura.add(subtotalLinea);

            }

        BigDecimal impuestos = subtotalFactura.multiply(new BigDecimal("0")); // Aquí podrías aplicar IVA
        BigDecimal total = subtotalFactura.add(impuestos);

        factura.setSubtotal(subtotalFactura);
        factura.setTotalImpuestos(impuestos);
        factura.setTotal(total);
        factura.setEstado(EstadoFactura.PENDIENTE);
        Usuario usuario = obtenerUsuarioAutenticado();
        factura.setUsuario(usuario);

        return facturaRepository.save(factura);
    }

    private Usuario obtenerUsuarioAutenticado() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
    }

    private String generarNumeroFactura() {
        Factura ultima = facturaRepository.findTopByOrderByIdDesc();
        Long nuevoId = (ultima != null) ? ultima.getId() + 1 : 1L;

        return String.format("FAC-%06d", nuevoId); // Ejemplo: FAC-000001
    }

    public void anularFactura(Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada con id: " + id));
        if (EstadoFactura.PAGADA.equals(factura.getEstado()) || EstadoFactura.ANULADA.equals(factura.getEstado())) {
        throw new IllegalStateException("La factura no se puede anular porque ya está " + factura.getEstado().toString().toLowerCase() + ".");
        }

        factura.setEstado(EstadoFactura.ANULADA);

        // Revertir stock de productos
        for (DetalleFactura detalle : factura.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getTipoItem() == TipoItem.PRODUCTO) {
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }
        facturaRepository.save(factura);
    }

    public List<Factura> getFacturasByClienteId(Long clienteId) {
        return facturaRepository.findByCliente_Id(clienteId);
    }

    public List<VentaReporteDTO> getVentasAgrupadasPor(String periodo) {
        String dateFormat = switch (periodo) {
            case "DAY" -> "YYYY-MM-DD";
            case "MONTH" -> "YYYY-MM";
            case "YEAR" -> "YYYY";
            default -> throw new IllegalArgumentException("Periodo no válido: " + periodo);
        };

        return facturaRepository.findVentasAgrupadasPorFecha(dateFormat);
    }

    public List<FacturaResponseDTO> obtenerReportePor(String tipo, String fecha) {
        LocalDate fechaParsed;
        try {
            // Parse the date string to LocalDate
            fechaParsed = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use YYYY-MM-DD");
        }

        // Use system default zone (you can also specify a specific zone if needed)
        ZoneId zoneId = ZoneId.systemDefault();
        List<Factura> facturas;

        switch (tipo.toLowerCase()) {
            case "diario":
                // Get invoices for the specific day
                ZonedDateTime startOfDay = fechaParsed.atStartOfDay(zoneId);
                ZonedDateTime endOfDay = fechaParsed.atTime(23, 59, 59, 999_999_999).atZone(zoneId);
                facturas = facturaRepository.findByFechaEmisionBetween(startOfDay, endOfDay);
                break;

            case "mensual":
                // Get invoices for the entire month
                ZonedDateTime startOfMonth = fechaParsed.withDayOfMonth(1).atStartOfDay(zoneId);
                ZonedDateTime endOfMonth = fechaParsed.withDayOfMonth(fechaParsed.lengthOfMonth())
                        .atTime(23, 59, 59, 999_999_999).atZone(zoneId);
                facturas = facturaRepository.findByFechaEmisionBetween(startOfMonth, endOfMonth);
                break;

            case "anual":
                // Get invoices for the entire year
                ZonedDateTime startOfYear = fechaParsed.withDayOfYear(1).atStartOfDay(zoneId);
                ZonedDateTime endOfYear = fechaParsed.withDayOfYear(fechaParsed.lengthOfYear())
                        .atTime(23, 59, 59, 999_999_999).atZone(zoneId);
                facturas = facturaRepository.findByFechaEmisionBetween(startOfYear, endOfYear);
                break;

            default:
                throw new IllegalArgumentException("Tipo de reporte no válido: " + tipo + ". Valores permitidos: diario, mensual, anual");
        }

        return facturas.stream()
                .filter(factura -> factura.getEstado() != EstadoFactura.ANULADA) // Exclude cancelled invoices
                .map(facturaMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public long contarFacturas() {
        return facturaRepository.count();
    }

}
