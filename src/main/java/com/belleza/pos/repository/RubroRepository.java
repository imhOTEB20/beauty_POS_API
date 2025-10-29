package com.belleza.pos.repository;

import com.belleza.pos.entity.Rubro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Rubro
 */
@Repository
public interface RubroRepository extends JpaRepository<Rubro, Integer> {
    Optional<Rubro> findByNombre(String nombre);
    List<Rubro> findByActivo(Boolean activo);
    Boolean existsByNombre(String nombre);
}
