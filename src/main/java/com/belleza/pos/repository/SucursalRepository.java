package com.belleza.pos.repository;

import com.belleza.pos.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Sucursal
 */
@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {

    Optional<Sucursal> findByNombre(String nombre);

    List<Sucursal> findByActivo(Boolean activo);

    Boolean existsByNombre(String nombre);
}
