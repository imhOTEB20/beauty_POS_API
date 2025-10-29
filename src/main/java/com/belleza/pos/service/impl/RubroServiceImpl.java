package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateRubroRequest;
import com.belleza.pos.dto.request.UpdateRubroRequest;
import com.belleza.pos.dto.response.RubroResponse;
import com.belleza.pos.dto.response.RubroSimpleResponse;
import com.belleza.pos.entity.Rubro;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.RubroMapper;
import com.belleza.pos.repository.ArticuloRepository;
import com.belleza.pos.repository.RubroRepository;
import com.belleza.pos.service.RubroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RubroServiceImpl implements RubroService {

    private final RubroRepository rubroRepository;
    private final ArticuloRepository articuloRepository;
    private final RubroMapper rubroMapper;

    @Override
    @Transactional
    public RubroResponse create(CreateRubroRequest request) {
        log.info("Creando rubro: {}", request.nombre());

        if (rubroRepository.existsByNombre(request.nombre())) {
            throw new BusinessException("Ya existe un rubro con el nombre: " + request.nombre());
        }

        Rubro rubro = rubroMapper.toEntity(request);
        rubro = rubroRepository.save(rubro);

        log.info("Rubro creado exitosamente con ID: {}", rubro.getIdRubro());
        return rubroMapper.toResponse(rubro, 0L);
    }

    @Override
    @Transactional
    public RubroResponse update(Integer id, UpdateRubroRequest request) {
        log.info("Actualizando rubro con ID: {}", id);

        Rubro rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", id));

        if (request.nombre() != null && !request.nombre().equals(rubro.getNombre())) {
            if (rubroRepository.existsByNombre(request.nombre())) {
                throw new BusinessException("Ya existe un rubro con el nombre: " + request.nombre());
            }
        }

        rubroMapper.updateEntity(rubro, request);
        rubro = rubroRepository.save(rubro);

        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(id).size();
        log.info("Rubro actualizado exitosamente: {}", id);
        return rubroMapper.toResponse(rubro, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public RubroResponse getById(Integer id) {
        log.debug("Obteniendo rubro por ID: {}", id);
        Rubro rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", id));
        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(id).size();
        return rubroMapper.toResponse(rubro, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public RubroResponse getByNombre(String nombre) {
        log.debug("Obteniendo rubro por nombre: {}", nombre);
        Rubro rubro = rubroRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "nombre", nombre));
        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(rubro.getIdRubro()).size();
        return rubroMapper.toResponse(rubro, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RubroResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los rubros con paginación");
        return rubroRepository.findAll(pageable)
                .map(rubro -> {
                    long cantidadArticulos = articuloRepository.findByRubro_IdRubro(rubro.getIdRubro()).size();
                    return rubroMapper.toResponse(rubro, cantidadArticulos);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<RubroSimpleResponse> getAllActive() {
        log.debug("Obteniendo todos los rubros activos");
        return rubroRepository.findByActivo(true).stream()
                .map(rubroMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RubroResponse activate(Integer id) {
        log.info("Activando rubro: {}", id);
        Rubro rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", id));

        rubro.setActivo(true);
        rubro = rubroRepository.save(rubro);

        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(id).size();
        log.info("Rubro activado exitosamente: {}", id);
        return rubroMapper.toResponse(rubro, cantidadArticulos);
    }

    @Override
    @Transactional
    public RubroResponse deactivate(Integer id) {
        log.info("Desactivando rubro: {}", id);
        Rubro rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", id));

        rubro.setActivo(false);
        rubro = rubroRepository.save(rubro);

        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(id).size();
        log.info("Rubro desactivado exitosamente: {}", id);
        return rubroMapper.toResponse(rubro, cantidadArticulos);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando rubro (soft delete): {}", id);
        Rubro rubro = rubroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", id));

        rubro.setActivo(false);
        rubroRepository.save(rubro);

        log.info("Rubro eliminado exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando rubro permanentemente: {}", id);

        if (!rubroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rubro", "id", id);
        }

        long cantidadArticulos = articuloRepository.findByRubro_IdRubro(id).size();
        if (cantidadArticulos > 0) {
            throw new BusinessException(
                    "No se puede eliminar el rubro. Tiene " + cantidadArticulos + " artículo(s) asociado(s)");
        }

        rubroRepository.deleteById(id);
        log.info("Rubro eliminado permanentemente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return rubroRepository.existsByNombre(nombre);
    }
}
