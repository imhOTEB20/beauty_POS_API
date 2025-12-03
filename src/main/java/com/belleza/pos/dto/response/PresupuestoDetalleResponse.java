// ==========================================
// PresupuestoDetalleResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de detalle de presupuesto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoDetalleResponse {
    private Integer idDetalle;
    private Integer numeroLinea;
    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioSinIva;
    private BigDecimal porcentajeIva;
    private BigDecimal precioUnitarioConIva;
    private BigDecimal totalSinImpuestos;
    private BigDecimal totalConIva;
}