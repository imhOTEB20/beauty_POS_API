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
public class VentaFormaPagoResponse {
    private Integer id;
    private String formaPago;
    private String formaPagoDescripcion;
    private BigDecimal monto;
    private String detalle;
}
