package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateRubroRequest;
import com.belleza.pos.dto.request.UpdateRubroRequest;
import com.belleza.pos.dto.response.RubroResponse;
import com.belleza.pos.dto.response.RubroSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RubroService {
    RubroResponse create(CreateRubroRequest request);
    RubroResponse update(Integer id, UpdateRubroRequest request);
    RubroResponse getById(Integer id);
    RubroResponse getByNombre(String nombre);
    Page<RubroResponse> getAll(Pageable pageable);
    List<RubroSimpleResponse> getAllActive();
    RubroResponse activate(Integer id);
    RubroResponse deactivate(Integer id);
    void delete(Integer id);
    void deletePermanently(Integer id);
    boolean existsByNombre(String nombre);
}