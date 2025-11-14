package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar una caja existente
 */
public record UpdateCajaRequest(
        @Size(max = 20, message = "El número de caja no puede exceder los 20 caracteres")
        String numeroCaja,

        Integer idSucursal,

        @Size(max = 100, message = "La descripción no puede exceder los 100 caracteres")
        String descripcion,

        Boolean activo
) {}
