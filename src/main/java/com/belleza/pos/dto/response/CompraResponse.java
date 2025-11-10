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
 * DTO para respuesta de compra completa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponse {

    private Integer idCompra;

    // Proveedor
    private Integer idProveedor;
    private String razonSocialProveedor;
    private String nombreComercialProveedor;
    private String cuitProveedor;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Usuario
    private Integer idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    // Comprobante
    private String tipoComprobante;
    private String tipoComprobanteDescripcion;
    private String nroComprobante;
    private LocalDate fechaCompra;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal impuestosInternos;
    private BigDecimal iva21;
    private BigDecimal iva105;
    private BigDecimal total;

    // Opciones
    private Boolean actualizarPrecios;
    private Boolean actualizarStock;

    // Forma de pago
    private String formaPago;
    private String formaPagoDescripcion;

    // Estado
    private String estado;
    private String estadoDescripcion;
    private LocalDateTime fechaCreacion;
    private String observaciones;

    // Detalles
    private List<CompraDetalleResponse> detalles;

    // Información adicional
    private Integer cantidadArticulos;
}