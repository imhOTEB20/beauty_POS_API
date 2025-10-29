package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para artículos con stock bajo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloStockBajoResponse {

    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal diferencia;
    private String estadoStock;
}