package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO simplificado de artículo para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloSimpleResponse {

    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;
    private String nombreRubro;
    private BigDecimal precioVenta; // Precio de lista predeterminada
    private BigDecimal stockActual;
    private Boolean activo;
}
