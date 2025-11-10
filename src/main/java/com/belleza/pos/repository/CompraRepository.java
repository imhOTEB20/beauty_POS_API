package com.belleza.pos.repository;

import com.belleza.pos.entity.Compra;
import com.belleza.pos.entity.enums.EstadoCompra;
import com.belleza.pos.entity.enums.TipoComprobante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Compra
 */
@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {

    Optional<Compra> findByNroComprobante(String nroComprobante);

    Boolean existsByNroComprobante(String nroComprobante);

    List<Compra> findByEstado(EstadoCompra estado);

    Page<Compra> findByEstado(EstadoCompra estado, Pageable pageable);

    List<Compra> findByProveedor_IdProveedor(Integer idProveedor);

    Page<Compra> findByProveedor_IdProveedor(Integer idProveedor, Pageable pageable);

    List<Compra> findBySucursal_IdSucursal(Integer idSucursal);

    Page<Compra> findBySucursal_IdSucursal(Integer idSucursal, Pageable pageable);

    List<Compra> findByUsuario_IdUsuario(Integer idUsuario);

    Page<Compra> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);

    List<Compra> findByTipoComprobante(TipoComprobante tipoComprobante);

    Page<Compra> findByTipoComprobante(TipoComprobante tipoComprobante, Pageable pageable);

    @Query("SELECT c FROM Compra c WHERE c.fechaCompra = CURRENT_DATE")
    List<Compra> findComprasDelDia();

    @Query("SELECT c FROM Compra c WHERE c.fechaCompra = CURRENT_DATE")
    Page<Compra> findComprasDelDia(Pageable pageable);

    @Query("SELECT c FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin")
    List<Compra> findByFechaCompraBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT c FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin")
    Page<Compra> findByFechaCompraBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                          @Param("fechaFin") LocalDate fechaFin,
                                          Pageable pageable);

    @Query("SELECT c FROM Compra c WHERE c.sucursal.idSucursal = :idSucursal AND c.fechaCompra = CURRENT_DATE")
    List<Compra> findComprasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT c FROM Compra c WHERE c.nroComprobante LIKE %:searchTerm% OR c.proveedor.razonSocial LIKE %:searchTerm%")
    Page<Compra> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.fechaCompra = CURRENT_DATE AND c.estado = 'COMPLETADA'")
    BigDecimal getTotalComprasDelDia();

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.sucursal.idSucursal = :idSucursal AND c.fechaCompra = CURRENT_DATE AND c.estado = 'COMPLETADA'")
    BigDecimal getTotalComprasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Compra c WHERE c.fechaCompra BETWEEN :fechaInicio AND :fechaFin AND c.estado = 'COMPLETADA'")
    BigDecimal getTotalComprasByPeriodo(@Param("fechaInicio") LocalDate fechaInicio,
                                        @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT COUNT(c) FROM Compra c WHERE c.fechaCompra = CURRENT_DATE AND c.estado = 'COMPLETADA'")
    Long countComprasDelDia();

    @Query("SELECT COUNT(c) FROM Compra c WHERE c.sucursal.idSucursal = :idSucursal AND c.fechaCompra = CURRENT_DATE AND c.estado = 'COMPLETADA'")
    Long countComprasDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);
}