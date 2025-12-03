// ==========================================
// PresupuestoDetalleRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para detalle de presupuesto
 */
public record PresupuestoDetalleRequest(
        @NotNull(message = "El número de línea es obligatorio")
        Integer numeroLinea,

        @NotNull(message = "El ID de artículo es obligatorio")
        Integer idArticulo,

        @NotBlank(message = "El código de barras es obligatorio")
        @Size(max = 50, message = "El código de barras no puede exceder los 50 caracteres")
        String codigoBarras,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
        String descripcion,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad,

        @NotNull(message = "El precio sin IVA es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio sin IVA no puede ser negativo")
        BigDecimal precioSinIva,

        @NotNull(message = "El porcentaje de IVA es obligatorio")
        @DecimalMin(value = "0.0", message = "El porcentaje de IVA no puede ser negativo")
        BigDecimal porcentajeIva
) {
    public BigDecimal calcularPrecioConIva() {
        return precioSinIva.add(precioSinIva.multiply(porcentajeIva.divide(BigDecimal.valueOf(100))));
    }

    public BigDecimal calcularTotalSinImpuestos() {
        return precioSinIva.multiply(cantidad);
    }
}