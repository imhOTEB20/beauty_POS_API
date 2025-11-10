package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateProveedorRequest;
import com.belleza.pos.dto.request.UpdateProveedorRequest;
import com.belleza.pos.dto.response.ProveedorResponse;
import com.belleza.pos.dto.response.ProveedorSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProveedorService {
    ProveedorResponse create(CreateProveedorRequest request);
    ProveedorResponse update(Integer id, UpdateProveedorRequest request);
    ProveedorResponse getById(Integer id);
    ProveedorResponse getByNroProveedor(String nroProveedor);
    ProveedorResponse getByCuit(String cuit);
    Page<ProveedorResponse> getAll(Pageable pageable);
    List<ProveedorSimpleResponse> getAllActive();
    List<ProveedorResponse> getConCuentaCorriente();
    ProveedorResponse activate(Integer id);
    ProveedorResponse deactivate(Integer id);
    void delete(Integer id);
    void deletePermanently(Integer id);
    ProveedorResponse registrarPago(Integer id, BigDecimal monto);
    ProveedorResponse registrarCompra(Integer id, BigDecimal monto);
    boolean existsByNroProveedor(String nroProveedor);
    boolean existsByCuit(String cuit);
}
