package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para proveedor de artículo
 * @param idProveedor
 * @param costo
 * @param esPredeterminado
 */

public record ProveedorArticuloRequest(

    @NotNull(message = "El ID del proveedor es obligatorio")
    Integer idProveedor,

    @DecimalMin(value = "0.0", message = "El costo no puede ser negativo")
    BigDecimal costo,

    Boolean esPredeterminado
) {
    public ProveedorArticuloRequest(Integer idProveedor) {
        this(idProveedor, BigDecimal.ZERO, false);
    }
}