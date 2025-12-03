// ==========================================
// PresupuestoResponse.java
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
    private String documentoCliente;

    // Usuario
    private Integer idUsuario;
    private String nombreUsuario;

    // Sucursal
    private Integer idSucursal;
    private String nombreSucursal;

    // Lista de precios
    private Integer idListaPrecio;
    private String nombreListaPrecio;

    // Fecha
    private LocalDate fechaPresupuesto;

    // Totales
    private BigDecimal subtotal;
    private BigDecimal iva21;
    private BigDecimal iva105;
    private BigDecimal total;

    // Estado
    private String estado;
    private Integer idVentaGenerada;
    private String observaciones;

    // Detalles
    private List<PresupuestoDetalleResponse> detalles;

    // Auditoría
    private LocalDateTime fechaCreacion;

    // Validez
    private Integer diasValidez;
    private Boolean vigente;
}