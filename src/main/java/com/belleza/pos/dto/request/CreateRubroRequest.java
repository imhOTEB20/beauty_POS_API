package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para crear un nuevo rubro
 * @param nombre
 * @param descripcion
 * @param ivaPorcentaje
 * @param activo
 */

public record CreateRubroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        String nombre,

        String descripcion,

        @DecimalMin(value = "0.0", message = "El IVA no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El IVA no puede exceder el 100%")
        BigDecimal ivaPorcentaje,

        Boolean activo
) {
    public CreateRubroRequest(String nombre, String descripcion) {
        this(nombre, descripcion, BigDecimal.valueOf(21.00), true);
    }
}