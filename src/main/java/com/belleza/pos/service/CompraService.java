package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateCompraRequest;
import com.belleza.pos.dto.response.CompraResponse;
import com.belleza.pos.dto.response.CompraSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de Compra
 */
public interface CompraService {

    // ========== CRUD Básico ==========

    /**
     * Crea una nueva compra
     */
    CompraResponse create(CreateCompraRequest request);

    /**
     * Obtiene una compra por ID
     */
    CompraResponse getById(Integer id);

    /**
     * Obtiene una compra por número de comprobante
     */
    CompraResponse getByNroComprobante(String nroComprobante);

    /**
     * Obtiene todas las compras con paginación
     */
    Page<CompraResponse> getAll(Pageable pageable);

    /**
     * Busca compras por término de búsqueda
     */
    Page<CompraResponse> search(String searchTerm, Pageable pageable);

    /**
     * Anula una compra
     */
    CompraResponse anular(Integer id, String motivo);

    /**
     * Elimina una compra permanentemente
     */
    void delete(Integer id);

    // ========== Consultas por Estado ==========

    /**
     * Obtiene compras por estado
     */
    Page<CompraResponse> getByEstado(String estado, Pageable pageable);

    /**
     * Obtiene compras completadas
     */
    List<CompraSimpleResponse> getComprasCompletadas();

    /**
     * Obtiene compras pendientes
     */
    List<CompraSimpleResponse> getComprasPendientes();

    // ========== Consultas por Proveedor ==========

    /**
     * Obtiene compras de un proveedor
     */
    Page<CompraResponse> getByProveedor(Integer idProveedor, Pageable pageable);

    /**
     * Obtiene historial de compras de un proveedor
     */
    List<CompraSimpleResponse> getHistorialProveedor(Integer idProveedor);

    // ========== Consultas por Sucursal ==========

    /**
     * Obtiene compras de una sucursal
     */
    Page<CompraResponse> getBySucursal(Integer idSucursal, Pageable pageable);

    /**
     * Obtiene compras del día de una sucursal
     */
    List<CompraSimpleResponse> getComprasDelDiaBySucursal(Integer idSucursal);

    // ========== Consultas por Usuario ==========

    /**
     * Obtiene compras de un usuario
     */
    Page<CompraResponse> getByUsuario(Integer idUsuario, Pageable pageable);

    // ========== Consultas por Tipo de Comprobante ==========

    /**
     * Obtiene compras por tipo de comprobante
     */
    Page<CompraResponse> getByTipoComprobante(String tipoComprobante, Pageable pageable);

    // ========== Consultas por Fecha ==========

    /**
     * Obtiene compras del día
     */
    List<CompraSimpleResponse> getComprasDelDia();

    /**
     * Obtiene compras entre fechas
     */
    Page<CompraResponse> getByFechaCompraBetween(LocalDate fechaInicio,
                                                 LocalDate fechaFin,
                                                 Pageable pageable);

    /**
     * Obtiene compras de un período
     */
    List<CompraSimpleResponse> getComprasByPeriodo(LocalDate fechaInicio,
                                                   LocalDate fechaFin);

    // ========== Estadísticas y Totales ==========

    /**
     * Obtiene el total de compras del día
     */
    BigDecimal getTotalComprasDelDia();

    /**
     * Obtiene el total de compras del día por sucursal
     */
    BigDecimal getTotalComprasDelDiaBySucursal(Integer idSucursal);

    /**
     * Obtiene el total de compras por período
     */
    BigDecimal getTotalComprasByPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Cuenta compras del día
     */
    Long countComprasDelDia();

    /**
     * Cuenta compras del día por sucursal
     */
    Long countComprasDelDiaBySucursal(Integer idSucursal);

    // ========== Utilidades ==========

    /**
     * Verifica si existe un número de comprobante
     */
    boolean existsByNroComprobante(String nroComprobante);
}
