package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de sucursal para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalSimpleResponse {

    private Integer idSucursal;
    private String nombre;
    private String telefono;
    private Boolean activo;
}