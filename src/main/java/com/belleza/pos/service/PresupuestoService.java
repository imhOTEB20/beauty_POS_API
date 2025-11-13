package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreatePresupuestoRequest;
/*
Por el momento no se permite actualizar un presupuesto, simplemente se cambia su estado a cancelado o rechazado
como lo decida el usuario o la circunstancia.
import com.belleza.pos.dto.request.UpdatePresupuestoRequest;
 */
import com.belleza.pos.dto.response.PresupuestoResponse;
import com.belleza.pos.dto.response.PresupuestoSimpleResponse;
import com.belleza.pos.dto.response.VentaResponse;
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
     * Busca presupuestos por término de búsqueda
     */
    Page<PresupuestoResponse> search(String searchTerm, Pageable pageable);

    /**
     * Elimina un presupuesto permanentemente
     */
    void delete(Integer id);

    // ========== Gestión de Estado ==========

    /**
     * Aprueba un presupuesto
     */
    PresupuestoResponse aprobar(Integer id);

    /**
     * Rechaza un presupuesto
     */
    PresupuestoResponse rechazar(Integer id, String motivo);

    /**
     * Convierte un presupuesto a venta
     */
    VentaResponse convertirAVenta(Integer id);

    // ========== Consultas por Estado ==========

    /**
     * Obtiene presupuestos por estado
     */
    Page<PresupuestoResponse> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene presupuestos pendientes
     */
    List<PresupuestoSimpleResponse> getPresupuestosPendientes();

    /**
     * Obtiene presupuestos aprobados
     */
    List<PresupuestoSimpleResponse> getPresupuestosAprobados();

    /**
     * Obtiene presupuestos rechazados
     */
    List<PresupuestoSimpleResponse> getPresupuestosRechazados();

    /**
     * Obtiene presupuestos convertidos a venta
     */
    List<PresupuestoSimpleResponse> getPresupuestosConvertidos();

    // ========== Consultas por Cliente ==========

    /**
     * Obtiene presupuestos de un cliente
     */
    Page<PresupuestoResponse> getByCliente(Integer idCliente, Pageable pageable);

    /**
     * Obtiene historial de presupuestos de un cliente
     */
    List<PresupuestoSimpleResponse> getHistorialCliente(Integer idCliente);

    // ========== Consultas por Sucursal ==========

    /**
     * Obtiene presupuestos de una sucursal
     */
    Page<PresupuestoResponse> getBySucursal(Integer idSucursal, Pageable pageable);

    /**
     * Obtiene presupuestos del día de una sucursal
     */
    List<PresupuestoSimpleResponse> getPresupuestosDelDiaBySucursal(Integer idSucursal);

    // ========== Consultas por Usuario ==========

    /**
     * Obtiene presupuestos de un usuario
     */
    Page<PresupuestoResponse> getByUsuario(Integer idUsuario, Pageable pageable);

    // ========== Consultas por Fecha ==========

    /**
     * Obtiene presupuestos del día
     */
    List<PresupuestoSimpleResponse> getPresupuestosDelDia();

    /**
     * Obtiene presupuestos entre fechas
     */
    Page<PresupuestoResponse> getByFechaPresupuestoBetween(LocalDate fechaInicio,
                                                           LocalDate fechaFin,
                                                           Pageable pageable);

    /**
     * Obtiene presupuestos de un período
     */
    List<PresupuestoSimpleResponse> getPresupuestosByPeriodo(LocalDate fechaInicio,
                                                             LocalDate fechaFin);

    // ========== Estadísticas ==========

    /**
     * Cuenta presupuestos del día
     */
    Long countPresupuestosDelDia();

    /**
     * Cuenta presupuestos del día por sucursal
     */
    Long countPresupuestosDelDiaBySucursal(Integer idSucursal);

    /**
     * Cuenta presupuestos pendientes
     */
    Long countPresupuestosPendientes();

    /**
     * Cuenta presupuestos convertidos
     */
    Long countPresupuestosConvertidos();

    // ========== Utilidades ==========

    /**
     * Verifica si existe un número de presupuesto
     */
    boolean existsByNroPresupuesto(String nroPresupuesto);

    /**
     * Genera un número de presupuesto único
     */
    String generarNroPresupuesto();
}