package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para ajustar stock de artículo
 * @param cantidad
 * @param tipoAjuste
 * @param motivo
 */

public record AjusteStockRequest (

    @NotNull(message = "La cantidad es obligatoria")
    BigDecimal cantidad,

    @NotNull(message = "El tipo de ajuste es obligatorio")
    String tipoAjuste, // INGRESO, EGRESO, AJUSTE

    String motivo
) {}