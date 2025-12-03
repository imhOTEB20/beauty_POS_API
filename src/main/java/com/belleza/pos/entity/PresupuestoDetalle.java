// ==========================================
// PresupuestoDetalle.java
// ==========================================
package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidad PresupuestoDetalle
 */
@Entity
@Table(name = "presupuestos_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = false)
    private Presupuesto presupuesto;

    @Column(name = "numero_linea", nullable = false)
    private Integer numeroLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;

    @Column(name = "codigo_barras", nullable = false, length = 50)
    private String codigoBarras;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "cantidad", precision = 10, scale = 3, nullable = false)
    private BigDecimal cantidad;

    @Column(name = "precio_sin_iva", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioSinIva;

    @Column(name = "porcentaje_iva", precision = 5, scale = 2, nullable = false)
    private BigDecimal porcentajeIva;

    @Column(name = "precio_unitario_con_iva", precision = 15, scale = 2, nullable = false)
    private BigDecimal precioUnitarioConIva;

    @Column(name = "total_sin_impuestos", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalSinImpuestos;

    public void calcularPrecioConIva() {
        if (precioSinIva != null && porcentajeIva != null) {
            BigDecimal factorIva = BigDecimal.ONE.add(porcentajeIva.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            this.precioUnitarioConIva = precioSinIva.multiply(factorIva).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public void calcularTotal() {
        if (precioSinIva != null && cantidad != null) {
            this.totalSinImpuestos = precioSinIva.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);
        }
    }

    public void calcularTotales() {
        calcularPrecioConIva();
        calcularTotal();
    }
}