// ==========================================
// UpdateNotaCreditoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para actualizar una nota de crédito existente
 */
public record UpdateNotaCreditoRequest(
        @Size(max = 30, message = "El tipo de comprobante no puede exceder los 30 caracteres")
        String tipoComprobante,

        @Size(max = 30, message = "El número de comprobante no puede exceder los 30 caracteres")
        String nroComprobante,

        LocalDate fecha,

        Integer idCliente,

        @Size(max = 30, message = "El tipo de comprobante asociado no puede exceder los 30 caracteres")
        String tipoComprobanteAsociado,

        @Size(max = 10, message = "El punto de venta no puede exceder los 10 caracteres")
        String puntoVentaAsociado,

        @Size(max = 30, message = "El número de comprobante asociado no puede exceder los 30 caracteres")
        String nroComprobanteAsociado,

        String observaciones,

        String estado
) {}
