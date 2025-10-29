package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateListaPrecioRequest;
import com.belleza.pos.dto.request.UpdateListaPrecioRequest;
import com.belleza.pos.dto.response.ListaPrecioResponse;
import com.belleza.pos.dto.response.ListaPrecioSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListaPrecioService {
    ListaPrecioResponse create(CreateListaPrecioRequest request);
    ListaPrecioResponse update(Integer id, UpdateListaPrecioRequest request);
    ListaPrecioResponse getById(Integer id);
    ListaPrecioResponse getByNombre(String nombre);
    Page<ListaPrecioResponse> getAll(Pageable pageable);
    List<ListaPrecioSimpleResponse> getAllActive();
    ListaPrecioResponse getPredeterminada();
    ListaPrecioResponse setPredeterminada(Integer id);
    ListaPrecioResponse activate(Integer id);
    ListaPrecioResponse deactivate(Integer id);
    void delete(Integer id);
    void deletePermanently(Integer id);
    boolean existsByNombre(String nombre);
}
