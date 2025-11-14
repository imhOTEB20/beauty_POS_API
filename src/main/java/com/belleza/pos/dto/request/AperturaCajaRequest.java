package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para apertura de caja
 */
public record AperturaCajaRequest(
        @NotNull(message = "El ID de caja es obligatorio")
        Integer idCaja,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El monto inicial es obligatorio")
        @DecimalMin(value = "0.0", message = "El monto inicial no puede ser negativo")
        BigDecimal montoInicial,

        String observaciones
) {}
