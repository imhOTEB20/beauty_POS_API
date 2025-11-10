package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record VentaFormaPagoRequest(
        @NotBlank(message = "La forma de pago es obligatoria")
        String formaPago,

        @NotNull(message = "El monto es obligatorio")
        @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
        BigDecimal monto,

        @Size(max = 255, message = "El detalle no puede exceder los 255 caracteres")
        String detalle
) {
}