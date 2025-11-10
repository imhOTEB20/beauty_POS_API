package com.belleza.pos.repository;

import com.belleza.pos.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para VentaDetalle
 */
@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Integer> {

    List<VentaDetalle> findByVenta_IdVenta(Integer idVenta);

    List<VentaDetalle> findByArticulo_IdArticulo(Integer idArticulo);

    @Modifying
    @Query("DELETE FROM VentaDetalle vd WHERE vd.venta.idVenta = :idVenta")
    void deleteByVenta_IdVenta(@Param("idVenta") Integer idVenta);

    @Query("SELECT vd FROM VentaDetalle vd WHERE vd.venta.idVenta = :idVenta ORDER BY vd.numeroLinea")
    List<VentaDetalle> findByVentaOrderByNumeroLinea(@Param("idVenta") Integer idVenta);
}
