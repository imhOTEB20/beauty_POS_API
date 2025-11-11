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
 * DTO para respuesta de presupuesto completo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoResponse {

    private Integer idPresupuesto;
    private String nroPresupuesto;

    // Cliente
    private Integer idCliente;
    private String nombreCliente;
    private String apellidoCliente;
    private String nroDocumentoCliente;

    // Usuario
    private Integer idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Fecha
    private LocalDate fechaPresupuesto;

    // Lista de precios
    private Integer idListaPrecio;
    private String nombreListaPrecio;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal iva21;
    private BigDecimal iva105;
    private BigDecimal total;

    // Estado
    private String estado;
    private String estadoDescripcion;

    // Venta generada
    private Integer idVentaGenerada;
    private String nroTransaccionVenta;

    // Auditoría
    private LocalDateTime fechaCreacion;
    private String observaciones;

    // Detalles
    private List<PresupuestoDetalleResponse> detalles;

    // Información adicional
    private Integer cantidadArticulos;
}
