package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaDetalleResponse {
    private Integer idDetalle;
    private Integer numeroLinea;

    // Artículo
    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;

    // Cantidades y precios
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoMonto;
    private BigDecimal subtotal;
}
