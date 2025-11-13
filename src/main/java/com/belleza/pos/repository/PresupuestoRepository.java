package com.belleza.pos.repository;

import com.belleza.pos.entity.Presupuesto;
import com.belleza.pos.entity.enums.EstadoPresupuesto;
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

    List<Presupuesto> findByEstado(EstadoPresupuesto estado);

    Page<Presupuesto> findByEstado(EstadoPresupuesto estado, Pageable pageable);

    List<Presupuesto> findByCliente_IdCliente(Integer idCliente);

    Page<Presupuesto> findByCliente_IdCliente(Integer idCliente, Pageable pageable);

    List<Presupuesto> findBySucursal_IdSucursal(Integer idSucursal);

    Page<Presupuesto> findBySucursal_IdSucursal(Integer idSucursal, Pageable pageable);

    List<Presupuesto> findByUsuario_IdUsuario(Integer idUsuario);

    Page<Presupuesto> findByUsuario_IdUsuario(Integer idUsuario, Pageable pageable);

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto = CURRENT_DATE")
    List<Presupuesto> findPresupuestosDelDia();

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto = CURRENT_DATE")
    Page<Presupuesto> findPresupuestosDelDia(Pageable pageable);

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    List<Presupuesto> findByFechaPresupuestoBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin);

    @Query("SELECT p FROM Presupuesto p WHERE p.fechaPresupuesto BETWEEN :fechaInicio AND :fechaFin")
    Page<Presupuesto> findByFechaPresupuestoBetween(@Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin,
                                                    Pageable pageable);

    @Query("SELECT p FROM Presupuesto p WHERE p.sucursal.idSucursal = :idSucursal AND p.fechaPresupuesto = CURRENT_DATE")
    List<Presupuesto> findPresupuestosDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT p FROM Presupuesto p WHERE p.nroPresupuesto LIKE %:searchTerm% OR p.cliente.nombre LIKE %:searchTerm% OR p.cliente.apellido LIKE %:searchTerm%")
    Page<Presupuesto> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.fechaPresupuesto = CURRENT_DATE")
    Long countPresupuestosDelDia();

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.sucursal.idSucursal = :idSucursal AND p.fechaPresupuesto = CURRENT_DATE")
    Long countPresupuestosDelDiaBySucursal(@Param("idSucursal") Integer idSucursal);

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.estado = 'PENDIENTE'")
    Long countPresupuestosPendientes();

    @Query("SELECT COUNT(p) FROM Presupuesto p WHERE p.estado = 'CONVERTIDO_VENTA'")
    Long countPresupuestosConvertidos();
}
