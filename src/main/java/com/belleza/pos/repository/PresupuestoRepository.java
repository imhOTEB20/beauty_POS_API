// ==========================================
// PresupuestoRepository.java
// ==========================================
package com.belleza.pos.repository;

import com.belleza.pos.entity.Presupuesto;
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
 * Repositorio para Presupuesto
 */
@Repository
public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer> {

    Optional<Presupuesto> findByNroPresupuesto(String nroPresupuesto);

    Boolean existsByNroPresupuesto(String nroPresupuesto);

    List<Presupuesto> findByEstado(String estado);

    Page<Presupuesto> findByEstado(String estado, Pageable pageable);

    List<Presupuesto> findByCliente_IdCliente(Integer idCliente);

    Page<Presupuesto> findByCliente_IdCliente(Integer idCliente, Pageable pageable);

    List<Presupuesto> findBySucursal_IdSucursal(Integer idSucursal);

    Page<Presupuesto> findBySucursal_IdSucursal(Integer idSucursal, Pageable pageable);

    List<Presupuesto> findByUsuario_IdUsuario(Integer idUsuario);

    Page<Presupuesto> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    List<Presupuesto> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    Page<Presupuesto> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            Pageable pageable
    );

    @Query("SELECT p FROM Presupuesto p WHERE p.cliente.idCliente = :idCliente " +
            "AND p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    List<Presupuesto> findByClienteAndFechaBetween(
            @Param("idCliente") Integer idCliente,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT p FROM Presupuesto p WHERE p.sucursal.idSucursal = :idSucursal " +
            "AND p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    List<Presupuesto> findBySucursalAndFechaBetween(
            @Param("idSucursal") Integer idSucursal,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT p FROM Presupuesto p WHERE " +
            "(p.nroPresupuesto LIKE %:searchTerm% OR p.cliente.nombre LIKE %:searchTerm% " +
            "OR p.cliente.apellido LIKE %:searchTerm%)")
    Page<Presupuesto> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT p FROM Presupuesto p WHERE p.estado = 'PENDIENTE' " +
            "AND p.fechaPresupuesto < :fechaLimite")
    List<Presupuesto> findPresupuestosVencidos(@Param("fechaLimite") LocalDate fechaLimite);

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    @Query("SELECT COALESCE(SUM(p.total), 0) FROM Presupuesto p " +
            "WHERE p.estado = :estado AND p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    java.math.BigDecimal calcularTotalPorEstadoYPeriodo(
            @Param("estado") String estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}