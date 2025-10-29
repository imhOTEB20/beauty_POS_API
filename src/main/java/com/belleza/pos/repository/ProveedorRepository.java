package com.belleza.pos.repository;

import com.belleza.pos.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Proveedor
 */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByCuit(String cuit);
    Optional<Proveedor> findByNroProveedor(String nroProveedor);
    List<Proveedor> findByActivo(Boolean activo);
    Boolean existsByCuit(String cuit);
    Boolean existsByNroProveedor(String nroProveedor);
}
