package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de caja completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CajaResponse {
    private Integer idCaja;
    private String numeroCaja;
    private Integer idSucursal;
    private String nombreSucursal;
    private String descripcion;
    private Boolean activo;
    private BigDecimal saldoActual;
    private LocalDateTime fechaCreacion;
}