package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends  JpaRepository<Factura,Long> {

    List<Factura> findByCliente_Id(Long clienteId);

    Factura findTopByOrderByIdDesc();
}
