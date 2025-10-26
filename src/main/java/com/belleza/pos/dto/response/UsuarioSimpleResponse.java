package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de usuario para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSimpleResponse {

    private Integer idUsuario;
    private String username;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
}