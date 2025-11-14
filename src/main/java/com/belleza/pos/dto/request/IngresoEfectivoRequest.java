package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para ingreso de efectivo a caja
 */
public record IngresoEfectivoRequest(
        @NotNull(message = "El ID de caja es obligatorio")
        Integer idCaja,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        String observaciones
) {}