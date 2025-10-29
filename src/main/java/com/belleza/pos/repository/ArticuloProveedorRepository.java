package com.belleza.pos.repository;

import com.belleza.pos.entity.ArticuloProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para ArticuloProveedor
 */
@Repository
public interface ArticuloProveedorRepository extends JpaRepository<ArticuloProveedor, Integer> {

    List<ArticuloProveedor> findByArticulo_IdArticulo(Integer idArticulo);

    List<ArticuloProveedor> findByProveedor_IdProveedor(Integer idProveedor);

    Optional<ArticuloProveedor> findByArticulo_IdArticuloAndProveedor_IdProveedor(Integer idArticulo, Integer idProveedor);

    @Query("SELECT ap FROM ArticuloProveedor ap WHERE ap.articulo.idArticulo = :idArticulo AND ap.esPredeterminado = true")
    Optional<ArticuloProveedor> findProveedorPredeterminadoByArticulo(@Param("idArticulo") Integer idArticulo);

    void deleteByArticulo_IdArticulo(Integer idArticulo);
}
