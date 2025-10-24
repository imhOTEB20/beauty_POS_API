package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private Integer idUsuario;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
    private Integer idSucursal;
    private String nombreSucursal;
}