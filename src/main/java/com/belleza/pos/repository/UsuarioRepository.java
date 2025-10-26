package com.belleza.pos.repository;

import com.belleza.pos.entity.Usuario;
import com.belleza.pos.entity.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<Usuario> findByActivo(Boolean activo);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findBySucursal_IdSucursal(Integer idSucursal);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true AND u.sucursal.idSucursal = :idSucursal")
    List<Usuario> findActiveUsuariosBySucursal(Integer idSucursal);
}
