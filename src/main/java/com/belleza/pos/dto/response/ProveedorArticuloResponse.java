package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de proveedor de artículo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticuloResponse {

    private Integer id;
    private Integer idProveedor;
    private String razonSocial;
    private String nombreComercial;
    private BigDecimal costo;
    private Boolean esPredeterminado;
    private LocalDateTime ultimaActualizacion;
}