package com.belleza.pos.repository;

import com.belleza.pos.entity.Articulo;
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
 * Repositorio para Articulo
 */
@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {

    Optional<Articulo> findByCodigoBarras(String codigoBarras);

    Boolean existsByCodigoBarras(String codigoBarras);

    List<Articulo> findByActivo(Boolean activo);

    Page<Articulo> findByActivo(Boolean activo, Pageable pageable);

    List<Articulo> findByRubro_IdRubro(Integer idRubro);

    Page<Articulo> findByRubro_IdRubro(Integer idRubro, Pageable pageable);

    @Query("SELECT a FROM Articulo a WHERE a.descripcion LIKE %:searchTerm% OR a.codigoBarras LIKE %:searchTerm%")
    Page<Articulo> search(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT a FROM Articulo a WHERE a.usaControlStock = true AND a.stockActual <= a.stockMinimo AND a.activo = true")
    List<Articulo> findArticulosConStockBajo();

    @Query("SELECT a FROM Articulo a WHERE a.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND a.activo = true")
    List<Articulo> findArticulosProximosAVencer(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

    List<Articulo> findByEnOferta(Boolean enOferta);

    List<Articulo> findByPublicarEnWeb(Boolean publicarEnWeb);
}
