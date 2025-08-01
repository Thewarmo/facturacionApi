package com.facturacion.sistemafacturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos", schema = "facturacion")
@NamedQuery(name = "Producto.findByNombreContainingIgnoreCase", query = "SELECT p FROM Producto p WHERE lower(p.nombre) LIKE lower(concat('%', :nombre, '%'))")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name= "stock")
    private Integer stock;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false)
    private TipoItem tipoItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaProducto categoria;

    @Column(name = "activo", nullable = false)
    private boolean activo;

    @PrePersist
    public void prePersist() {
        this.activo = true;
    }

}
