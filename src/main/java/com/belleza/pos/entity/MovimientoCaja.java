package com.belleza.pos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad MovimientoCaja
 * Representa cada movimiento registrado en una caja (apertura, cierre, ventas, retiros, ingresos, etc.)
 */
@Entity
@Table(name = "movimientos_caja")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Integer idMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_caja", nullable = false)
    private Caja caja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /**
     * Tipo de movimiento: APERTURA, CIERRE, VENTA_EFECTIVO, VENTA_TARJETA_DEBITO,
     * VENTA_TARJETA_CREDITO, COMPRA, RETIRO, INGRESO, AJUSTE
     */
    @Column(name = "tipo_movimiento", nullable = false, length = 50)
    private String tipoMovimiento;

    @Column(name = "concepto", nullable = false, length = 255)
    private String concepto;

    @Column(name = "monto_ingreso", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoIngreso = BigDecimal.ZERO;

    @Column(name = "monto_egreso", precision = 15, scale = 2, nullable = false)
    private BigDecimal montoEgreso = BigDecimal.ZERO;

    @Column(name = "saldo_anterior", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoAnterior = BigDecimal.ZERO;

    @Column(name = "saldo_actual", precision = 15, scale = 2, nullable = false)
    private BigDecimal saldoActual = BigDecimal.ZERO;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora = LocalDateTime.now();

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    // Referencias opcionales a otras transacciones
    @Column(name = "id_venta")
    private Integer idVenta;

    @Column(name = "id_compra")
    private Integer idCompra;

    /**
     * Constructor para crear un movimiento básico
     */
    public MovimientoCaja(Caja caja, Usuario usuario, String tipoMovimiento, String concepto,
                          BigDecimal montoIngreso, BigDecimal montoEgreso, BigDecimal saldoAnterior) {
        this.caja = caja;
        this.usuario = usuario;
        this.tipoMovimiento = tipoMovimiento;
        this.concepto = concepto;
        this.montoIngreso = montoIngreso != null ? montoIngreso : BigDecimal.ZERO;
        this.montoEgreso = montoEgreso != null ? montoEgreso : BigDecimal.ZERO;
        this.saldoAnterior = saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO;
        this.saldoActual = this.saldoAnterior.add(this.montoIngreso).subtract(this.montoEgreso);
        this.fechaHora = LocalDateTime.now();
    }

    /**
     * Calcula el saldo actual basado en el saldo anterior y los montos
     */
    public void calcularSaldoActual() {
        this.saldoActual = this.saldoAnterior
                .add(this.montoIngreso != null ? this.montoIngreso : BigDecimal.ZERO)
                .subtract(this.montoEgreso != null ? this.montoEgreso : BigDecimal.ZERO);
    }
}