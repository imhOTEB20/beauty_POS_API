package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateMovimientoCajaRequest;
import com.belleza.pos.dto.response.MovimientoCajaResponse;
import com.belleza.pos.dto.response.ResumenCajaResponse;
import com.belleza.pos.entity.Caja;
import com.belleza.pos.entity.MovimientoCaja;
import com.belleza.pos.entity.Usuario;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper para convertir entre entidades MovimientoCaja y DTOs
 */
@Component
public class MovimientoCajaMapper {

    /**
     * Convierte CreateMovimientoCajaRequest a MovimientoCaja
     */
    public MovimientoCaja toEntity(
            CreateMovimientoCajaRequest request,
            Caja caja,
            Usuario usuario,
            BigDecimal saldoAnterior) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(request.tipoMovimiento());
        movimiento.setConcepto(request.concepto());

        // Establecer montos (nunca null)
        BigDecimal ingreso = request.montoIngreso() != null ? request.montoIngreso() : BigDecimal.ZERO;
        BigDecimal egreso = request.montoEgreso() != null ? request.montoEgreso() : BigDecimal.ZERO;

        movimiento.setMontoIngreso(ingreso);
        movimiento.setMontoEgreso(egreso);
        movimiento.setSaldoAnterior(saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO);

        // Calcular saldo actual
        BigDecimal saldoActual = movimiento.getSaldoAnterior()
                .add(ingreso)
                .subtract(egreso);
        movimiento.setSaldoActual(saldoActual);

        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setObservaciones(request.observaciones());
        movimiento.setIdVenta(request.idVenta());
        movimiento.setIdCompra(request.idCompra());

        return movimiento;
    }

    /**
     * Convierte MovimientoCaja a MovimientoCajaResponse
     */
    public MovimientoCajaResponse toResponse(MovimientoCaja movimiento) {
        if (movimiento == null) {
            return null;
        }

        String nombreUsuario = movimiento.getUsuario() != null ?
                movimiento.getUsuario().getNombre() + " " + movimiento.getUsuario().getApellido() :
                "Usuario desconocido";

        String numeroCaja = movimiento.getCaja() != null ?
                movimiento.getCaja().getNumeroCaja() :
                "Sin caja";

        Integer idCaja = movimiento.getCaja() != null ?
                movimiento.getCaja().getIdCaja() :
                null;

        Integer idUsuario = movimiento.getUsuario() != null ?
                movimiento.getUsuario().getIdUsuario() :
                null;

        return MovimientoCajaResponse.builder()
                .idMovimiento(movimiento.getIdMovimiento())
                .idCaja(idCaja)
                .numeroCaja(numeroCaja)
                .idUsuario(idUsuario)
                .nombreUsuario(nombreUsuario)
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .concepto(movimiento.getConcepto())
                .montoIngreso(movimiento.getMontoIngreso())
                .montoEgreso(movimiento.getMontoEgreso())
                .saldoAnterior(movimiento.getSaldoAnterior())
                .saldoActual(movimiento.getSaldoActual())
                .fechaHora(movimiento.getFechaHora())
                .observaciones(movimiento.getObservaciones())
                .idVenta(movimiento.getIdVenta())
                .idCompra(movimiento.getIdCompra())
                .build();
    }

    /**
     * Crea un ResumenCajaResponse vacío
     */
    public ResumenCajaResponse createEmptyResumen(Caja caja) {
        String nombreSucursal = caja.getSucursal() != null ?
                caja.getSucursal().getNombre() :
                "Sin sucursal";

        return ResumenCajaResponse.builder()
                .idCaja(caja.getIdCaja())
                .numeroCaja(caja.getNumeroCaja())
                .nombreSucursal(nombreSucursal)
                .saldoInicial(BigDecimal.ZERO)
                .totalIngresos(BigDecimal.ZERO)
                .totalEgresos(BigDecimal.ZERO)
                .saldoFinal(BigDecimal.ZERO)
                .ventasEfectivo(BigDecimal.ZERO)
                .ventasTarjetaDebito(BigDecimal.ZERO)
                .ventasTarjetaCredito(BigDecimal.ZERO)
                .retiros(BigDecimal.ZERO)
                .ingresos(BigDecimal.ZERO)
                .cantidadMovimientos(0)
                .build();
    }

    /**
     * Convierte un MovimientoCaja a un resumen simple
     */
    public String toResumenSimple(MovimientoCaja movimiento) {
        if (movimiento == null) {
            return "Sin movimientos";
        }

        return String.format("%s - %s - $%.2f",
                movimiento.getTipoMovimiento(),
                movimiento.getConcepto(),
                movimiento.getSaldoActual());
    }

    /**
     * Valida y ajusta los montos de un movimiento
     */
    public void validarYAjustarMontos(MovimientoCaja movimiento) {
        if (movimiento.getMontoIngreso() == null) {
            movimiento.setMontoIngreso(BigDecimal.ZERO);
        }
        if (movimiento.getMontoEgreso() == null) {
            movimiento.setMontoEgreso(BigDecimal.ZERO);
        }
        if (movimiento.getSaldoAnterior() == null) {
            movimiento.setSaldoAnterior(BigDecimal.ZERO);
        }

        // Recalcular saldo actual
        movimiento.calcularSaldoActual();
    }

    /**
     * Crea un movimiento de apertura
     */
    public MovimientoCaja createMovimientoApertura(
            Caja caja,
            Usuario usuario,
            BigDecimal montoInicial,
            String observaciones) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("APERTURA");
        movimiento.setConcepto("Apertura de caja");
        movimiento.setMontoIngreso(montoInicial != null ? montoInicial : BigDecimal.ZERO);
        movimiento.setMontoEgreso(BigDecimal.ZERO);
        movimiento.setSaldoAnterior(BigDecimal.ZERO);
        movimiento.setSaldoActual(montoInicial != null ? montoInicial : BigDecimal.ZERO);
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setObservaciones(observaciones);

        return movimiento;
    }

    /**
     * Crea un movimiento de cierre
     */
    public MovimientoCaja createMovimientoCierre(
            Caja caja,
            Usuario usuario,
            BigDecimal saldoActual,
            String observaciones) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("CIERRE");
        movimiento.setConcepto("Cierre de caja");
        movimiento.setMontoIngreso(BigDecimal.ZERO);
        movimiento.setMontoEgreso(BigDecimal.ZERO);
        movimiento.setSaldoAnterior(saldoActual != null ? saldoActual : BigDecimal.ZERO);
        movimiento.setSaldoActual(saldoActual != null ? saldoActual : BigDecimal.ZERO);
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setObservaciones(observaciones);

        return movimiento;
    }

    /**
     * Crea un movimiento de venta
     */
    public MovimientoCaja createMovimientoVenta(
            Caja caja,
            Usuario usuario,
            String tipoMovimiento,
            Integer idVenta,
            BigDecimal monto,
            BigDecimal saldoAnterior) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setConcepto("Venta #" + idVenta);
        movimiento.setMontoIngreso(monto != null ? monto : BigDecimal.ZERO);
        movimiento.setMontoEgreso(BigDecimal.ZERO);
        movimiento.setSaldoAnterior(saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO);
        movimiento.calcularSaldoActual();
        movimiento.setFechaHora(LocalDateTime.now());
        movimiento.setIdVenta(idVenta);

        return movimiento;
    }

    /**
     * Crea un movimiento de retiro
     */
    public MovimientoCaja createMovimientoRetiro(
            Caja caja,
            Usuario usuario,
            BigDecimal monto,
            String motivo,
            BigDecimal saldoAnterior) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("RETIRO");
        movimiento.setConcepto("Retiro: " + motivo);
        movimiento.setMontoIngreso(BigDecimal.ZERO);
        movimiento.setMontoEgreso(monto != null ? monto : BigDecimal.ZERO);
        movimiento.setSaldoAnterior(saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO);
        movimiento.calcularSaldoActual();
        movimiento.setFechaHora(LocalDateTime.now());

        return movimiento;
    }

    /**
     * Crea un movimiento de ingreso
     */
    public MovimientoCaja createMovimientoIngreso(
            Caja caja,
            Usuario usuario,
            BigDecimal monto,
            String motivo,
            BigDecimal saldoAnterior) {

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setUsuario(usuario);
        movimiento.setTipoMovimiento("INGRESO");
        movimiento.setConcepto("Ingreso: " + motivo);
        movimiento.setMontoIngreso(monto != null ? monto : BigDecimal.ZERO);
        movimiento.setMontoEgreso(BigDecimal.ZERO);
        movimiento.setSaldoAnterior(saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO);
        movimiento.calcularSaldoActual();
        movimiento.setFechaHora(LocalDateTime.now());

        return movimiento;
    }
}