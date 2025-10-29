package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaPrecioSimpleResponse {

    private Integer idLista;
    private String nombre;
    private Boolean esPredeterminada;
    private Boolean activo;
}
