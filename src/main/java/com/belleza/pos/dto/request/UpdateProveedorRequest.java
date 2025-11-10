package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para actualizar un proveedor existente
 * @param razonSocial
 * @param nombreComercial
 * @param telefono
 * @param celular
 * @param email
 * @param paginaWeb
 * @param personaContacto
 * @param calle
 * @param numero
 * @param localidad
 * @param provincia
 * @param codigoPostal
 * @param informacionDomicilioAdicional
 * @param cuentaCorrienteHabilitada
 * @param limiteCredito
 * @param diasPlazoPago
 * @param condicionIva
 * @param ingresosBrutos
 * @param observaciones
 * @param activo
 */
public record UpdateProveedorRequest(

    @Size(max = 150, message = "La razón social no puede exceder los 150 caracteres")
    String razonSocial,

    @Size(max = 150, message = "El nombre comercial no puede exceder los 150 caracteres")
    String nombreComercial,

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    String telefono,

    @Size(max = 20, message = "El celular no puede exceder los 20 caracteres")
    String celular,

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    String email,

    @Size(max = 255, message = "La página web no puede exceder los 255 caracteres")
    String paginaWeb,

    @Size(max = 150, message = "La persona de contacto no puede exceder los 150 caracteres")
    String personaContacto,

    @Size(max = 150, message = "La calle no puede exceder los 150 caracteres")
    String calle,

    @Size(max = 10, message = "El número no puede exceder los 10 caracteres")
    String numero,

    @Size(max = 100, message = "La localidad no puede exceder los 100 caracteres")
    String localidad,

    @Size(max = 100, message = "La provincia no puede exceder los 100 caracteres")
    String provincia,

    @Size(max = 10, message = "El código postal no puede exceder los 10 caracteres")
    String codigoPostal,

    String informacionDomicilioAdicional,

    Boolean cuentaCorrienteHabilitada,

    @DecimalMin(value = "0.0", message = "El límite de crédito no puede ser negativo")
    BigDecimal limiteCredito,

    Integer diasPlazoPago,

    String condicionIva,

    @Size(max = 50, message = "Ingresos brutos no puede exceder los 50 caracteres")
    String ingresosBrutos,

    String observaciones,

    Boolean activo
) {}
