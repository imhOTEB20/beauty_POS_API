package com.belleza.pos.service;

import com.belleza.pos.dto.request.AnularNotaCreditoRequest;
import com.belleza.pos.dto.request.CreateNotaCreditoRequest;
import com.belleza.pos.dto.request.UpdateNotaCreditoRequest;
import com.belleza.pos.dto.response.EstadisticasNotaCreditoResponse;
import com.belleza.pos.dto.response.NotaCreditoDetalleResponse;
import com.belleza.pos.dto.response.NotaCreditoResponse;
import com.belleza.pos.dto.response.NotaCreditoSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de NotaCredito
 */
public interface NotaCreditoService {

    // ========== CRUD Básico ==========

    /**
     * Crea una nueva nota de crédito
     */
    NotaCreditoResponse create(CreateNotaCreditoRequest request);

    /**
     * Actualiza una nota de crédito existente
     */
    NotaCreditoResponse update(Integer id, UpdateNotaCreditoRequest request);

    /**
     * Obtiene una nota de crédito por ID
     */
    NotaCreditoResponse getById(Integer id);

    /**
     * Obtiene una nota de crédito por número de comprobante
     */
    NotaCreditoResponse getByNroComprobante(String nroComprobante);

    /**
     * Obtiene todas las notas de crédito con paginación
     */
    Page<NotaCreditoResponse> getAll(Pageable pageable);

    /**
     * Obtiene todas las notas de crédito activas
     */
    List<NotaCreditoSimpleResponse> getAllActive();

    /**
     * Busca notas de crédito por término de búsqueda
     */
    Page<NotaCreditoResponse> search(String searchTerm, Pageable pageable);

    /**
     * Obtiene notas de crédito por cliente
     */
    Page<NotaCreditoResponse> getByCliente(Integer idCliente, Pageable pageable);

    /**
     * Obtiene notas de crédito por sucursal
     */
    Page<NotaCreditoResponse> getBySucursal(Integer idSucursal, Pageable pageable);

    /**
     * Obtiene notas de crédito por estado
     */
    Page<NotaCreditoResponse> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene notas de crédito entre fechas
     */
    List<NotaCreditoResponse> getByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Anula una nota de crédito
     */
    NotaCreditoResponse anular(Integer id, AnularNotaCreditoRequest request);

    /**
     * Elimina una nota de crédito permanentemente
     */
    void deletePermanently(Integer id);

    // ========== Gestión de Detalles ==========

    /**
     * Obtiene los detalles de una nota de crédito
     */
    List<NotaCreditoDetalleResponse> getDetalles(Integer idNotaCredito);

    // ========== Consultas y Reportes ==========

    /**
     * Obtiene notas de crédito por cliente entre fechas
     */
    List<NotaCreditoResponse> getByClienteAndFechaBetween(
            Integer idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene notas de crédito por sucursal entre fechas
     */
    List<NotaCreditoResponse> getBySucursalAndFechaBetween(
            Integer idSucursal,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    /**
     * Obtiene notas de crédito por tipo de comprobante
     */
    List<NotaCreditoResponse> getByTipoComprobante(String tipoComprobante);

    /**
     * Calcula el total de notas de crédito en un período
     */
    java.math.BigDecimal calcularTotalPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Calcula el total de notas de crédito por cliente
     */
    java.math.BigDecimal calcularTotalPorCliente(Integer idCliente);

    /**
     * Obtiene estadísticas de notas de crédito en un período
     */
    EstadisticasNotaCreditoResponse getEstadisticas(LocalDate fechaInicio, LocalDate fechaFin);

    // ========== Utilidades ==========

    /**
     * Verifica si existe un número de comprobante
     */
    boolean existsByNroComprobante(String nroComprobante);

    /**
     * Obtiene notas de crédito del día
     */
    List<NotaCreditoSimpleResponse> getNotasDelDia();

    /**
     * Obtiene notas de crédito del mes
     */
    List<NotaCreditoSimpleResponse> getNotasDelMes();
}