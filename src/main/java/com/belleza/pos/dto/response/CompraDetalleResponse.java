package com.belleza.pos.dto.response;

import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de detalle de compra
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompraDetalleResponse extends DetalleOperacionResponse {

    private Integer idCompra;
}