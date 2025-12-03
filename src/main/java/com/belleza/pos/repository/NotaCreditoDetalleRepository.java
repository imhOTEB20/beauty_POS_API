// ==========================================
// NotaCreditoDetalleRepository.java
// ==========================================
package com.belleza.pos.repository;

import com.belleza.pos.entity.NotaCreditoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para NotaCreditoDetalle
 */
@Repository
public interface NotaCreditoDetalleRepository extends JpaRepository<NotaCreditoDetalle, Integer> {

    List<NotaCreditoDetalle> findByNotaCredito_IdNotaCredito(Integer idNotaCredito);

    @Query("SELECT ncd FROM NotaCreditoDetalle ncd WHERE ncd.articulo.idArticulo = :idArticulo")
    List<NotaCreditoDetalle> findByArticulo(@Param("idArticulo") Integer idArticulo);

    void deleteByNotaCredito_IdNotaCredito(Integer idNotaCredito);
}
