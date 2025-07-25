package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByFacturaId(Long facturaId);
}
