// ==========================================
// UpdatePresupuestoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO para actualizar un presupuesto existente
 */
public record UpdatePresupuestoRequest(
        @Size(max = 30, message = "El número de presupuesto no puede exceder los 30 caracteres")
        String nroPresupuesto,

        Integer idCliente,

        LocalDate fechaPresupuesto,

        Integer idListaPrecio,

        String observaciones,

        String estado
) {}