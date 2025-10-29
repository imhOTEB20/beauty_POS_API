package com.belleza.pos.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para actualizar un cliente existente
 * @param nombre
 * @param apellido
 * @param telefono
 * @param celular
 * @param email
 * @param calle
 * @param numero
 * @param localidad
 * @param provincia
 * @param codigoPostal
 * @param cuentaCorrienteHabilitada
 * @param tipoLimite
 * @param limiteCredito
 * @param diasPlazoPago
 * @param condicionIva
 * @param observaciones
 * @param activo
 */

public record UpdateClienteRequest(

    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,

    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    String apellido,

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    String telefono,

    @Size(max = 20, message = "El celular no puede exceder los 20 caracteres")
    String celular,

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    String email,

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

    Boolean cuentaCorrienteHabilitada,

    String tipoLimite,

    @DecimalMin(value = "0.0", message = "El límite de crédito no puede ser negativo")
    BigDecimal limiteCredito,

    Integer diasPlazoPago,

    String condicionIva,

    String observaciones,

    Boolean activo
) {}