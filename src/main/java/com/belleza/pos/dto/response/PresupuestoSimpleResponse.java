// ==========================================
// PresupuestoSimpleResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuesta simple de presupuesto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoSimpleResponse {
    private Integer idPresupuesto;
    private String nroPresupuesto;
    private LocalDate fechaPresupuesto;
    private String nombreCliente;
    private BigDecimal total;
    private String estado;
    private Boolean vigente;
}
