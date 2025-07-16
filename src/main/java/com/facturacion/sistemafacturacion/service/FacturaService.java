package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.model.DetalleFactura;
import com.facturacion.sistemafacturacion.model.Factura;
import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.model.Cliente;
import com.facturacion.sistemafacturacion.repository.FacturaRepository;
import com.facturacion.sistemafacturacion.repository.ProductoRepository;
import com.facturacion.sistemafacturacion.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Factura> getAllFacturas(){
        return facturaRepository.findAll();
    }

    public Factura getFacturaById(Long id){
        return facturaRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Factura no encontrada con el Id: "+id));
    }

    @Transactional
    public Factura createFactura(Factura factura) {
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

            if (producto.getStock() < detalle.getCantidad()) {
                throw new RuntimeException("No hay stock suficiente para el producto: " + producto.getNombre());
            }

            detalle.setPrecioUnitario(producto.getPrecioUnitario());
            BigDecimal subtotalLinea = producto.getPrecioUnitario().multiply(new BigDecimal(detalle.getCantidad()));
            detalle.setSubtotalLinea(subtotalLinea);
            detalle.setFactura(factura);
            subtotalFactura = subtotalFactura.add(subtotalLinea);
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        BigDecimal impuestos = subtotalFactura.multiply(new BigDecimal("0"));
        BigDecimal total = subtotalFactura.add(impuestos);
        factura.setSubtotal(subtotalFactura);
        factura.setTotalImpuestos(impuestos);
        factura.setTotal(total);

        return facturaRepository.save(factura);
    }

    public void deleteFactura(Long id) {
        if (!facturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Factura no encontrada con el ID: " + id);
        }
        facturaRepository.deleteById(id);
    }

    public List<Factura> getFacturasByClienteId(Long clienteId) {
        return facturaRepository.findByCliente_Id(clienteId);
    }

}
