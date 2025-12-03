// ==========================================
// ConvertirPresupuestoVentaRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para convertir un presupuesto en venta
 */
public record ConvertirPresupuestoVentaRequest(
        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        String observaciones
) {}
