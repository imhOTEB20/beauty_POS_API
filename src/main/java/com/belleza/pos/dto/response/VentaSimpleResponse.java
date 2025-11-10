package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaSimpleResponse {
    private Integer idVenta;
    private String nroTransaccion;
    private String nroComprobante;
    private String tipoComprobante;
    private String nombreCliente;
    private String nombreSucursal;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fechaVenta;
}