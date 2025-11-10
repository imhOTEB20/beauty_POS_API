package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear una nueva venta
 * @param nroTransaccion
 * @param nroCaja
 * @param idSucursal
 * @param idUsuario
 * @param idCliente
 * @param tipoComprobante
 * @param nroComprobante
 * @param cae
 * @param fechaVencimientoCae
 * @param idListaPrecio
 * @param detalles
 * @param formasPago
 * @param descuentoPorcentaje
 * @param descuentoMonto
 * @param recargoPorcentaje
 * @param recargoMonto
 * @param estado
 * @param observaciones
 */
public record CreateVentaRequest(

        @NotBlank(message = "El número de transacción es obligatorio")
        @Size(max = 30, message = "El número de transacción no puede exceder los 30 caracteres")
        String nroTransaccion,

        @Size(max = 20, message = "El número de caja no puede exceder los 20 caracteres")
        String nroCaja,

        @NotNull(message = "La sucursal es obligatoria")
        Integer idSucursal,

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        Integer idCliente, // Opcional

        @NotBlank(message = "El tipo de comprobante es obligatorio")
        String tipoComprobante, // FACTURA_A, FACTURA_B, FACTURA_C, TICKET, etc.

        @Size(max = 30, message = "El número de comprobante no puede exceder los 30 caracteres")
        String nroComprobante,

        @Size(max = 20, message = "El CAE no puede exceder los 20 caracteres")
        String cae,

        LocalDate fechaVencimientoCae,

        @NotNull(message = "La lista de precios es obligatoria")
        Integer idListaPrecio,

        @NotNull(message = "Los detalles de la venta son obligatorios")
        @NotEmpty(message = "Debe incluir al menos un artículo en la venta")
        @Valid
        List<VentaDetalleRequest> detalles,

        @NotNull(message = "Las formas de pago son obligatorias")
        @NotEmpty(message = "Debe incluir al menos una forma de pago")
        @Valid
        List<VentaFormaPagoRequest> formasPago,

        @DecimalMin(value = "0.0", message = "El descuento por porcentaje no puede ser negativo")
        @DecimalMax(value = "100.0", message = "El descuento por porcentaje no puede exceder el 100%")
        BigDecimal descuentoPorcentaje,

        @DecimalMin(value = "0.0", message = "El descuento en monto no puede ser negativo")
        BigDecimal descuentoMonto,

        @DecimalMin(value = "0.0", message = "El recargo por porcentaje no puede ser negativo")
        BigDecimal recargoPorcentaje,

        @DecimalMin(value = "0.0", message = "El recargo en monto no puede ser negativo")
        BigDecimal recargoMonto,

        String estado, // PENDIENTE, COMPLETADA, PRESUPUESTO

        @Size(max = 5000, message = "Las observaciones no pueden exceder los 5000 caracteres")
        String observaciones
) {
    public CreateVentaRequest {
        // Valores por defecto
        if (descuentoPorcentaje == null) descuentoPorcentaje = BigDecimal.ZERO;
        if (descuentoMonto == null) descuentoMonto = BigDecimal.ZERO;
        if (recargoPorcentaje == null) recargoPorcentaje = BigDecimal.ZERO;
        if (recargoMonto == null) recargoMonto = BigDecimal.ZERO;
        if (estado == null || estado.isBlank()) estado = "COMPLETADA";
    }
}