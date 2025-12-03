// ==========================================
// PresupuestoDetalleRepository.java
// ==========================================
package com.belleza.pos.repository;

import com.belleza.pos.entity.PresupuestoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para PresupuestoDetalle
 */
@Repository
public interface PresupuestoDetalleRepository extends JpaRepository<PresupuestoDetalle, Integer> {

    List<PresupuestoDetalle> findByPresupuesto_IdPresupuesto(Integer idPresupuesto);

    @Query("SELECT pd FROM PresupuestoDetalle pd WHERE pd.articulo.idArticulo = :idArticulo")
    List<PresupuestoDetalle> findByArticulo(@Param("idArticulo") Integer idArticulo);

    void deleteByPresupuesto_IdPresupuesto(Integer idPresupuesto);
}