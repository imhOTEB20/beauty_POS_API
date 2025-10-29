package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para precio de artículo en una lista
 * @param idLista
 * @param precioVenta
 * @param precioCosto
 * @param porcentajeUtilidad
 */

public record PrecioRequest(

    @NotNull(message = "El ID de lista de precios es obligatorio")
    Integer idLista,

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0")
    BigDecimal precioVenta,

    @DecimalMin(value = "0.0", message = "El precio de costo no puede ser negativo")
    BigDecimal precioCosto,

    @DecimalMin(value = "0.0", message = "El porcentaje de utilidad no puede ser negativo")
    BigDecimal porcentajeUtilidad
) {
    public PrecioRequest(Integer idLista, BigDecimal precioVenta) {
        this(
                idLista,
                precioVenta,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }
}
