package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO simplificado de cliente para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteSimpleResponse {

    private Integer idCliente;
    private String nroCliente;
    private String nombreCompleto;
    private String nroDocumento;
    private String telefono;
    private BigDecimal saldoActual;
    private Boolean activo;
}
