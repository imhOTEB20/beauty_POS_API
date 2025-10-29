package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateSucursalRequest;
import com.belleza.pos.dto.request.UpdateSucursalRequest;
import com.belleza.pos.dto.response.SucursalResponse;
import com.belleza.pos.dto.response.SucursalSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de Sucursal
 */
public interface SucursalService {

    /**
     * Crea una nueva sucursal
     */
    SucursalResponse create(CreateSucursalRequest request);

    /**
     * Actualiza una sucursal existente
     */
    SucursalResponse update(Integer id, UpdateSucursalRequest request);

    /**
     * Obtiene una sucursal por ID
     */
    SucursalResponse getById(Integer id);

    /**
     * Obtiene una sucursal por nombre
     */
    SucursalResponse getByNombre(String nombre);

    /**
     * Obtiene todas las sucursales con paginación
     */
    Page<SucursalResponse> getAll(Pageable pageable);

    /**
     * Obtiene todas las sucursales activas
     */
    List<SucursalSimpleResponse> getAllActive();

    /**
     * Activa una sucursal
     */
    SucursalResponse activate(Integer id);

    /**
     * Desactiva una sucursal
     */
    SucursalResponse deactivate(Integer id);

    /**
     * Elimina una sucursal (soft delete)
     */
    void delete(Integer id);

    /**
     * Elimina una sucursal permanentemente
     */
    void deletePermanently(Integer id);

    /**
     * Verifica si existe una sucursal por nombre
     */
    boolean existsByNombre(String nombre);

    /**
     * Cuenta el número de usuarios en una sucursal
     */
    long countUsuariosBySucursal(Integer idSucursal);
}