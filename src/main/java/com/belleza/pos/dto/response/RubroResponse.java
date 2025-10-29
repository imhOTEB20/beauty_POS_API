package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de rubro
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RubroResponse {

    private Integer idRubro;
    private String nombre;
    private String descripcion;
    private BigDecimal ivaPorcentaje;
    private Boolean activo;
    private Long cantidadArticulos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
