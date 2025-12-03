package com.belleza.pos.service;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de Presupuesto
 */
public interface PresupuestoService {

    // ========== CRUD Básico ==========

    /**
     * Crea un nuevo presupuesto
     */
    PresupuestoResponse create(CreatePresupuestoRequest request);

    /**
     * Actualiza un presupuesto existente
     */
    PresupuestoResponse update(Integer id, UpdatePresupuestoRequest request);

    /**
     * Obtiene un presupuesto por ID
     */
    PresupuestoResponse getById(Integer id);

    /**
     * Obtiene un presupuesto por número
     */
    PresupuestoResponse getByNroPresupuesto(String nroPresupuesto);

    /**
     * Obtiene todos los presupuestos con paginación
     */
    Page<PresupuestoResponse> getAll(Pageable pageable);

    /**
     * Obtiene todos los presupuestos pendientes
     */
    List<PresupuestoSimpleResponse> getAllPendientes();

    /**
     * Busca presupuestos por término de búsqueda
     */
    Page<PresupuestoResponse> search(String searchTerm, Pageable pageable);

    /**
     * Obtiene presupuestos por cliente
     */
    Page<PresupuestoResponse> getByCliente(Integer idCliente, Pageable pageable);

    /**
     * Obtiene presupuestos por sucursal
     */
    Page<PresupuestoResponse> getBySucursal(Integer idSucursal, Pageable pageable);

    /**
     * Obtiene presupuestos por usuario
     */
    Page<PresupuestoResponse> getByUsuario(Integer idUsuario, Pageable pageable);

    /**
     * Obtiene presupuestos por estado
     */
    Page<PresupuestoResponse> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene presupuestos entre fechas
     */
    List<PresupuestoResponse> getByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Elimina un presupuesto permanentemente
     */
    void deletePermanently(Integer id);

    // ========== Gestión de Estados ==========

    /**
     * Aprueba un presupuesto
     */
    PresupuestoResponse aprobar(Integer id, AprobarPresupuestoRequest request);

    /**
     * Rechaza un presupuesto
     */
    PresupuestoResponse rechazar(Integer id, RechazarPresupuestoRequest request);

    /**
     * Convierte un presupuesto en venta
     * NOTA: Implementación simplificada, cuando se implemente Ventas se completará
     */
    PresupuestoResponse convertirEnVenta(Integer id, ConvertirPresupuestoVentaRequest request);

    // ========== Gestión de Detalles ==========

    /**
     * Obtiene los detalles de un presupuesto
     */
    List<PresupuestoDetalleResponse> getDetalles(Integer idPresupuesto);

    // ========== Consultas y Reportes ==========

    /**
     * Obtiene presupuestos por cliente entre fechas
     */
    List<PresupuestoResponse> getByClienteAndFechaBetween(
            Integer idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene presupuestos por sucursal entre fechas
     */
    List<PresupuestoResponse> getBySucursalAndFechaBetween(
            Integer idSucursal,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene presupuestos vencidos
     */
    List<PresupuestoSimpleResponse> getPresupuestosVencidos(Integer diasValidez);

    /**
     * Obtiene presupuestos vigentes
     */
    List<PresupuestoSimpleResponse> getPresupuestosVigentes();

    /**
     * Cuenta presupuestos por estado
     */
    Long countByEstado(String estado);

    /**
     * Calcula el total de presupuestos por estado en un período
     */
    java.math.BigDecimal calcularTotalPorEstadoYPeriodo(
            String estado,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene estadísticas de presupuestos en un período
     */
    EstadisticasPresupuestoResponse getEstadisticas(LocalDate fechaInicio, LocalDate fechaFin);

    // ========== Utilidades ==========

    /**
     * Verifica si existe un número de presupuesto
     */
    boolean existsByNroPresupuesto(String nroPresupuesto);

    /**
     * Obtiene presupuestos del día
     */
    List<PresupuestoSimpleResponse> getPresupuestosDelDia();

    /**
     * Obtiene presupuestos del mes
     */
    List<PresupuestoSimpleResponse> getPresupuestosDelMes();

    /**
     * Genera el siguiente número de presupuesto
     */
    String generarNroPresupuesto();
}