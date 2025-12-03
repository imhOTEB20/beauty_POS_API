package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de estado actual de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCajaResponse {
    private Integer idCaja;
    private String numeroCaja;
    private String nombreSucursal;
    private Boolean activo;
    private Boolean abierta;
    private BigDecimal saldoActual;
    private LocalDateTime fechaUltimoMovimiento;
    private String tipoUltimoMovimiento;
    private String nombreUsuarioActual;
}