package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambiar la contraseña de un usuario
 * @param currentPassword
 * @param newPassword
 * @param confirmPassword
 */

public record ChangePasswordRequest (

    @NotBlank(message = "La contraseña actual es obligatoria")
    String currentPassword,

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    String newPassword,

    @NotBlank(message = "Debe confirmar la nueva contraseña")
    String confirmPassword
) {}