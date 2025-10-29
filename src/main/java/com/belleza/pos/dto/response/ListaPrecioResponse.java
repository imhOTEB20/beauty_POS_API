package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO respuesta datos completo de lista
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaPrecioResponse {

    private Integer idLista;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Boolean esPredeterminada;
    private Long cantidadArticulosConPrecio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
