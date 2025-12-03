// ==========================================
// AnularNotaCreditoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para anular una nota de crédito
 */
public record AnularNotaCreditoRequest(
        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotBlank(message = "El motivo de anulación es obligatorio")
        String motivo
) {}