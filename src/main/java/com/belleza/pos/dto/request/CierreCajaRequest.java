package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para cierre de caja
 */
public record CierreCajaRequest(
        @NotNull(message = "El ID de caja es obligatorio")
        Integer idCaja,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El monto final es obligatorio")
        @DecimalMin(value = "0.0", message = "El monto final no puede ser negativo")
        BigDecimal montoFinal,

        String observaciones
) {}