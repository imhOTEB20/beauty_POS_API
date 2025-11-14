package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo movimiento de caja
 */
public record CreateMovimientoCajaRequest(
        @NotNull(message = "El ID de caja es obligatorio")
        Integer idCaja,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotBlank(message = "El tipo de movimiento es obligatorio")
        @Size(max = 50, message = "El tipo de movimiento no puede exceder los 50 caracteres")
        String tipoMovimiento,

        @NotBlank(message = "El concepto es obligatorio")
        @Size(max = 255, message = "El concepto no puede exceder los 255 caracteres")
        String concepto,

        @DecimalMin(value = "0.0", message = "El monto de ingreso no puede ser negativo")
        BigDecimal montoIngreso,

        @DecimalMin(value = "0.0", message = "El monto de egreso no puede ser negativo")
        BigDecimal montoEgreso,

        String observaciones,

        Integer idVenta,

        Integer idCompra
) {
    public CreateMovimientoCajaRequest(
            Integer idCaja,
            Integer idUsuario,
            String tipoMovimiento,
            String concepto,
            BigDecimal montoIngreso,
            BigDecimal montoEgreso
    ) {
        this(idCaja, idUsuario, tipoMovimiento, concepto,
                montoIngreso != null ? montoIngreso : BigDecimal.ZERO,
                montoEgreso != null ? montoEgreso : BigDecimal.ZERO,
                null, null, null);
    }
}
