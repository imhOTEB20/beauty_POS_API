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
 * Entidad Proveedor
 */
@Entity
@Table(name = "proveedores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @Column(name = "nro_proveedor", unique = true, length = 20)
    private String nroProveedor;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 150)
    private String nombreComercial;

    @Column(name = "cuit", unique = true, nullable = false, length = 13)
    private String cuit;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "celular", length = 20)
    private String celular;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "pagina_web")
    private String paginaWeb;

    @Column(name = "persona_contacto", length = 150)
    private String personaContacto;

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

    @Column(name = "informacion_domicilio_adicional", columnDefinition = "TEXT")
    private String informacionDomicilioAdicional;

    @Column(name = "cuenta_corriente_habilitada", nullable = false)
    private Boolean cuentaCorrienteHabilitada = false;

    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "saldo_actual", precision = 15, scale = 2)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    @Column(name = "dias_plazo_pago")
    private Integer diasPlazoPago = 0;

    @Column(name = "condicion_iva", length = 30)
    private String condicionIva;

    @Column(name = "ingresos_brutos", length = 50)
    private String ingresosBrutos;

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
