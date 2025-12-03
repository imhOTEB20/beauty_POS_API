// ==========================================
// CreateNotaCreditoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear una nueva nota de crédito
 */
public record CreateNotaCreditoRequest(
        @NotBlank(message = "El tipo de comprobante es obligatorio")
        @Size(max = 30, message = "El tipo de comprobante no puede exceder los 30 caracteres")
        String tipoComprobante,

        @Size(max = 30, message = "El número de comprobante no puede exceder los 30 caracteres")
        String nroComprobante,

        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        Integer idCliente,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El ID de sucursal es obligatorio")
        Integer idSucursal,

        // Comprobante asociado (opcional)
        @Size(max = 30, message = "El tipo de comprobante asociado no puede exceder los 30 caracteres")
        String tipoComprobanteAsociado,

        @Size(max = 10, message = "El punto de venta no puede exceder los 10 caracteres")
        String puntoVentaAsociado,

        @Size(max = 30, message = "El número de comprobante asociado no puede exceder los 30 caracteres")
        String nroComprobanteAsociado,

        String observaciones,

        @NotEmpty(message = "Debe incluir al menos un detalle")
        @Valid
        List<NotaCreditoDetalleRequest> detalles
) {
    public CreateNotaCreditoRequest(
            String tipoComprobante,
            LocalDate fecha,
            Integer idCliente,
            Integer idUsuario,
            Integer idSucursal,
            List<NotaCreditoDetalleRequest> detalles
    ) {
        this(tipoComprobante, null, fecha, idCliente, idUsuario, idSucursal,
                null, null, null, null, detalles);
    }
}