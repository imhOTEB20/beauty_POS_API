package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
/**
 * DTO para respuesta de detalle de compra
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDetalleResponse {

    private Integer idDetalle;

    private Integer idCompra;

    private Integer numeroLinea;

    private Integer idArticulo;

    private String codigoBarras;

    private String descripcion;

    private BigDecimal cantidad;

    private BigDecimal precioSinIva;

    private BigDecimal porcentajeIva;

    private BigDecimal precioUnitarioSinIva;

    private BigDecimal precioUnitarioConIva;

    private BigDecimal totalSinImpuestos;
}