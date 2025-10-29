package com.belleza.pos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de cliente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Integer idCliente;
    private String nroCliente;
    private String tipoCuenta;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String tipoDocumento;
    private String nroDocumento;
    private String telefono;
    private String celular;
    private String email;
    private String calle;
    private String numero;
    private String localidad;
    private String provincia;
    private String codigoPostal;
    private String direccionCompleta;
    private Boolean cuentaCorrienteHabilitada;
    private String tipoLimite;
    private BigDecimal limiteCredito;
    private BigDecimal saldoActual;
    private BigDecimal creditoDisponible;
    private Integer diasPlazoPago;
    private String condicionIva;
    private String estadoCredito; // NORMAL, ALERTA, EXCEDIDO
    private String observaciones;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
