package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para respuesta de resumen de caja
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumenCajaResponse {
    private Integer idCaja;
    private String numeroCaja;
    private String nombreSucursal;
    private LocalDate fecha;
    private BigDecimal saldoInicial;
    private BigDecimal totalIngresos;
    private BigDecimal totalEgresos;
    private BigDecimal saldoFinal;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasTarjetaDebito;
    private BigDecimal ventasTarjetaCredito;
    private BigDecimal retiros;
    private BigDecimal ingresos;
    private Integer cantidadMovimientos;
}
