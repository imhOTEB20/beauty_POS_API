package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de sucursal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SucursalResponse {

    private Integer idSucursal;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
