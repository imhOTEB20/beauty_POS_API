package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de movimiento de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCajaResponse {
    private Integer idMovimiento;
    private Integer idCaja;
    private String numeroCaja;
    private Integer idUsuario;
    private String nombreUsuario;
    private String tipoMovimiento;
    private String concepto;
    private BigDecimal montoIngreso;
    private BigDecimal montoEgreso;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoActual;
    private LocalDateTime fechaHora;
    private String observaciones;
    private Integer idVenta;
    private Integer idCompra;
}
