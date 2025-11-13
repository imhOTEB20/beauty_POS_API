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
 * DTO para respuesta simple de presupuesto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresupuestoSimpleResponse {
    private Integer idPresupuesto;
    private String nroPresupuesto;

    // Cliente
    private Integer idCliente;
    private String nombreCliente;

    // Usuario
    private Integer idUsuario;
    private String username;

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

    // Venta generada
    private Integer idVentaGenerada;
    private String nroTransaccionVenta;

    // Auditoría
    private LocalDateTime fechaCreacion;
    private String observaciones;

    private String estado;

    // Detalles
    private List<PresupuestoDetalleResponse> detalles;
}
