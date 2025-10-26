package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitud de refresh token
 * @param refreshToken
 */

public record RefreshTokenRequest (
        @NotBlank(message = "El refresh token es obligatorio")
        String refreshToken
) {}