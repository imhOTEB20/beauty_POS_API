package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear un nuevo artículo
 * @param codigoBarras
 * @param descripcion
 * @param idRubro
 * @param fechaVencimiento
 * @param imagenUrl
 * @param precios
 * @param proveedores
 * @param unidadVenta
 * @param usaControlStock
 * @param stockActual
 * @param stockMinimo
 * @param stockMaximo
 * @param publicarEnWeb
 * @param enOferta
 * @param activo
 */

public record CreateArticuloRequest(

    @NotBlank(message = "El código de barras es obligatorio")
    @Size(max = 50, message = "El código de barras no puede exceder los 50 caracteres")
    String codigoBarras,

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    String descripcion,

    Integer idRubro,

    LocalDate fechaVencimiento,

    @Size(max = 500, message = "La URL de imagen no puede exceder los 500 caracteres")
    String imagenUrl,

    // Precios por lista
    List<PrecioRequest> precios,

    // Proveedores
    List<ProveedorArticuloRequest> proveedores,

    String unidadVenta, // UNIDAD o KG

    Boolean usaControlStock,

    @DecimalMin(value = "0.0", message = "El stock no puede ser negativo")
    BigDecimal stockActual,

    @DecimalMin(value = "0.0", message = "El stock mínimo no puede ser negativo")
    BigDecimal stockMinimo,

    @DecimalMin(value = "0.0", message = "El stock máximo no puede ser negativo")
    BigDecimal stockMaximo,

    Boolean publicarEnWeb,

    Boolean enOferta,

    Boolean activo
) {
    public CreateArticuloRequest(
            String codigoBarras,
            String descripcion,
            Integer idRubro,
            LocalDate fechaVencimiento,
            String imagenUrl,
            List<PrecioRequest> precios,
            List<ProveedorArticuloRequest> proveedores
    ) {
        this(
                codigoBarras,
                descripcion,
                idRubro,
                fechaVencimiento,
                imagenUrl,
                precios,
                proveedores,
                "UNIDAD",
                false,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                false,
                false,
                true);
    }
}