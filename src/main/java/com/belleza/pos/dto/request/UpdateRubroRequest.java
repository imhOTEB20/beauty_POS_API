package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para actualizar un rubro existente
 * @param nombre
 * @param descripcion
 * @param ivaPorcentaje
 * @param activo
 */

public record UpdateRubroRequest (

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,

    String descripcion,

    @DecimalMin(value = "0.0", message = "El IVA no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El IVA no puede exceder el 100%")
    BigDecimal ivaPorcentaje,

    Boolean activo
) {}