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
 * DTO para respuesta de artículo completo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloResponse {

    private Integer idArticulo;
    private String codigoBarras;
    private String descripcion;

    // Rubro
    private Integer idRubro;
    private String nombreRubro;

    // Configuración
    private String unidadVenta;
    private Boolean usaControlStock;

    // Stock
    private BigDecimal stockActual;
    private BigDecimal stockMinimo;
    private BigDecimal stockMaximo;
    private String estadoStock; // OK, BAJO, CRITICO

    // Vencimiento
    private LocalDate fechaVencimiento;
    private Integer diasParaVencer;
    private Boolean proximoAVencer;

    // Imagen
    private String imagenUrl;

    // Web
    private Boolean publicarEnWeb;
    private Boolean enOferta;

    // Estado
    private Boolean activo;

    // Precios
    private List<PrecioResponse> precios;

    // Proveedores
    private List<ProveedorArticuloResponse> proveedores;

    // Auditoría
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}