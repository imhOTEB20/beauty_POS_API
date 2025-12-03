// ==========================================
// CreatePresupuestoRequest.java
// ==========================================
package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear un nuevo presupuesto
 */
public record CreatePresupuestoRequest(
        @NotBlank(message = "El número de presupuesto es obligatorio")
        @Size(max = 30, message = "El número de presupuesto no puede exceder los 30 caracteres")
        String nroPresupuesto,

        @NotNull(message = "El ID de cliente es obligatorio")
        Integer idCliente,

        @NotNull(message = "El ID de usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "El ID de sucursal es obligatorio")
        Integer idSucursal,

        @NotNull(message = "La fecha del presupuesto es obligatoria")
        LocalDate fechaPresupuesto,

        @NotNull(message = "El ID de lista de precios es obligatorio")
        Integer idListaPrecio,

        String observaciones,

        @NotEmpty(message = "Debe incluir al menos un detalle")
        @Valid
        List<PresupuestoDetalleRequest> detalles
) {
    public CreatePresupuestoRequest(
            String nroPresupuesto,
            Integer idCliente,
            Integer idUsuario,
            Integer idSucursal,
            Integer idListaPrecio,
            List<PresupuestoDetalleRequest> detalles
    ) {
        this(nroPresupuesto, idCliente, idUsuario, idSucursal,
                LocalDate.now(), idListaPrecio, null, detalles);
    }
}