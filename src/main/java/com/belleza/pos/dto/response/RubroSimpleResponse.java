package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de rubro para listados
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RubroSimpleResponse {

    private Integer idRubro;
    private String nombre;
    private Boolean activo;
}
