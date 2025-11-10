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
 * DTO para respuesta de venta completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponse {

    private Integer idVenta;
    private String nroTransaccion;
    private String nroCaja;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Usuario
    private Integer idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    // Cliente
    private Integer idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String nroDocumentoCliente;

    // Comprobante
    private String tipoComprobante;
    private String tipoComprobanteDescripcion;
    private String nroComprobante;
    private String cae;
    private LocalDate fechaVencimientoCae;

    // Lista de precios
    private Integer idListaPrecio;
    private String nombreListaPrecio;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal descuentoPorcentaje;
    private BigDecimal descuentoMonto;
    private BigDecimal recargoPorcentaje;
    private BigDecimal recargoMonto;
    private BigDecimal total;

    // Estado
    private String estado;
    private String estadoDescripcion;
    private LocalDateTime fechaVenta;
    private String observaciones;

    // Detalles
    private List<VentaDetalleResponse> detalles;

    // Formas de pago
    private List<VentaFormaPagoResponse> formasPago;

    // Información adicional
    private Integer cantidadArticulos;
    private BigDecimal totalPagado;
    private BigDecimal vuelto; // Si el pago es en efectivo y sobra
}
