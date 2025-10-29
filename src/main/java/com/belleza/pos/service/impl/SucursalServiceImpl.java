package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateSucursalRequest;
import com.belleza.pos.dto.request.UpdateSucursalRequest;
import com.belleza.pos.dto.response.SucursalResponse;
import com.belleza.pos.dto.response.SucursalSimpleResponse;
import com.belleza.pos.entity.Sucursal;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.SucursalMapper;
import com.belleza.pos.repository.SucursalRepository;
import com.belleza.pos.repository.UsuarioRepository;
import com.belleza.pos.service.SucursalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Sucursal
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalMapper sucursalMapper;

    @Override
    @Transactional
    public SucursalResponse create(CreateSucursalRequest request) {
        log.info("Creando sucursal con nombre: {}", request.nombre());

        // Validar que el nombre no exista
        if (sucursalRepository.existsByNombre(request.nombre())) {
            throw new BusinessException("Ya existe una sucursal con el nombre: " + request.nombre());
        }

        // Crear sucursal
        Sucursal sucursal = sucursalMapper.toEntity(request);
        sucursal = sucursalRepository.save(sucursal);

        log.info("Sucursal creada exitosamente con ID: {}", sucursal.getIdSucursal());
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional
    public SucursalResponse update(Integer id, UpdateSucursalRequest request) {
        log.info("Actualizando sucursal con ID: {}", id);

        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));

        // Validar nombre si se está cambiando
        if (request.nombre() != null && !request.nombre().equals(sucursal.getNombre())) {
            if (sucursalRepository.existsByNombre(request.nombre())) {
                throw new BusinessException("Ya existe una sucursal con el nombre: " + request.nombre());
            }
        }

        // Actualizar sucursal
        sucursalMapper.updateEntity(sucursal, request);
        sucursal = sucursalRepository.save(sucursal);

        log.info("Sucursal actualizada exitosamente: {}", id);
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse getById(Integer id) {
        log.debug("Obteniendo sucursal por ID: {}", id);
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse getByNombre(String nombre) {
        log.debug("Obteniendo sucursal por nombre: {}", nombre);
        Sucursal sucursal = sucursalRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "nombre", nombre));
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SucursalResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las sucursales con paginación");
        return sucursalRepository.findAll(pageable)
                .map(sucursalMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SucursalSimpleResponse> getAllActive() {
        log.debug("Obteniendo todas las sucursales activas");
        return sucursalRepository.findByActivo(true).stream()
                .map(sucursalMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SucursalResponse activate(Integer id) {
        log.info("Activando sucursal: {}", id);
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));

        sucursal.setActivo(true);
        sucursal = sucursalRepository.save(sucursal);

        log.info("Sucursal activada exitosamente: {}", id);
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional
    public SucursalResponse deactivate(Integer id) {
        log.info("Desactivando sucursal: {}", id);
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));

        // Validar que no tenga usuarios activos
        long usuariosActivos = usuarioRepository.findActiveUsuariosBySucursal(id).size();
        if (usuariosActivos > 0) {
            throw new BusinessException(
                    "No se puede desactivar la sucursal. Tiene " + usuariosActivos + " usuario(s) activo(s) asignado(s)");
        }

        sucursal.setActivo(false);
        sucursal = sucursalRepository.save(sucursal);

        log.info("Sucursal desactivada exitosamente: {}", id);
        return sucursalMapper.toResponse(sucursal);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando sucursal (soft delete): {}", id);
        Sucursal sucursal = sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", id));

        // Validar que no tenga usuarios activos
        long usuariosActivos = usuarioRepository.findActiveUsuariosBySucursal(id).size();
        if (usuariosActivos > 0) {
            throw new BusinessException(
                    "No se puede eliminar la sucursal. Tiene " + usuariosActivos + " usuario(s) activo(s) asignado(s)");
        }

        sucursal.setActivo(false);
        sucursalRepository.save(sucursal);

        log.info("Sucursal eliminada exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando sucursal permanentemente: {}", id);

        if (!sucursalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sucursal", "id", id);
        }

        // Validar que no tenga usuarios asignados
        long usuariosTotal = usuarioRepository.findBySucursal_IdSucursal(id).size();
        if (usuariosTotal > 0) {
            throw new BusinessException(
                    "No se puede eliminar permanentemente la sucursal. Tiene " + usuariosTotal + " usuario(s) asignado(s)");
        }

        sucursalRepository.deleteById(id);
        log.info("Sucursal eliminada permanentemente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return sucursalRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsuariosBySucursal(Integer idSucursal) {
        log.debug("Contando usuarios de sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return usuarioRepository.findBySucursal_IdSucursal(idSucursal).size();
    }
}