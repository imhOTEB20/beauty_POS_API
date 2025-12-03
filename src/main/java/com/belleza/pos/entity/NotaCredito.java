// ==========================================
// NotaCredito.java
// ==========================================
package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad NotaCredito
 */
@Entity
@Table(name = "notas_credito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NotaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nota_credito")
    private Integer idNotaCredito;

    @Column(name = "tipo_comprobante", nullable = false, length = 30)
    private String tipoComprobante; // NOTA_CREDITO_A, NOTA_CREDITO_B, NOTA_CREDITO_C

    @Column(name = "nro_comprobante", length = 30)
    private String nroComprobante;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    // Comprobante asociado (opcional)
    @Column(name = "tipo_comprobante_asociado", length = 30)
    private String tipoComprobanteAsociado;

    @Column(name = "punto_venta_asociado", length = 10)
    private String puntoVentaAsociado;

    @Column(name = "nro_comprobante_asociado", length = 30)
    private String nroComprobanteAsociado;

    // Totales
    @Column(name = "total", precision = 15, scale = 2, nullable = false)
    private BigDecimal total;

    // Estado
    @Column(name = "estado", length = 20, nullable = false)
    private String estado = "ACTIVA"; // ACTIVA, ANULADA

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "notaCredito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotaCreditoDetalle> detalles = new ArrayList<>();

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
}