// ==========================================
// MovimientoCajaService.java (INTERFACE)
// ==========================================
package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateMovimientoCajaRequest;
import com.belleza.pos.dto.response.MovimientoCajaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio de MovimientoCaja
 */
public interface MovimientoCajaService {

    // ========== CRUD Básico ==========

    /**
     * Crea un nuevo movimiento de caja
     */
    MovimientoCajaResponse create(CreateMovimientoCajaRequest request);

    /**
     * Obtiene un movimiento por ID
     */
    MovimientoCajaResponse getById(Integer id);

    /**
     * Obtiene todos los movimientos de una caja
     */
    List<MovimientoCajaResponse> getAllByCaja(Integer idCaja);

    /**
     * Obtiene movimientos de una caja con paginación
     */
    Page<MovimientoCajaResponse> getAllByCaja(Integer idCaja, Pageable pageable);

    /**
     * Obtiene movimientos de una caja entre fechas
     */
    List<MovimientoCajaResponse> getByFechaHoraBetween(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Obtiene movimientos de una caja entre fechas con paginación
     */
    Page<MovimientoCajaResponse> getByFechaHoraBetween(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable
    );

    /**
     * Obtiene el último movimiento de una caja
     */
    MovimientoCajaResponse getUltimoMovimiento(Integer idCaja);

    /**
     * Obtiene el primer movimiento del día de una caja
     */
    MovimientoCajaResponse getPrimerMovimientoDelDia(Integer idCaja, LocalDateTime fecha);

    // ========== Consultas por Tipo ==========

    /**
     * Obtiene movimientos por tipo
     */
    List<MovimientoCajaResponse> getByTipoMovimiento(String tipoMovimiento);

    /**
     * Obtiene movimientos de una caja por tipo
     */
    List<MovimientoCajaResponse> getByCajaAndTipoMovimiento(Integer idCaja, String tipoMovimiento);

    /**
     * Obtiene movimientos de una caja por tipo entre fechas
     */
    List<MovimientoCajaResponse> getByCajaAndTipoMovimientoAndFechas(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // ========== Consultas por Usuario ==========

    /**
     * Obtiene movimientos por usuario
     */
    List<MovimientoCajaResponse> getByUsuario(Integer idUsuario);

    /**
     * Obtiene movimientos de un usuario entre fechas
     */
    List<MovimientoCajaResponse> getByUsuarioAndFechas(
            Integer idUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // ========== Consultas por Venta/Compra ==========

    /**
     * Obtiene movimientos asociados a una venta
     */
    List<MovimientoCajaResponse> getByVenta(Integer idVenta);

    /**
     * Obtiene movimientos asociados a una compra
     */
    List<MovimientoCajaResponse> getByCompra(Integer idCompra);

    /**
     * Verifica si existe un movimiento asociado a una venta
     */
    boolean existsByVenta(Integer idVenta);

    /**
     * Verifica si existe un movimiento asociado a una compra
     */
    boolean existsByCompra(Integer idCompra);

    // ========== Cálculos ==========

    /**
     * Calcula el saldo actual de una caja
     */
    BigDecimal calcularSaldoActual(Integer idCaja);

    /**
     * Calcula el saldo entre fechas
     */
    BigDecimal calcularSaldoEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Calcula el total de ingresos entre fechas
     */
    BigDecimal calcularTotalIngresosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Calcula el total de egresos entre fechas
     */
    BigDecimal calcularTotalEgresosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Calcula el total por tipo de movimiento entre fechas
     */
    BigDecimal calcularTotalPorTipoMovimiento(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // ========== Estadísticas ==========

    /**
     * Cuenta la cantidad de movimientos entre fechas
     */
    Long contarMovimientosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    /**
     * Cuenta movimientos por tipo en un período
     */
    Long contarMovimientosPorTipo(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    // ========== Utilidades ==========

    /**
     * Elimina todos los movimientos de una caja
     */
    void deleteAllByCaja(Integer idCaja);
}