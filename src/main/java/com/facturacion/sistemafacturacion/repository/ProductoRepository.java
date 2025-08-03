package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.model.Producto;
import com.facturacion.sistemafacturacion.model.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    @Query("SELECT p.codigo FROM Producto p WHERE p.tipoItem = :tipo ORDER BY p.id DESC")
    List<String> findUltimoCodigoByTipo(@Param("tipo") TipoItem tipo, Pageable pageable);

    boolean existsByCodigo(String codigo);

    List<Producto> findByActivoTrue();

    Optional<Producto> findByIdAndActivoTrue(Long id);

    long count();

    @Query("SELECT COUNT(*) as count FROM facturacion.productos WHERE activo = true")
    long countByActivo();

}
