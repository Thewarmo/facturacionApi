package com.facturacion.sistemafacturacion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes", schema = "facturacion")
@NamedQuery(name = "Cliente.findByRucCedula", query = "SELECT c FROM Cliente c WHERE c.rucCedula = :rucCedula")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "ruc_cedula", nullable = false, unique = true, length = 20)
    private String rucCedula;

    @Column(columnDefinition = "TEXT")
    private String direccion;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

}
