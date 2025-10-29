package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Cliente
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    @Column(name = "nro_cliente", unique = true, length = 20)
    private String nroCliente;

    @Column(name = "tipo_cuenta", length = 20)
    private String tipoCuenta = "INDIVIDUO"; // INDIVIDUO, EMPRESA

    // Datos personales
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", length = 100)
    private String apellido;

    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "nro_documento", length = 20)
    private String nroDocumento;

    // Contacto
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "email", length = 100)
    private String email;

    // Dirección
    @Column(name = "calle", length = 150)
    private String calle;

    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "localidad", length = 100)
    private String localidad;

    @Column(name = "provincia", length = 100)
    private String provincia;

    @Column(name = "codigo_postal", length = 10)
    private String codigoPostal;

    // Configuración de cuenta corriente
    @Column(name = "cuenta_corriente_habilitada", nullable = false)
    private Boolean cuentaCorrienteHabilitada = false;

    @Column(name = "tipo_limite", length = 20)
    private String tipoLimite = "LIMITADA"; // ILIMITADA, LIMITADA

    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    @Column(name = "dias_plazo_pago")
    private Integer diasPlazoPago = 0;

    // Condición IVA
    @Column(name = "condicion_iva", length = 30)
    private String condicionIva = "CONSUMIDOR_FINAL";

    // Observaciones
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}