// ==========================================
// NotaCreditoDetalle.java
// ==========================================
package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidad NotaCreditoDetalle
 */
@Entity
@Table(name = "notas_credito_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotaCreditoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota_credito", nullable = false)
    private NotaCredito notaCredito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo")
    private Articulo articulo;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "cantidad", precision = 10, scale = 3, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioUnitario;

    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total;
}