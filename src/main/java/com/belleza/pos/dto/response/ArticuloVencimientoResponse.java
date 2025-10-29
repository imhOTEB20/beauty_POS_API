package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para artículos próximos a vencer
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloVencimientoResponse {

    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private LocalDate fechaVencimiento;
    private Integer diasRestantes;
    private BigDecimal stockActual;
    private String estadoVencimiento; // VENCIDO, CRITICO, PROXIMO
}
