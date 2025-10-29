package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateClienteRequest;
import com.belleza.pos.dto.request.UpdateClienteRequest;
import com.belleza.pos.dto.response.ClienteResponse;
import com.belleza.pos.dto.response.ClienteSimpleResponse;
import com.belleza.pos.entity.Cliente;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.ClienteMapper;
import com.belleza.pos.repository.ClienteRepository;
import com.belleza.pos.service.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponse create(CreateClienteRequest request) {
        log.info("Creando cliente: {}", request.nombre());

        if (request.nroCliente() != null && clienteRepository.existsByNroCliente(request.nroCliente())) {
            throw new BusinessException("Ya existe un cliente con el número: " + request.nroCliente());
        }

        if (request.nroDocumento() != null && clienteRepository.existsByNroDocumento(request.nroDocumento())) {
            throw new BusinessException("Ya existe un cliente con el documento: " + request.nroDocumento());
        }

        if (request.email() != null && clienteRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ya existe un cliente con el email: " + request.email());
        }

        Cliente cliente = clienteMapper.toEntity(request);

        // Generar número de cliente si no se proporciona
        if (cliente.getNroCliente() == null || cliente.getNroCliente().isEmpty()) {
            cliente.setNroCliente(generarNroCliente());
        }

        cliente = clienteRepository.save(cliente);

        log.info("Cliente creado exitosamente con ID: {}", cliente.getIdCliente());
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse update(Integer id, UpdateClienteRequest request) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        if (request.email() != null && !request.email().equals(cliente.getEmail())) {
            if (clienteRepository.existsByEmail(request.email())) {
                throw new BusinessException("Ya existe un cliente con el email: " + request.email());
            }
        }

        clienteMapper.updateEntity(cliente, request);
        cliente = clienteRepository.save(cliente);

        log.info("Cliente actualizado exitosamente: {}", id);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getById(Integer id) {
        log.debug("Obteniendo cliente por ID: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getByNroCliente(String nroCliente) {
        log.debug("Obteniendo cliente por número: {}", nroCliente);
        Cliente cliente = clienteRepository.findByNroCliente(nroCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "número", nroCliente));
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse getByNroDocumento(String nroDocumento) {
        log.debug("Obteniendo cliente por documento: {}", nroDocumento);
        Cliente cliente = clienteRepository.findByNroDocumento(nroDocumento)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "documento", nroDocumento));
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los clientes con paginación");
        return clienteRepository.findAll(pageable)
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteSimpleResponse> getAllActive() {
        log.debug("Obteniendo todos los clientes activos");
        return clienteRepository.findByActivo(true).stream()
                .map(clienteMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando clientes con término: {}", searchTerm);
        return clienteRepository.search(searchTerm, pageable)
                .map(clienteMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> getConCuentaCorriente() {
        log.debug("Obteniendo clientes con cuenta corriente");
        return clienteRepository.findConCuentaCorriente().stream()
                .map(clienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> getConLimiteCreditoExcedido() {
        log.debug("Obteniendo clientes con límite de crédito excedido");
        return clienteRepository.findConLimiteCreditoExcedido().stream()
                .map(clienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> getConSaldoPendiente() {
        log.debug("Obteniendo clientes con saldo pendiente");
        return clienteRepository.findConSaldoPendiente().stream()
                .map(clienteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClienteResponse activate(Integer id) {
        log.info("Activando cliente: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setActivo(true);
        cliente = clienteRepository.save(cliente);

        log.info("Cliente activado exitosamente: {}", id);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse deactivate(Integer id) {
        log.info("Desactivando cliente: {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setActivo(false);
        cliente = clienteRepository.save(cliente);

        log.info("Cliente desactivado exitosamente: {}", id);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando cliente (soft delete): {}", id);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setActivo(false);
        clienteRepository.save(cliente);

        log.info("Cliente eliminado exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando cliente permanentemente: {}", id);

        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }

        clienteRepository.deleteById(id);
        log.info("Cliente eliminado permanentemente: {}", id);
    }

    @Override
    @Transactional
    public ClienteResponse registrarPago(Integer id, BigDecimal monto) {
        log.info("Registrando pago de cliente {}: {}", id, monto);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        if (!cliente.getCuentaCorrienteHabilitada()) {
            throw new BusinessException("El cliente no tiene cuenta corriente habilitada");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del pago debe ser mayor a cero");
        }

        BigDecimal nuevoSaldo = cliente.getSaldoActual().subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }

        cliente.setSaldoActual(nuevoSaldo);
        cliente = clienteRepository.save(cliente);

        log.info("Pago registrado exitosamente. Nuevo saldo: {}", nuevoSaldo);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse registrarVenta(Integer id, BigDecimal monto) {
        log.info("Registrando venta para cliente {}: {}", id, monto);

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        if (!cliente.getCuentaCorrienteHabilitada()) {
            throw new BusinessException("El cliente no tiene cuenta corriente habilitada");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto de la venta debe ser mayor a cero");
        }

        BigDecimal nuevoSaldo = cliente.getSaldoActual().add(monto);

        // Validar límite de crédito si no es ilimitado
        if (!"ILIMITADA".equals(cliente.getTipoLimite())) {
            if (nuevoSaldo.compareTo(cliente.getLimiteCredito()) > 0) {
                throw new BusinessException("La venta excede el límite de crédito del cliente");
            }
        }

        cliente.setSaldoActual(nuevoSaldo);
        cliente = clienteRepository.save(cliente);

        log.info("Venta registrada exitosamente. Nuevo saldo: {}", nuevoSaldo);
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroCliente(String nroCliente) {
        return clienteRepository.existsByNroCliente(nroCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroDocumento(String nroDocumento) {
        return clienteRepository.existsByNroDocumento(nroDocumento);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return clienteRepository.existsByEmail(email);
    }

    private String generarNroCliente() {
        long count = clienteRepository.count() + 1;
        return String.format("CLI%06d", count);
    }
}