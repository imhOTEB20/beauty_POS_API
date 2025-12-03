// ==========================================
// AprobarPresupuestoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para aprobar un presupuesto
 */
public record AprobarPresupuestoRequest(
        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        String observaciones
) {}