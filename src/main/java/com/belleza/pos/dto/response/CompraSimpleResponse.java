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
 * DTO para respuesta simple de compra
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraSimpleResponse {

    private Integer idCompra;

    // Proveedor
    private String razonSocialProveedor;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Usuario
    private Integer idUsuario;
    private String username;

    // Comprobante
    private String nroComprobante;
    private String tipoComprobante;
    private LocalDate fechaCompra;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal impuestosInternos;
    private BigDecimal iva21;
    private BigDecimal iva105;
    private BigDecimal total;

    // Forma de pago
    private String formaPago;

    // Estado
    private String estado;
    private LocalDateTime fechaCreacion;

    // Detalles
    private List<CompraDetalleResponse> detalles;
}