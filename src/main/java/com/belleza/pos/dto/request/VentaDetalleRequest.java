package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VentaDetalleRequest(
        @NotNull(message = "El ID del artículo es obligatorio")
        Integer idArticulo,

        @NotNull(message = "La cantidad es obligatoria")
        @DecimalMin(value = "0.001", message = "La cantidad debe ser mayor a cero")
        BigDecimal cantidad,

        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio unitario no puede ser negativo")
        BigDecimal precioUnitario,

        @DecimalMin(value = "0.0", message = "El descuento por porcentaje no puede ser negativo")
        BigDecimal descuentoPorcentaje,

        @DecimalMin(value = "0.0", message = "El descuento en monto no puede ser negativo")
        BigDecimal descuentoMonto
) {
    public VentaDetalleRequest {
        if (descuentoPorcentaje == null) descuentoPorcentaje = BigDecimal.ZERO;
        if (descuentoMonto == null) descuentoMonto = BigDecimal.ZERO;
    }
}
