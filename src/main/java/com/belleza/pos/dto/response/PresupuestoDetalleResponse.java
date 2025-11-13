package com.belleza.pos.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * DTO para respuesta de detalle de presupuesto
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PresupuestoDetalleResponse extends DetalleOperacionResponse {

    private Integer idPresupuesto;
}