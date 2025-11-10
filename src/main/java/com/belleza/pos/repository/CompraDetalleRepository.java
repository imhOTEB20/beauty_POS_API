package com.belleza.pos.repository;

import com.belleza.pos.entity.CompraDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para CompraDetalle
 */
@Repository
public interface CompraDetalleRepository extends JpaRepository<CompraDetalle, Integer> {

    List<CompraDetalle> findByCompra_IdCompra(Integer idCompra);

    List<CompraDetalle> findByArticulo_IdArticulo(Integer idArticulo);

    @Modifying
    @Query("DELETE FROM CompraDetalle cd WHERE cd.compra.idCompra = :idCompra")
    void deleteByCompra_IdCompra(@Param("idCompra") Integer idCompra);

    @Query("SELECT cd FROM CompraDetalle cd WHERE cd.compra.idCompra = :idCompra ORDER BY cd.numeroLinea")
    List<CompraDetalle> findByCompraOrderByNumeroLinea(@Param("idCompra") Integer idCompra);
}
