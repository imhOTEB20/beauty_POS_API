package com.belleza.pos.service;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de Caja
 */
public interface CajaService {

    // ========== CRUD Básico ==========

    /**
     * Crea una nueva caja
     */
    CajaResponse create(CreateCajaRequest request);

    /**
     * Actualiza una caja existente
     */
    CajaResponse update(Integer id, UpdateCajaRequest request);

    /**
     * Obtiene una caja por ID
     */
    CajaResponse getById(Integer id);

    /**
     * Obtiene una caja por número de caja
     */
    CajaResponse getByNumeroCaja(String numeroCaja);

    /**
     * Obtiene todas las cajas con paginación
     */
    Page<CajaResponse> getAll(Pageable pageable);

    /**
     * Obtiene todas las cajas activas
     */
    List<CajaSimpleResponse> getAllActive();

    /**
     * Obtiene cajas por sucursal
     */
    List<CajaSimpleResponse> getBySucursal(Integer idSucursal);

    /**
     * Obtiene cajas activas por sucursal
     */
    List<CajaSimpleResponse> getActiveBySucursal(Integer idSucursal);

    /**
     * Activa una caja
     */
    CajaResponse activate(Integer id);

    /**
     * Desactiva una caja
     */
    CajaResponse deactivate(Integer id);

    /**
     * Elimina una caja (soft delete)
     */
    void delete(Integer id);

    // ========== Gestión de Movimientos ==========

    /**
     * Crea un movimiento de caja
     */
    MovimientoCajaResponse createMovimiento(CreateMovimientoCajaRequest request);

    /**
     * Obtiene un movimiento por ID
     */
    MovimientoCajaResponse getMovimientoById(Integer id);

    /**
     * Obtiene todos los movimientos de una caja
     */
    List<MovimientoCajaResponse> getMovimientosByCaja(Integer idCaja);

    /**
     * Obtiene movimientos de una caja con paginación
     */
    Page<MovimientoCajaResponse> getMovimientosByCaja(Integer idCaja, Pageable pageable);

    /**
     * Obtiene movimientos de una caja entre fechas
     */
    List<MovimientoCajaResponse> getMovimientosByCajaAndFechas(
            Integer idCaja,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene el último movimiento de una caja
     */
    MovimientoCajaResponse getUltimoMovimiento(Integer idCaja);

    // ========== Operaciones de Caja ==========

    /**
     * Apertura de caja
     */
    MovimientoCajaResponse aperturaCaja(AperturaCajaRequest request);

    /**
     * Cierre de caja
     */
    MovimientoCajaResponse cierreCaja(CierreCajaRequest request);

    /**
     * Retiro de efectivo
     */
    MovimientoCajaResponse retiroEfectivo(RetiroEfectivoRequest request);

    /**
     * Ingreso de efectivo
     */
    MovimientoCajaResponse ingresoEfectivo(IngresoEfectivoRequest request);

    /**
     * Registra un movimiento de venta en efectivo
     */
    MovimientoCajaResponse registrarVentaEfectivo(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            java.math.BigDecimal monto
    );

    /**
     * Registra un movimiento de venta con tarjeta de débito
     */
    MovimientoCajaResponse registrarVentaTarjetaDebito(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            java.math.BigDecimal monto
    );

    /**
     * Registra un movimiento de venta con tarjeta de crédito
     */
    MovimientoCajaResponse registrarVentaTarjetaCredito(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            java.math.BigDecimal monto
    );

    // ========== Consultas y Reportes ==========

    /**
     * Obtiene el estado actual de una caja
     */
    EstadoCajaResponse getEstadoCaja(Integer idCaja);

    /**
     * Obtiene el resumen de caja del día
     */
    ResumenCajaResponse getResumenDia(Integer idCaja, LocalDate fecha);

    /**
     * Obtiene el resumen de caja entre fechas
     */
    ResumenCajaResponse getResumenEntreFechas(
            Integer idCaja,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Verifica si una caja está abierta
     */
    boolean isCajaAbierta(Integer idCaja);

    /**
     * Verifica si existe un número de caja
     */
    boolean existsByNumeroCaja(String numeroCaja);

    /**
     * Verifica si existe un número de caja en una sucursal
     */
    boolean existsByNumeroCajaAndSucursal(String numeroCaja, Integer idSucursal);
}