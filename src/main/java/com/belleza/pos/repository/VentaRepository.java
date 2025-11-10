package com.belleza.pos.repository;

import com.belleza.pos.entity.Venta;
import com.belleza.pos.entity.enums.EstadoVenta;
import com.belleza.pos.entity.enums.TipoComprobante;
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
 * Repositorio para Venta
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    Optional<Venta> findByNroTransaccion(String nroTransaccion);

    Boolean existsByNroTransaccion(String nroTransaccion);

    List<Venta> findByEstado(EstadoVenta estado);

    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);

    List<Venta> findByCliente_IdCliente(Integer idCliente);

    Page<Venta> findByCliente_IdCliente(Integer idCliente, Pageable pageable);

    List<Venta> findBySucursal_IdSucursal(Integer idSucursal);

    Page<Venta> findBySucursal_IdSucursal(Integer idSucursal, Pageable pageable);

    List<Venta> findByUsuario_IdUsuario(Integer idUsuario);

    Page<Venta> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);

    List<Venta> findByTipoComprobante(TipoComprobante tipoComprobante);

    Page<Venta> findByTipoComprobante(TipoComprobante tipoComprobante, Pageable pageable);

    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    List<Venta> findVentasDelDia();

    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE")
    Page<Venta> findVentasDelDia(Pageable pageable);

    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findByFechaVentaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                        @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Page<Venta> findByFechaVentaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                        @Param("fechaFin") LocalDateTime fechaFin,
                                        Pageable pageable);

    @Query("SELECT v FROM Venta v WHERE v.sucursal.idSucursal = :idSucursal AND DATE(v.fechaVenta) = CURRENT_DATE")
    List<Venta> findVentasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT v FROM Venta v WHERE v.nroTransaccion LIKE %:searchTerm% OR v.nroComprobante LIKE %:searchTerm%")
    Page<Venta> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'COMPLETADA'")
    BigDecimal getTotalVentasDelDia();

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.sucursal.idSucursal = :idSucursal AND DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'COMPLETADA'")
    BigDecimal getTotalVentasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin AND v.estado = 'COMPLETADA'")
    BigDecimal getTotalVentasByPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                       @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(v) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'COMPLETADA'")
    Long countVentasDelDia();

    @Query("SELECT COUNT(v) FROM Venta v WHERE v.sucursal.idSucursal = :idSucursal AND DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'COMPLETADA'")
    Long countVentasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);
}
