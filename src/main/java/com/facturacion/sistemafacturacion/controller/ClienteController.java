package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.dto.ClienteDTO;
import com.facturacion.sistemafacturacion.mapper.ClienteMapper;
import com.facturacion.sistemafacturacion.model.Cliente;
import com.facturacion.sistemafacturacion.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteMapper clienteMapper;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<ClienteDTO> getAllClientes(){
        return clienteService.getAllClientes()
                .stream()
                .map(clienteMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteDTO> getClienteById(@PathVariable Long id){
        Cliente cliente = clienteService.getClienteById(id);
        return ResponseEntity.ok(clienteMapper.toDTO(cliente));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteDTO> createCliente(@RequestBody ClienteDTO clienteDTO){
        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        Cliente nuevo = clienteService.createOrUpdateCliente(cliente);
        return new ResponseEntity<>(clienteMapper.toDTO(nuevo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClienteDTO> updateCliente(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = clienteService.getClienteById(id);
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEmail(clienteDTO.getEmail());

        Cliente actualizado = clienteService.createOrUpdateCliente(cliente);
        return ResponseEntity.ok(clienteMapper.toDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

}
