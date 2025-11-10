package com.belleza.pos.entity;

import com.belleza.pos.entity.enums.EstadoCompra;
import com.belleza.pos.entity.enums.FormaPago;
import com.belleza.pos.entity.enums.TipoComprobante;
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
 * Entidad Compra
 */
@Entity
@Table(name = "compras")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    // Información del comprobante
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 30)
    private TipoComprobante tipoComprobante;

    @Column(name = "nro_comprobante", length = 30)
    private String nroComprobante;

    @Column(name = "fecha_compra", nullable = false)
    private LocalDate fechaCompra;

    // Totales
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "impuestos_internos", precision = 15, scale = 2)
    private BigDecimal impuestosInternos = BigDecimal.ZERO;

    @Column(name = "iva_21", precision = 15, scale = 2)
    private BigDecimal iva21 = BigDecimal.ZERO;

    @Column(name = "iva_10_5", precision = 15, scale = 2)
    private BigDecimal iva105 = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // Opciones
    @Column(name = "actualizar_precios", nullable = false)
    private Boolean actualizarPrecios = false;

    @Column(name = "actualizar_stock", nullable = false)
    private Boolean actualizarStock = true;

    // Información de pago
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false, length = 30)
    private FormaPago formaPago;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoCompra estado = EstadoCompra.COMPLETADA;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompraDetalle> detalles = new ArrayList<>();

    // Métodos helper
    public void addDetalle(CompraDetalle detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
    }

    public void removeDetalle(CompraDetalle detalle) {
        detalles.remove(detalle);
        detalle.setCompra(null);
    }
}
