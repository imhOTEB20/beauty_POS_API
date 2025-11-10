package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de proveedor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorResponse {

    private Integer idProveedor;
    private String nroProveedor;
    private String razonSocial;
    private String nombreComercial;
    private String cuit;
    private String telefono;
    private String celular;
    private String email;
    private String paginaWeb;
    private String personaContacto;
    private String calle;
    private String numero;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String direccionCompleta;
    private String informacionDomicilioAdicional;
    private Boolean cuentaCorrienteHabilitada;
    private BigDecimal limiteCredito;
    private BigDecimal saldoActual;
    private BigDecimal creditoDisponible;
    private Integer diasPlazoPago;
    private String condicionIva;
    private String ingresosBrutos;
    private String estadoCredito;
    private String observaciones;
    private Boolean activo;
    private Long cantidadArticulos;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}