package com.facturacion.sistemafacturacion.service;

import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;
import com.facturacion.sistemafacturacion.model.Cliente;
import com.facturacion.sistemafacturacion.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.facturacion.sistemafacturacion.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;


@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public List<Cliente> getAllClientes() {
        return clienteRepository.findAllByActivoTrue();
    }

    public Cliente getClienteById(Long id){
        return clienteRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Cliente no encontrado con el ID: "+ id));
    }

    public Cliente createOrUpdateCliente(Cliente cliente){
        return clienteRepository.save(cliente);
    }

    public void deleteCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Cliente no encontrado con el ID: " + id);
        }
        Cliente cliente = getClienteById(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    public long contarClientes(){
        return clienteRepository.count();
    }

}
