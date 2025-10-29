package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para actualizar un artículo existente
 * @param codigoBarras
 * @param descripcion
 * @param idRubro
 * @param unidadVenta
 * @param usaControlStock
 * @param stockActual
 * @param stockMinimo
 * @param stockMaximo
 * @param fechaVencimiento
 * @param imagenUrl
 * @param publicarEnWeb
 * @param enOferta
 * @param activo
 */

public record UpdateArticuloRequest(
        @Size(max = 50, message = "El código de barras no puede exceder los 50 caracteres")
        String codigoBarras,

        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
        String descripcion,

        Integer idRubro,

        String unidadVenta,

        Boolean usaControlStock,

        @DecimalMin(value = "0.0", message = "El stock no puede ser negativo")
        BigDecimal stockActual,

        @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
        BigDecimal stockMinimo,

        @DecimalMin(value = "0.0", message = "El stock máximo no puede ser negativo")
        BigDecimal stockMaximo,

        LocalDate fechaVencimiento,

        @Size(max = 500, message = "La URL de imagen no puede exceder los 500 caracteres")
        String imagenUrl,

        Boolean publicarEnWeb,

        Boolean enOferta,

        Boolean activo
) {}