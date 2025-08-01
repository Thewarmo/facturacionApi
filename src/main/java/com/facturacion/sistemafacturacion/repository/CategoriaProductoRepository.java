package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.model.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Integer> {
    Optional<CategoriaProducto> findByNombre(String nombre);
}
