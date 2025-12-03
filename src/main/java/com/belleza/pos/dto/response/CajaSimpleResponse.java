package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para respuesta simple de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaSimpleResponse {
    private Integer idCaja;
    private String numeroCaja;
    private String nombreSucursal;
    private String descripcion;
    private Boolean activo;
    private BigDecimal saldoActual;
}