package com.belleza.pos.repository;

import com.belleza.pos.entity.ListaPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para ListaPrecio
 */
@Repository
public interface ListaPrecioRepository extends JpaRepository<ListaPrecio, Integer> {
    Optional<ListaPrecio> findByNombre(String nombre);
    List<ListaPrecio> findByActivo(Boolean activo);
    Optional<ListaPrecio> findByEsPredeterminada(Boolean esPredeterminada);
    Boolean existsByNombre(String nombre);
}

