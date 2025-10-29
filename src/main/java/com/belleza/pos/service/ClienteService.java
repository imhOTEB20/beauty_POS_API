package com.belleza.pos.service;

import com.belleza.pos.dto.request.CreateClienteRequest;
import com.belleza.pos.dto.request.UpdateClienteRequest;
import com.belleza.pos.dto.response.ClienteResponse;
import com.belleza.pos.dto.response.ClienteSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ClienteService {
    ClienteResponse create(CreateClienteRequest request);
    ClienteResponse update(Integer id, UpdateClienteRequest request);
    ClienteResponse getById(Integer id);
    ClienteResponse getByNroCliente(String nroCliente);
    ClienteResponse getByNroDocumento(String nroDocumento);
    Page<ClienteResponse> getAll(Pageable pageable);
    List<ClienteSimpleResponse> getAllActive();
    Page<ClienteResponse> search(String searchTerm, Pageable pageable);
    List<ClienteResponse> getConCuentaCorriente();
    List<ClienteResponse> getConLimiteCreditoExcedido();
    List<ClienteResponse> getConSaldoPendiente();
    ClienteResponse activate(Integer id);
    ClienteResponse deactivate(Integer id);
    void delete(Integer id);
    void deletePermanently(Integer id);
    ClienteResponse registrarPago(Integer id, BigDecimal monto);
    ClienteResponse registrarVenta(Integer id, BigDecimal monto);
    boolean existsByNroCliente(String nroCliente);
    boolean existsByNroDocumento(String nroDocumento);
    boolean existsByEmail(String email);
}