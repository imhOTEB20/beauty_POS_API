package com.belleza.pos.repository;

import com.belleza.pos.entity.VentaFormaPago;
import com.belleza.pos.entity.enums.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio para VentaFormaPago
 */
@Repository
public interface VentaFormaPagoRepository extends JpaRepository<VentaFormaPago, Integer> {

    List<VentaFormaPago> findByVenta_IdVenta(Integer idVenta);

    List<VentaFormaPago> findByFormaPago(FormaPago formaPago);

    @Modifying
    @Query("DELETE FROM VentaFormaPago vfp WHERE vfp.venta.idVenta = :idVenta")
    void deleteByVenta_IdVenta(@Param("idVenta") Integer idVenta);

    @Query("SELECT COALESCE(SUM(vfp.monto), 0) FROM VentaFormaPago vfp WHERE vfp.venta.idVenta = :idVenta")
    BigDecimal getTotalPagadoByVenta(@Param("idVenta") Integer idVenta);

    @Query("SELECT COALESCE(SUM(vfp.monto), 0) FROM VentaFormaPago vfp WHERE vfp.formaPago = :formaPago AND DATE(vfp.venta.fechaVenta) = CURRENT_DATE")
    BigDecimal getTotalByFormaPagoDelDia(@Param("formaPago") FormaPago formaPago);
}
