package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entidad que contiene detalles referenctes a un articulo seleccionado para un presupuesto
 */

@Entity
@Table(name = "presupuestos_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PresupuestoDetalle extends DetalleOperacion{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = false)
    private Presupuesto presupuesto;
}
