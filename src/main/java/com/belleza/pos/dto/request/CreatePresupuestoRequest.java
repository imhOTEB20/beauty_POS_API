package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear un nuevo presupuesto
 */
public record CreatePresupuestoRequest(

        @NotBlank(message = "El número de presupuesto es obligatorio")
        @Size(max = 30, message = "El número de presupuesto no puede exceder los 30 caracteres")
        String nroPresupuesto,

        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        @NotNull(message = "La sucursal es obligatoria")
        Integer idSucursal,

        @NotNull(message = "La fecha del presupuesto es obligatoria")
        LocalDate fechaPresupuesto,

        @NotNull(message = "La lista de precios es obligatoria")
        Integer idListaPrecio,

        @NotNull(message = "Los detalles del presupuesto son obligatorios")
        @NotEmpty(message = "Debe incluir al menos un artículo en el presupuesto")
        @Valid
        List<PresupuestoDetalleRequest> detalles,

        String estado, // PENDIENTE, APROBADO, RECHAZADO

        @Size(max = 5000, message = "Las observaciones no pueden exceder los 5000 caracteres")
        String observaciones
) {
    public CreatePresupuestoRequest {
        // Valores por defecto
        if (estado == null || estado.isBlank()) estado = "PENDIENTE";
    }
}