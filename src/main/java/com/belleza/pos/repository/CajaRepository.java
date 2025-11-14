package com.belleza.pos.repository;

import com.belleza.pos.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Caja
 */
@Repository
public interface CajaRepository extends JpaRepository<Caja, Integer> {

    Optional<Caja> findByNumeroCaja(String numeroCaja);

    Boolean existsByNumeroCaja(String numeroCaja);

    List<Caja> findByActivo(Boolean activo);

    List<Caja> findBySucursal_IdSucursal(Integer idSucursal);

    List<Caja> findBySucursal_IdSucursalAndActivo(Integer idSucursal, Boolean activo);

    @Query("SELECT c FROM Caja c WHERE c.numeroCaja = :numeroCaja AND c.sucursal.idSucursal = :idSucursal")
    Optional<Caja> findByNumeroCajaAndSucursal(@Param("numeroCaja") String numeroCaja, @Param("idSucursal") Integer idSucursal);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Caja c " +
            "WHERE c.numeroCaja = :numeroCaja AND c.sucursal.idSucursal = :idSucursal")
    Boolean existsByNumeroCajaAndSucursal(@Param("numeroCaja") String numeroCaja, @Param("idSucursal") Integer idSucursal);
}
