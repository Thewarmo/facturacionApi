package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {

    Optional<Cliente> findByRucCedula(String rucCedula);

    List<Cliente> findAllByActivoTrue();

    long count();
}
