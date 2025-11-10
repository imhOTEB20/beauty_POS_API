package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateVentaRequest(
        @Size(max = 30, message = "El número de comprobante no puede exceder los 30 caracteres")
        String nroComprobante,

        @Size(max = 20, message = "El CAE no puede exceder los 20 caracteres")
        String cae,

        LocalDate fechaVencimientoCae,

        String estado,

        @Size(max = 5000, message = "Las observaciones no pueden exceder los 5000 caracteres")
        String observaciones
) {
}