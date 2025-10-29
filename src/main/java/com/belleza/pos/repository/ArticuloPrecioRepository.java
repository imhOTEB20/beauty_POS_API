package com.belleza.pos.repository;

import com.belleza.pos.entity.ArticuloPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para ArticuloPrecio
 */
@Repository
public interface ArticuloPrecioRepository extends JpaRepository<ArticuloPrecio, Integer> {

    List<ArticuloPrecio> findByArticulo_IdArticulo(Integer idArticulo);

    List<ArticuloPrecio> findByListaPrecio_IdLista(Integer idLista);

    Optional<ArticuloPrecio> findByArticulo_IdArticuloAndListaPrecio_IdLista(Integer idArticulo, Integer idLista);

    @Query("SELECT ap FROM ArticuloPrecio ap WHERE ap.articulo.idArticulo = :idArticulo AND ap.listaPrecio.esPredeterminada = true")
    Optional<ArticuloPrecio> findPrecioPredeterminadoByArticulo(@Param("idArticulo") Integer idArticulo);

    void deleteByArticulo_IdArticulo(Integer idArticulo);
}
