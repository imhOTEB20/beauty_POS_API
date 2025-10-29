package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateListaPrecioRequest;
import com.belleza.pos.dto.request.UpdateListaPrecioRequest;
import com.belleza.pos.dto.response.ListaPrecioResponse;
import com.belleza.pos.dto.response.ListaPrecioSimpleResponse;
import com.belleza.pos.entity.ListaPrecio;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.ListaPrecioMapper;
import com.belleza.pos.repository.ArticuloPrecioRepository;
import com.belleza.pos.repository.ListaPrecioRepository;
import com.belleza.pos.service.ListaPrecioService;
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
public class ListaPrecioServiceImpl implements ListaPrecioService {

    private final ListaPrecioRepository listaPrecioRepository;
    private final ArticuloPrecioRepository articuloPrecioRepository;
    private final ListaPrecioMapper listaPrecioMapper;

    @Override
    @Transactional
    public ListaPrecioResponse create(CreateListaPrecioRequest request) {
        log.info("Creando lista de precios: {}", request.nombre());

        if (listaPrecioRepository.existsByNombre(request.nombre())) {
            throw new BusinessException("Ya existe una lista con el nombre: " + request.nombre());
        }

        // Si se marca como predeterminada, quitar la actual
        if (request.esPredeterminada()) {
            listaPrecioRepository.findByEsPredeterminada(true).ifPresent(lista -> {
                lista.setEsPredeterminada(false);
                listaPrecioRepository.save(lista);
            });
        }

        ListaPrecio lista = listaPrecioMapper.toEntity(request);
        lista = listaPrecioRepository.save(lista);

        log.info("Lista de precios creada exitosamente con ID: {}", lista.getIdLista());
        return listaPrecioMapper.toResponse(lista, 0L);
    }

    @Override
    @Transactional
    public ListaPrecioResponse update(Integer id, UpdateListaPrecioRequest request) {
        log.info("Actualizando lista de precios con ID: {}", id);

        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));

        if (request.nombre() != null && !request.nombre().equals(lista.getNombre())) {
            if (listaPrecioRepository.existsByNombre(request.nombre())) {
                throw new BusinessException("Ya existe una lista con el nombre: " + request.nombre());
            }
        }

        // Si se marca como predeterminada, quitar la actual
        if (request.esPredeterminada() != null && request.esPredeterminada() && !lista.getEsPredeterminada()) {
            listaPrecioRepository.findByEsPredeterminada(true).ifPresent(l -> {
                if (!l.getIdLista().equals(id)) {
                    l.setEsPredeterminada(false);
                    listaPrecioRepository.save(l);
                }
            });
        }

        listaPrecioMapper.updateEntity(lista, request);
        lista = listaPrecioRepository.save(lista);

        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        log.info("Lista de precios actualizada exitosamente: {}", id);
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecioResponse getById(Integer id) {
        log.debug("Obteniendo lista de precios por ID: {}", id);
        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));
        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecioResponse getByNombre(String nombre) {
        log.debug("Obteniendo lista de precios por nombre: {}", nombre);
        ListaPrecio lista = listaPrecioRepository.findByNombre(nombre)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "nombre", nombre));
        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(lista.getIdLista()).size();
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ListaPrecioResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las listas de precios con paginación");
        return listaPrecioRepository.findAll(pageable)
                .map(lista -> {
                    long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(lista.getIdLista()).size();
                    return listaPrecioMapper.toResponse(lista, cantidadArticulos);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaPrecioSimpleResponse> getAllActive() {
        log.debug("Obteniendo todas las listas de precios activas");
        return listaPrecioRepository.findByActivo(true).stream()
                .map(listaPrecioMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ListaPrecioResponse getPredeterminada() {
        log.debug("Obteniendo lista de precios predeterminada");
        ListaPrecio lista = listaPrecioRepository.findByEsPredeterminada(true)
                .orElseThrow(() -> new ResourceNotFoundException("No hay una lista de precios predeterminada configurada"));
        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(lista.getIdLista()).size();
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional
    public ListaPrecioResponse setPredeterminada(Integer id) {
        log.info("Estableciendo lista de precios predeterminada: {}", id);

        // Quitar predeterminada actual
        listaPrecioRepository.findByEsPredeterminada(true).ifPresent(lista -> {
            lista.setEsPredeterminada(false);
            listaPrecioRepository.save(lista);
        });

        // Establecer nueva predeterminada
        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));

        lista.setEsPredeterminada(true);
        lista = listaPrecioRepository.save(lista);

        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        log.info("Lista de precios predeterminada establecida exitosamente: {}", id);
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional
    public ListaPrecioResponse activate(Integer id) {
        log.info("Activando lista de precios: {}", id);
        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));

        lista.setActivo(true);
        lista = listaPrecioRepository.save(lista);

        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        log.info("Lista de precios activada exitosamente: {}", id);
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional
    public ListaPrecioResponse deactivate(Integer id) {
        log.info("Desactivando lista de precios: {}", id);
        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));

        if (lista.getEsPredeterminada()) {
            throw new BusinessException("No se puede desactivar la lista predeterminada");
        }

        lista.setActivo(false);
        lista = listaPrecioRepository.save(lista);

        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        log.info("Lista de precios desactivada exitosamente: {}", id);
        return listaPrecioMapper.toResponse(lista, cantidadArticulos);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando lista de precios (soft delete): {}", id);
        ListaPrecio lista = listaPrecioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", id));

        if (lista.getEsPredeterminada()) {
            throw new BusinessException("No se puede eliminar la lista predeterminada");
        }

        lista.setActivo(false);
        listaPrecioRepository.save(lista);

        log.info("Lista de precios eliminada exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando lista de precios permanentemente: {}", id);

        if (!listaPrecioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lista de precios", "id", id);
        }

        ListaPrecio lista = listaPrecioRepository.findById(id).get();
        if (lista.getEsPredeterminada()) {
            throw new BusinessException("No se puede eliminar la lista predeterminada");
        }

        long cantidadArticulos = articuloPrecioRepository.findByListaPrecio_IdLista(id).size();
        if (cantidadArticulos > 0) {
            throw new BusinessException(
                    "No se puede eliminar la lista. Tiene " + cantidadArticulos + " artículo(s) con precio asignado");
        }

        listaPrecioRepository.deleteById(id);
        log.info("Lista de precios eliminada permanentemente: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return listaPrecioRepository.existsByNombre(nombre);
    }
}