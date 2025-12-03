// ==========================================
// NotaCreditoDetalleResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta de detalle de nota de crédito
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaCreditoDetalleResponse {
    private Integer idDetalle;
    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
}
