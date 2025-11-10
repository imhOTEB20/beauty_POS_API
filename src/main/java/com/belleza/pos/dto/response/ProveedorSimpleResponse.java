package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO simplificado de proveedor para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorSimpleResponse {

    private Integer idProveedor;
    private String nroProveedor;
    private String razonSocial;
    private String nombreComercial;
    private String cuit;
    private String telefono;
    private BigDecimal saldoActual;
    private Boolean activo;
}
