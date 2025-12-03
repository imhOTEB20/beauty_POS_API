// ==========================================
// NotaCreditoResponse.java
// ==========================================
package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuesta de nota de crédito completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaCreditoResponse {
    private Integer idNotaCredito;
    private String tipoComprobante;
    private String nroComprobante;
    private LocalDate fecha;

    // Cliente
    private Integer idCliente;
    private String nombreCliente;
    private String documentoCliente;

    // Usuario
    private Integer idUsuario;
    private String nombreUsuario;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Comprobante asociado
    private String tipoComprobanteAsociado;
    private String puntoVentaAsociado;
    private String nroComprobanteAsociado;
    private String comprobanteAsociadoCompleto;

    // Totales
    private BigDecimal total;

    // Estado
    private String estado;
    private String observaciones;

    // Detalles
    private List<NotaCreditoDetalleResponse> detalles;

    // Auditoría
    private LocalDateTime fechaCreacion;
}