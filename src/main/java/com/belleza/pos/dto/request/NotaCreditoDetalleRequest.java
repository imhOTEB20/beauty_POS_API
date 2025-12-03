// ==========================================
// NotaCreditoDetalleRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para detalle de nota de crédito
 */
public record NotaCreditoDetalleRequest(
        Integer idArticulo,

        @Size(max = 50, message = "El código de barras no puede exceder los 50 caracteres")
        String codigoBarras,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
        String descripcion,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
        BigDecimal precioUnitario
) {
    public BigDecimal calcularTotal() {
        return cantidad.multiply(precioUnitario);
    }
}