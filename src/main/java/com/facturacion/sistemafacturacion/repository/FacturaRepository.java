package com.facturacion.sistemafacturacion.repository;

import com.facturacion.sistemafacturacion.dto.VentaReporteDTO;
import com.facturacion.sistemafacturacion.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface FacturaRepository extends  JpaRepository<Factura,Long> {

    List<Factura> findByCliente_Id(Long clienteId);

    Factura findTopByOrderByIdDesc();

    @Query(value = """
    SELECT 
            TO_CHAR(f.fecha_emision, :formatoFecha) AS periodo,
            COUNT(*) AS total_facturas,
            SUM(f.total) AS total_ventas
        FROM facturacion.facturas f
        WHERE f.estado = 'PAGADA'
        GROUP BY periodo
        ORDER BY periodo
    """, nativeQuery = true)
    List<VentaReporteDTO> findVentasAgrupadasPorFecha(@Param("formatoFecha") String formatoFecha);

    // Method to find invoices between two ZonedDateTime values
    List<Factura> findByFechaEmisionBetween(ZonedDateTime start, ZonedDateTime end);

    // Alternative: using @Query with ZonedDateTime for more complex queries
    @Query("SELECT f FROM Factura f WHERE f.fechaEmision >= :start AND f.fechaEmision <= :end AND f.estado != 'ANULADA' ORDER BY f.fechaEmision DESC")
    List<Factura> findFacturasByDateRangeExcludingCancelled(@Param("start") ZonedDateTime start, @Param("end") ZonedDateTime end);

    // Method for finding by client ID (matching your named query)
    List<Factura> findByClienteId(Long clienteId);

    // Method for finding by client ID using the named query you defined
    @Query(name = "Factura.findByCliente_Id")
    List<Factura> findByClienteIdUsingNamedQuery(@Param("clienteId") Long clienteId);

    long count();
}
