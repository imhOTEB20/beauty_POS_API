package com.belleza.pos.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear una nueva compra
 */
public record CreateCompraRequest(

        @NotNull(message = "El proveedor es obligatorio")
        Integer idProveedor,

        @NotNull(message = "La sucursal es obligatoria")
        Integer idSucursal,

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        @NotBlank(message = "El tipo de comprobante es obligatorio")
        String tipoComprobante, // FACTURA_A, FACTURA_B, FACTURA_C, NOTA_DEBITO, NOTA_CREDITO

        @Size(max = 30, message = "El número de comprobante no puede exceder los 30 caracteres")
        String nroComprobante,

        @NotNull(message = "La fecha de compra es obligatoria")
        LocalDate fechaCompra,

        @NotNull(message = "Los detalles de la compra son obligatorios")
        @NotEmpty(message = "Debe incluir al menos un artículo en la compra")
        @Valid
        List<CompraDetalleRequest> detalles,

        @DecimalMin(value = "0.0", message = "Los impuestos internos no pueden ser negativos")
        BigDecimal impuestosInternos,

        Boolean actualizarPrecios,

        Boolean actualizarStock,

        @NotBlank(message = "La forma de pago es obligatoria")
        String formaPago, // EFECTIVO, TARJETA, CHEQUE, TRANSFERENCIA, CUENTA_CORRIENTE

        String estado, // PENDIENTE, COMPLETADA

        @Size(max = 5000, message = "Las observaciones no pueden exceder los 5000 caracteres")
        String observaciones
) {
    public CreateCompraRequest {
        // Valores por defecto
        if (impuestosInternos == null) impuestosInternos = BigDecimal.ZERO;
        if (actualizarPrecios == null) actualizarPrecios = false;
        if (actualizarStock == null) actualizarStock = true;
        if (estado == null || estado.isBlank()) estado = "COMPLETADA";
    }
}