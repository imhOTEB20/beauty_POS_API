package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad ArticuloPrecio - Relación entre Artículo y Lista de Precios
 */
@Entity
@Table(name = "articulos_precios",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_articulo", "id_lista"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloPrecio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_precio")
    private Integer idPrecio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lista", nullable = false)
    private ListaPrecio listaPrecio;

    @Column(name = "precio_costo", precision = 15, scale = 2)
    private BigDecimal precioCosto = BigDecimal.ZERO;

    @Column(name = "precio_venta", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "porcentaje_utilidad", precision = 5, scale = 2)
    private BigDecimal porcentajeUtilidad = BigDecimal.ZERO;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @PrePersist
    @PreUpdate
    private void actualizarFecha() {
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }
}