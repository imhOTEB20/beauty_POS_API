// ==========================================
// NotaCreditoRepository.java
// ==========================================
package com.belleza.pos.repository;

import com.belleza.pos.entity.NotaCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para NotaCredito
 */
@Repository
public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Integer> {

    Optional<NotaCredito> findByNroComprobante(String nroComprobante);

    Boolean existsByNroComprobante(String nroComprobante);

    List<NotaCredito> findByEstado(String estado);

    Page<NotaCredito> findByEstado(String estado, Pageable pageable);

    List<NotaCredito> findByCliente_IdCliente(Integer idCliente);

    Page<NotaCredito> findByCliente_IdCliente(Integer idCliente, Pageable pageable);

    List<NotaCredito> findBySucursal_IdSucursal(Integer idSucursal);

    Page<NotaCredito> findBySucursal_IdSucursal(Integer idSucursal, Pageable pageable);

    @Query("SELECT nc FROM NotaCredito nc WHERE nc.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<NotaCredito> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT nc FROM NotaCredito nc WHERE nc.fecha BETWEEN :fechaInicio AND :fechaFin")
    Page<NotaCredito> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    @Query("SELECT nc FROM NotaCredito nc WHERE nc.cliente.idCliente = :idCliente " +
            "AND nc.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<NotaCredito> findByClienteAndFechaBetween(
            @Param("idCliente") Integer idCliente,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT nc FROM NotaCredito nc WHERE nc.sucursal.idSucursal = :idSucursal " +
            "AND nc.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<NotaCredito> findBySucursalAndFechaBetween(
            @Param("idSucursal") Integer idSucursal,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT nc FROM NotaCredito nc WHERE " +
            "(nc.nroComprobante LIKE %:searchTerm% OR nc.cliente.nombre LIKE %:searchTerm% " +
            "OR nc.cliente.apellido LIKE %:searchTerm%)")
    Page<NotaCredito> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT nc FROM NotaCredito nc WHERE nc.tipoComprobante = :tipoComprobante")
    List<NotaCredito> findByTipoComprobante(@Param("tipoComprobante") String tipoComprobante);

    @Query("SELECT COALESCE(SUM(nc.total), 0) FROM NotaCredito nc " +
            "WHERE nc.estado = 'ACTIVA' AND nc.fecha BETWEEN :fechaInicio AND :fechaFin")
    java.math.BigDecimal calcularTotalPeriodo(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT COALESCE(SUM(nc.total), 0) FROM NotaCredito nc " +
            "WHERE nc.cliente.idCliente = :idCliente AND nc.estado = 'ACTIVA'")
    java.math.BigDecimal calcularTotalPorCliente(@Param("idCliente") Integer idCliente);
}