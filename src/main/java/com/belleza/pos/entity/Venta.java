package com.belleza.pos.entity;

import com.belleza.pos.entity.enums.EstadoVenta;
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
 * Entidad Venta
 */
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "nro_transaccion", unique = true, nullable = false, length = 30)
    private String nroTransaccion;

    @Column(name = "nro_caja", length = 20)
    private String nroCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    // Información del comprobante
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false, length = 30)
    private TipoComprobante tipoComprobante;

    @Column(name = "nro_comprobante", length = 30)
    private String nroComprobante;

    @Column(name = "cae", length = 20)
    private String cae;

    @Column(name = "fecha_vencimiento_cae")
    private LocalDate fechaVencimientoCae;

    // Lista de precios utilizada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lista_precio", nullable = false)
    private ListaPrecio listaPrecio;

    // Totales
    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "descuento_porcentaje", precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje = BigDecimal.ZERO;

    @Column(name = "descuento_monto", precision = 15, scale = 2)
    private BigDecimal descuentoMonto = BigDecimal.ZERO;

    @Column(name = "recargo_porcentaje", precision = 5, scale = 2)
    private BigDecimal recargoPorcentaje = BigDecimal.ZERO;

    @Column(name = "recargo_monto", precision = 15, scale = 2)
    private BigDecimal recargoMonto = BigDecimal.ZERO;

    @Column(name = "total", nullable = false, precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    // Estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.COMPLETADA;

    @CreatedDate
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Relaciones
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaDetalle> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VentaFormaPago> formasPago = new ArrayList<>();

    // Métodos helper
    public void addDetalle(VentaDetalle detalle) {
        detalles.add(detalle);
        detalle.setVenta(this);
    }

    public void removeDetalle(VentaDetalle detalle) {
        detalles.remove(detalle);
        detalle.setVenta(null);
    }

    public void addFormaPago(VentaFormaPago formaPago) {
        formasPago.add(formaPago);
        formaPago.setVenta(this);
    }

    public void removeFormaPago(VentaFormaPago formaPago) {
        formasPago.remove(formaPago);
        formaPago.setVenta(null);
    }
}
