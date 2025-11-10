package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para con los detalles de la compra de un articulo
 */

public record CompraDetalleRequest(
        @NotBlank
        Integer idArticulo,

        @NotNull
        BigDecimal cantidad,

        @NotNull
        BigDecimal precioSinIva,

        @NotNull
        BigDecimal porcentajeIva
) {}
