// ==========================================
// RechazarPresupuestoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para rechazar un presupuesto
 */
public record RechazarPresupuestoRequest(
        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotBlank(message = "El motivo de rechazo es obligatorio")
        String motivo
) {}

