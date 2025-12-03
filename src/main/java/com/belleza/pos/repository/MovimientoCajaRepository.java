package com.belleza.pos.repository;

import com.belleza.pos.entity.MovimientoCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para MovimientoCaja
 * Gestiona todas las operaciones de acceso a datos de movimientos de caja
 */
@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {

    // ========== Consultas por Caja ==========

    /**
     * Busca todos los movimientos de una caja específica
     */
    List<MovimientoCaja> findByCaja_IdCaja(Integer idCaja);

    /**
     * Busca movimientos de una caja con paginación
     */
    Page<MovimientoCaja> findByCaja_IdCaja(Integer idCaja, Pageable pageable);

    /**
     * Busca movimientos de una caja ordenados por fecha descendente
     */
    List<MovimientoCaja> findByCaja_IdCajaOrderByFechaHoraDesc(Integer idCaja);

    /**
     * Busca movimientos de una caja ordenados por fecha ascendente
     */
    List<MovimientoCaja> findByCaja_IdCajaOrderByFechaHoraAsc(Integer idCaja);

    // ========== Consultas por Fecha ==========

    /**
     * Busca movimientos de una caja entre fechas
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaHora DESC")
    List<MovimientoCaja> findByCajaAndFechaHoraBetween(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Busca movimientos de una caja entre fechas con paginación
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    Page<MovimientoCaja> findByCajaAndFechaHoraBetween(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            Pageable pageable
    );

    // ========== Último Movimiento ==========

    /**
     * Obtiene el último movimiento de una caja
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "ORDER BY m.fechaHora DESC, m.idMovimiento DESC LIMIT 1")
    Optional<MovimientoCaja> findUltimoMovimientoByCaja(@Param("idCaja") Integer idCaja);

    /**
     * Obtiene el primer movimiento de una caja en una fecha específica
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaHora ASC, m.idMovimiento ASC LIMIT 1")
    Optional<MovimientoCaja> findPrimerMovimientoDelDia(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // ========== Cálculos de Saldo ==========

    /**
     * Calcula el saldo actual de una caja sumando todos los movimientos
     */
    @Query("SELECT COALESCE(SUM(m.montoIngreso - m.montoEgreso), 0) FROM MovimientoCaja m " +
            "WHERE m.caja.idCaja = :idCaja")
    BigDecimal calcularSaldoActualCaja(@Param("idCaja") Integer idCaja);

    /**
     * Calcula el saldo entre fechas
     */
    @Query("SELECT COALESCE(SUM(m.montoIngreso - m.montoEgreso), 0) FROM MovimientoCaja m " +
            "WHERE m.caja.idCaja = :idCaja AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularSaldoEntreFechas(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Calcula el total de ingresos de una caja entre fechas
     */
    @Query("SELECT COALESCE(SUM(m.montoIngreso), 0) FROM MovimientoCaja m " +
            "WHERE m.caja.idCaja = :idCaja AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularTotalIngresosEntreFechas(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Calcula el total de egresos de una caja entre fechas
     */
    @Query("SELECT COALESCE(SUM(m.montoEgreso), 0) FROM MovimientoCaja m " +
            "WHERE m.caja.idCaja = :idCaja AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularTotalEgresosEntreFechas(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // ========== Consultas por Tipo de Movimiento ==========

    /**
     * Busca movimientos por tipo
     */
    List<MovimientoCaja> findByTipoMovimiento(String tipoMovimiento);

    /**
     * Busca movimientos de una caja por tipo
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.tipoMovimiento = :tipoMovimiento " +
            "ORDER BY m.fechaHora DESC")
    List<MovimientoCaja> findByCajaAndTipoMovimiento(
            @Param("idCaja") Integer idCaja,
            @Param("tipoMovimiento") String tipoMovimiento
    );

    /**
     * Busca movimientos de una caja por tipo entre fechas
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.tipoMovimiento = :tipoMovimiento " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaHora DESC")
    List<MovimientoCaja> findByCajaAndTipoMovimientoAndFechaHoraBetween(
            @Param("idCaja") Integer idCaja,
            @Param("tipoMovimiento") String tipoMovimiento,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Calcula el total por tipo de movimiento entre fechas
     */
    @Query("SELECT COALESCE(SUM(m.montoIngreso), 0) FROM MovimientoCaja m " +
            "WHERE m.caja.idCaja = :idCaja " +
            "AND m.tipoMovimiento = :tipoMovimiento " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularTotalPorTipoMovimiento(
            @Param("idCaja") Integer idCaja,
            @Param("tipoMovimiento") String tipoMovimiento,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // ========== Consultas por Usuario ==========

    /**
     * Busca movimientos por usuario
     */
    List<MovimientoCaja> findByUsuario_IdUsuario(Integer idUsuario);

    /**
     * Busca movimientos de un usuario entre fechas
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.usuario.idUsuario = :idUsuario " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaHora DESC")
    List<MovimientoCaja> findByUsuarioAndFechaHoraBetween(
            @Param("idUsuario") Integer idUsuario,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // ========== Consultas por Venta/Compra ==========

    /**
     * Busca movimientos asociados a una venta
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.idVenta = :idVenta")
    List<MovimientoCaja> findByIdVenta(@Param("idVenta") Integer idVenta);

    /**
     * Busca movimientos asociados a una compra
     */
    @Query("SELECT m FROM MovimientoCaja m WHERE m.idCompra = :idCompra")
    List<MovimientoCaja> findByIdCompra(@Param("idCompra") Integer idCompra);

    /**
     * Verifica si existe un movimiento asociado a una venta
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MovimientoCaja m " +
            "WHERE m.idVenta = :idVenta")
    Boolean existsByIdVenta(@Param("idVenta") Integer idVenta);

    /**
     * Verifica si existe un movimiento asociado a una compra
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MovimientoCaja m " +
            "WHERE m.idCompra = :idCompra")
    Boolean existsByIdCompra(@Param("idCompra") Integer idCompra);

    // ========== Estadísticas ==========

    /**
     * Cuenta la cantidad de movimientos de una caja entre fechas
     */
    @Query("SELECT COUNT(m) FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    Long contarMovimientosEntreFechas(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Cuenta movimientos por tipo en un período
     */
    @Query("SELECT COUNT(m) FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.tipoMovimiento = :tipoMovimiento " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    Long contarMovimientosPorTipo(
            @Param("idCaja") Integer idCaja,
            @Param("tipoMovimiento") String tipoMovimiento,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // ========== Eliminación ==========

    /**
     * Elimina todos los movimientos de una caja
     */
    void deleteByCaja_IdCaja(Integer idCaja);

    /**
     * Elimina movimientos de una caja entre fechas
     */
    @Query("DELETE FROM MovimientoCaja m WHERE m.caja.idCaja = :idCaja " +
            "AND m.fechaHora BETWEEN :fechaInicio AND :fechaFin")
    void deleteByCajaAndFechaHoraBetween(
            @Param("idCaja") Integer idCaja,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}