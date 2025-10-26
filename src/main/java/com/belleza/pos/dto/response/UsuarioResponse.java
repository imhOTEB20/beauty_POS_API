package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Integer idUsuario;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private Integer idSucursal;
    private String nombreSucursal;
    private Boolean activo;
    private LocalDateTime ultimoLogin;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
