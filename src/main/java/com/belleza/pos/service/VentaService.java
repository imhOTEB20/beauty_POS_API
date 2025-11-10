package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateVentaRequest;
import com.belleza.pos.dto.request.UpdateVentaRequest;
import com.belleza.pos.dto.response.VentaResponse;
import com.belleza.pos.dto.response.VentaSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz del servicio de Venta
 */
public interface VentaService {

    // ========== CRUD Básico ==========

    /**
     * Crea una nueva venta
     */
    VentaResponse create(CreateVentaRequest request);

    /**
     * Actualiza una venta existente
     */
    VentaResponse update(Integer id, UpdateVentaRequest request);

    /**
     * Obtiene una venta por ID
     */
    VentaResponse getById(Integer id);

    /**
     * Obtiene una venta por número de transacción
     */
    VentaResponse getByNroTransaccion(String nroTransaccion);

    /**
     * Obtiene todas las ventas con paginación
     */
    Page<VentaResponse> getAll(Pageable pageable);

    /**
     * Busca ventas por término de búsqueda
     */
    Page<VentaResponse> search(String searchTerm, Pageable pageable);

    /**
     * Anula una venta
     */
    VentaResponse anular(Integer id, String motivo);

    /**
     * Elimina una venta permanentemente
     */
    void delete(Integer id);

    // ========== Consultas por Estado ==========

    /**
     * Obtiene ventas por estado
     */
    Page<VentaResponse> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene ventas completadas
     */
    List<VentaSimpleResponse> getVentasCompletadas();

    /**
     * Obtiene ventas pendientes
     */
    List<VentaSimpleResponse> getVentasPendientes();

    /**
     * Obtiene presupuestos
     */
    List<VentaSimpleResponse> getPresupuestos();

    // ========== Consultas por Cliente ==========

    /**
     * Obtiene ventas de un cliente
     */
    Page<VentaResponse> getByCliente(Integer idCliente, Pageable pageable);

    /**
     * Obtiene historial de compras de un cliente
     */
    List<VentaSimpleResponse> getHistorialCliente(Integer idCliente);

    // ========== Consultas por Sucursal ==========

    /**
     * Obtiene ventas de una sucursal
     */
    Page<VentaResponse> getBySucursal(Integer idSucursal, Pageable pageable);

    /**
     * Obtiene ventas del día de una sucursal
     */
    List<VentaSimpleResponse> getVentasDelDiaBySucursal(Integer idSucursal);

    // ========== Consultas por Usuario ==========

    /**
     * Obtiene ventas de un usuario
     */
    Page<VentaResponse> getByUsuario(Integer idUsuario, Pageable pageable);

    // ========== Consultas por Tipo de Comprobante ==========

    /**
     * Obtiene ventas por tipo de comprobante
     */
    Page<VentaResponse> getByTipoComprobante(String tipoComprobante, Pageable pageable);

    // ========== Consultas por Fecha ==========

    /**
     * Obtiene ventas del día
     */
    List<VentaSimpleResponse> getVentasDelDia();

    /**
     * Obtiene ventas entre fechas
     */
    Page<VentaResponse> getByFechaVentaBetween(LocalDateTime fechaInicio,
                                               LocalDateTime fechaFin,
                                               Pageable pageable);

    /**
     * Obtiene ventas de un período
     */
    List<VentaSimpleResponse> getVentasByPeriodo(LocalDateTime fechaInicio,
                                                 LocalDateTime fechaFin);

    // ========== Estadísticas y Totales ==========

    /**
     * Obtiene el total de ventas del día
     */
    BigDecimal getTotalVentasDelDia();

    /**
     * Obtiene el total de ventas del día por sucursal
     */
    BigDecimal getTotalVentasDelDiaBySucursal(Integer idSucursal);

    /**
     * Obtiene el total de ventas por período
     */
    BigDecimal getTotalVentasByPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Cuenta ventas del día
     */
    Long countVentasDelDia();

    /**
     * Cuenta ventas del día por sucursal
     */
    Long countVentasDelDiaBySucursal(Integer idSucursal);

    // ========== Utilidades ==========

    /**
     * Verifica si existe un número de transacción
     */
    boolean existsByNroTransaccion(String nroTransaccion);

    /**
     * Genera un número de transacción único
     */
    String generarNroTransaccion();
}