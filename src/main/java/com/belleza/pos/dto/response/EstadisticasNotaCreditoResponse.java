// ==========================================
// EstadisticasNotaCreditoResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuesta de estadísticas de notas de crédito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasNotaCreditoResponse {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer cantidadTotal;
    private Integer cantidadActivas;
    private Integer cantidadAnuladas;
    private BigDecimal totalActivas;
    private BigDecimal totalAnuladas;
    private BigDecimal totalGeneral;
}