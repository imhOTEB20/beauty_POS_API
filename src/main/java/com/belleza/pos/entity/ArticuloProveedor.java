package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad ArticuloProveedor - Relación entre Artículo y Proveedor
 */
@Entity
@Table(name = "articulos_proveedores",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_articulo", "id_proveedor"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @Column(name = "costo", precision = 15, scale = 2)
    private BigDecimal costo = BigDecimal.ZERO;

    @Column(name = "es_predeterminado", nullable = false)
    private Boolean esPredeterminado = false;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime ultimaActualizacion;

    @PrePersist
    @PreUpdate
    private void actualizarFecha() {
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
