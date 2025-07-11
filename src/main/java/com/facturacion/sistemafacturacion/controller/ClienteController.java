package com.facturacion.sistemafacturacion.controller;

import com.facturacion.sistemafacturacion.model.Cliente;
import com.facturacion.sistemafacturacion.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> getAllClientes(){
        return clienteService.getAllClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> getClienteById(@PathVariable Long id){
        Cliente cliente =  clienteService.getClienteById(id);
        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente){
        Cliente nuevoCliente = clienteService.createOrUpdateCliente(cliente);
        return new ResponseEntity<>(nuevoCliente,HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> updateCliente(@PathVariable long id,@RequestBody Cliente clienteDetails){
        Cliente cliente =  clienteService.getClienteById(id);
        cliente.setNombre(clienteDetails.getNombre());
        cliente.setApellido(clienteDetails.getApellido());
        cliente.setRucCedula(clienteDetails.getRucCedula());
        cliente.setDireccion(clienteDetails.getDireccion());
        cliente.setEmail(clienteDetails.getEmail());
        cliente.setTelefono(clienteDetails.getTelefono());
        Cliente updateCliente = clienteService.createOrUpdateCliente(cliente);
        return ResponseEntity.ok(updateCliente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id){
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

}
