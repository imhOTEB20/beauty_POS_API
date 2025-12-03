// ==========================================
// EstadisticasPresupuestoResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuesta de estadísticas de presupuestos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPresupuestoResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadTotal;
    private Integer cantidadPendientes;
    private Integer cantidadAprobados;
    private Integer cantidadRechazados;
    private Integer cantidadConvertidos;
    private BigDecimal totalPendientes;
    private BigDecimal totalAprobados;
    private BigDecimal totalRechazados;
    private BigDecimal totalConvertidos;
    private BigDecimal totalGeneral;
    private BigDecimal tasaConversion; // Porcentaje de presupuestos convertidos en venta
}