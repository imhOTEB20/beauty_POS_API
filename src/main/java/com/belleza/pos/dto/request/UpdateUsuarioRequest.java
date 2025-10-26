package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar un usuario existente
 * @param nombre
 * @param apellido
 * @param email
 * @param rol
 * @param idSucursal
 * @param activo
 */

public record UpdateUsuarioRequest (

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,

    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    String apellido,

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    String email,

    String rol,

    Integer idSucursal,

    Boolean activo
) {}