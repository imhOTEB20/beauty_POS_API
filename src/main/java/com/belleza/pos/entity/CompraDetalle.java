package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Entidad CompraDetalle - Líneas de la compra
 */
@Entity
@Table(name = "compras_detalle")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Integer idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_compra", nullable = false)
    private Compra compra;

    @Column(name = "numero_linea", nullable = false)
    private Integer numeroLinea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulo articulo;

    @Column(name = "codigo_barras", nullable = false, length = 50)
    private String codigoBarras;

    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 3)
    private BigDecimal cantidad;

    @Column(name = "precio_sin_iva", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioSinIva;

    @Column(name = "porcentaje_iva", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeIva;

    @Column(name = "precio_unitario_con_iva", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitarioConIva;

    @Column(name = "total_sin_impuestos", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalSinImpuestos;

    /**
     * Calcula el precio unitario con IVA
     */
    public void calcularPrecioConIva() {
        if (precioSinIva != null && porcentajeIva != null) {
            BigDecimal factorIva = BigDecimal.ONE.add(porcentajeIva.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            this.precioUnitarioConIva = precioSinIva.multiply(factorIva).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Calcula el total sin impuestos de la línea
     */
    public void calcularTotal() {
        if (precioSinIva != null && cantidad != null) {
            this.totalSinImpuestos = precioSinIva.multiply(cantidad).setScale(2, RoundingMode.HALF_UP);
        }
    }

    /**
     * Calcula todos los valores derivados
     */
    public void calcularTotales() {
        calcularPrecioConIva();
        calcularTotal();
    }
}
