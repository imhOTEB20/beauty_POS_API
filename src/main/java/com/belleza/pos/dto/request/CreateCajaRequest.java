package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear una nueva caja
 */
public record CreateCajaRequest(
        @NotBlank(message = "El número de caja es obligatorio")
        @Size(max = 20, message = "El número de caja no puede exceder los 20 caracteres")
        String numeroCaja,

        @NotNull(message = "El ID de sucursal es obligatorio")
        Integer idSucursal,

        @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
        String descripcion,

        Boolean activo
) {
    public CreateCajaRequest(String numeroCaja, Integer idSucursal, String descripcion) {
        this(numeroCaja, idSucursal, descripcion, true);
    }
}
