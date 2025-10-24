package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de login
 * @param username
 * @param password
 */

public record LoginRequest(
        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {}