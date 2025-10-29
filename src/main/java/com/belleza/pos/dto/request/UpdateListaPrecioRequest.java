package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar una lista de precios
 * @param nombre
 * @param descripcion
 * @param activo
 * @param esPredeterminada
 */

public record UpdateListaPrecioRequest (

    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    String nombre,

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    String descripcion,

    Boolean activo,

    Boolean esPredeterminada
) {}
