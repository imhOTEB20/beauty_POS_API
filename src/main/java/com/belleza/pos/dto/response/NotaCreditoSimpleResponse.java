// ==========================================
// NotaCreditoSimpleResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuesta simple de nota de crédito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaCreditoSimpleResponse {
    private Integer idNotaCredito;
    private String tipoComprobante;
    private String nroComprobante;
    private LocalDate fecha;
    private String nombreCliente;
    private BigDecimal total;
    private String estado;
}