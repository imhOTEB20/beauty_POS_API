package com.belleza.pos.service;

import com.belleza.pos.dto.request.ChangePasswordRequest;
import com.belleza.pos.dto.request.CreateUsuarioRequest;
import com.belleza.pos.dto.request.UpdateUsuarioRequest;
import com.belleza.pos.dto.response.UsuarioResponse;
import com.belleza.pos.dto.response.UsuarioSimpleResponse;
import com.belleza.pos.entity.enums.RolUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de Usuario
 */
public interface UsuarioService {

    /**
     * Crea un nuevo usuario
     */
    UsuarioResponse create(CreateUsuarioRequest request);

    /**
     * Actualiza un usuario existente
     */
    UsuarioResponse update(Integer id, UpdateUsuarioRequest request);

    /**
     * Obtiene un usuario por ID
     */
    UsuarioResponse getById(Integer id);

    /**
     * Obtiene un usuario por username
     */
    UsuarioResponse getByUsername(String username);

    /**
     * Obtiene todos los usuarios con paginación
     */
    Page<UsuarioResponse> getAll(Pageable pageable);

    /**
     * Obtiene todos los usuarios activos
     */
    List<UsuarioSimpleResponse> getAllActive();

    /**
     * Obtiene usuarios por rol
     */
    List<UsuarioSimpleResponse> getByRol(RolUsuario rol);

    /**
     * Obtiene usuarios por sucursal
     */
    List<UsuarioSimpleResponse> getBySucursal(Integer idSucursal);

    /**
     * Activa un usuario
     */
    UsuarioResponse activate(Integer id);

    /**
     * Desactiva un usuario
     */
    UsuarioResponse deactivate(Integer id);

    /**
     * Elimina un usuario (soft delete)
     */
    void delete(Integer id);

    /**
     * Elimina un usuario permanentemente
     */
    void deletePermanently(Integer id);

    /**
     * Cambia la contraseña de un usuario
     */
    void changePassword(Integer id, ChangePasswordRequest request);

    /**
     * Resetea la contraseña de un usuario (solo admin)
     */
    void resetPassword(Integer id, String newPassword);

    /**
     * Verifica si existe un username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un email
     */
    boolean existsByEmail(String email);
}