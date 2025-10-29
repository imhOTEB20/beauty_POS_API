package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar una sucursal existente
 * @param nombre
 * @param direccion
 * @param telefono
 * @param email
 * @param activo
 */
public record UpdateSucursalRequest(
        @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
        String nombre,

        @Size(max = 255, message = "La dirección no puede exceder los 255 caracteres")
        String direccion,

        @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
        String telefono,

        @Email(message = "El email debe ser válido")
        @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
        String email,

        Boolean activo
) {}