package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de precio de artículo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrecioResponse {

    private Integer idPrecio;
    private Integer idLista;
    private String nombreLista;
    private BigDecimal precioCosto;
    private BigDecimal precioVenta;
    private BigDecimal precioConIva;
    private BigDecimal porcentajeUtilidad;
    private LocalDateTime fechaUltimaActualizacion;
}