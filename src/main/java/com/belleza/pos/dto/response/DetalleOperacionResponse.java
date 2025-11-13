package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Clase base para detalles de operaciones (compra, presupuesto, etc.)
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DetalleOperacionResponse {

    private Integer idDetalle;
    private Integer numeroLinea;
    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioSinIva;
    private BigDecimal porcentajeIva;
    private BigDecimal precioUnitarioSinIva;
    private BigDecimal precioUnitarioConIva;
    private BigDecimal totalSinImpuestos;
}