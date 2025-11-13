package com.belleza.pos.entity;

import com.belleza.pos.entity.enums.EstadoPresupuesto;
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
 * Entidad Presupuesto
 */
@Entity
@Table(name = "presupuestos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presupuesto")
    private Integer idPresupuesto;

    @Column(name = "nro_presupuesto", unique = true, nullable = false, length = 30)
    private String nroPresupuesto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @Column(name = "fecha_presupuesto", nullable = false)
    private LocalDate fechaPresupuesto;

    // Lista de precios
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lista_precio", nullable = false)
    private ListaPrecio listaPrecio;

    // Totales
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "iva_21", precision = 15, scale = 2)
    private BigDecimal iva21 = BigDecimal.ZERO;

    @Column(name = "iva_10_5", precision = 15, scale = 2)
    private BigDecimal iva105 = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPresupuesto estado = EstadoPresupuesto.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta_generada")
    private Venta ventaGenerada;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "presupuesto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PresupuestoDetalle> detalles = new ArrayList<>();

    // Métodos helper
    public void addDetalle(PresupuestoDetalle detalle) {
        detalles.add(detalle);
        detalle.setPresupuesto(this);
    }

    public void removeDetalle(PresupuestoDetalle detalle) {
        detalles.remove(detalle);
        detalle.setPresupuesto(null);
    }
}
