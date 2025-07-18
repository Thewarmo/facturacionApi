package com.facturacion.sistemafacturacion.mapper;

import com.facturacion.sistemafacturacion.dto.*;
import com.facturacion.sistemafacturacion.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FacturaMapper {

    public FacturaDTO toDTO(Factura factura) {
        FacturaDTO dto = new FacturaDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setEstado(factura.getEstado().name());
        dto.setSubtotal(factura.getSubtotal());
        dto.setTotalImpuestos(factura.getTotalImpuestos());
        dto.setTotal(factura.getTotal());

        ClientFacturaDTO clienteDTO = new ClientFacturaDTO();
        Cliente cliente = factura.getCliente();
        clienteDTO.setId(cliente.getId());
        clienteDTO.setNombre(cliente.getNombre());
        clienteDTO.setApellido(cliente.getApellido());
        clienteDTO.setRucCedula(cliente.getRucCedula());
        clienteDTO.setDireccion(cliente.getDireccion());
        clienteDTO.setEmail(cliente.getEmail());
        clienteDTO.setTelefono(cliente.getTelefono());
        dto.setCliente(clienteDTO);

        List<DetalleFacturaDTO> detalles = factura.getDetalles().stream()
                .map(detalle -> {
                    DetalleFacturaDTO d = new DetalleFacturaDTO();
                    d.setId(detalle.getId());
                    d.setCantidad(detalle.getCantidad());
                    d.setPrecioUnitario(detalle.getPrecioUnitario());
                    d.setSubtotalLinea(detalle.getSubtotalLinea());
                    d.setProductoId(detalle.getProducto().getId());
                    d.setProductoNombre(detalle.getProducto().getNombre());
                    return d;
                }).collect(Collectors.toList());

        dto.setDetalles(detalles);

        return dto;
    }

    public Factura toEntity(FacturaRequestDTO dto) {
        Factura factura = new Factura();

        // Convertir ClienteDTO a entidad Cliente
        ClienteDTO clienteDTO = dto.getCliente();
        Cliente cliente = new Cliente();
        cliente.setRucCedula(clienteDTO.getRucCedula());
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEmail(clienteDTO.getEmail());
        factura.setCliente(cliente);

        // Usuario opcional
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            factura.setUsuario(usuario);
        }

        // Convertir lista de detalles
        factura.setDetalles(dto.getDetalles().stream().map(det -> {
            DetalleFactura detalle = new DetalleFactura();

            ProductoDTO productoDTO = det.getProducto();
            Producto producto = new Producto();
            producto.setId(productoDTO.getId()); // solo necesitas el ID para buscarlo
            detalle.setProducto(producto);

            detalle.setCantidad(det.getCantidad());
            detalle.setFactura(factura); // importante si usas relaciones bidireccionales
            return detalle;
        }).collect(Collectors.toList()));

        return factura;
    }


    public FacturaResponseDTO toResponseDTO(Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(factura.getId()); // Asegúrate de incluir el ID
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setFechaEmision(factura.getFechaEmision());
        dto.setEstado(factura.getEstado());
        dto.setSubtotal(factura.getSubtotal());
        dto.setTotalImpuestos(factura.getTotalImpuestos());
        dto.setTotal(factura.getTotal());

        // Cliente
        if (factura.getCliente() != null) {
            Cliente cliente = factura.getCliente();
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(cliente.getId());
            clienteDTO.setNombre(cliente.getNombre());
            clienteDTO.setApellido(cliente.getApellido());
            clienteDTO.setRucCedula(cliente.getRucCedula());
            clienteDTO.setDireccion(cliente.getDireccion());
            clienteDTO.setTelefono(cliente.getTelefono());
            clienteDTO.setEmail(cliente.getEmail());
            dto.setCliente(clienteDTO);
        }

        // Detalles
        List<DetalleFacturaResponseDTO> detalles = factura.getDetalles().stream().map(detalle -> {
            DetalleFacturaResponseDTO detDTO = new DetalleFacturaResponseDTO();
            detDTO.setId(detalle.getId()); // Importante para evitar confusiones
            detDTO.setCantidad(detalle.getCantidad());
            detDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            detDTO.setSubtotalLinea(detalle.getSubtotalLinea());

            Producto producto = detalle.getProducto();
            if (producto != null) {
                ProductoDTO productoDTO = new ProductoDTO();
                productoDTO.setId(producto.getId());
                productoDTO.setCodigo(producto.getCodigo());
                productoDTO.setNombre(producto.getNombre());
                productoDTO.setDescripcion(producto.getDescripcion());
                productoDTO.setPrecioUnitario(producto.getPrecioUnitario());
                productoDTO.setStock(producto.getStock());

                if (producto.getCategoria() != null) {
                    productoDTO.setCategoriaId(producto.getCategoria().getId());
                    productoDTO.setCategoriaNombre(producto.getCategoria().getNombre());
                }

                detDTO.setProducto(productoDTO);
            }

            return detDTO;
        }).toList();

        dto.setDetalles(detalles);
        return dto;
    }
}

