package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateProveedorRequest;
import com.belleza.pos.dto.request.UpdateProveedorRequest;
import com.belleza.pos.dto.response.ProveedorResponse;
import com.belleza.pos.dto.response.ProveedorSimpleResponse;
import com.belleza.pos.entity.Proveedor;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.ProveedorMapper;
import com.belleza.pos.repository.ArticuloProveedorRepository;
import com.belleza.pos.repository.ProveedorRepository;
import com.belleza.pos.service.ProveedorService;
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
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;
    private final ProveedorMapper proveedorMapper;

    @Override
    @Transactional
    public ProveedorResponse create(CreateProveedorRequest request) {
        log.info("Creando proveedor: {}", request.razonSocial());

        if (proveedorRepository.existsByCuit(request.cuit())) {
            throw new BusinessException("Ya existe un proveedor con el CUIT: " + request.cuit());
        }

        if (request.nroProveedor() != null && proveedorRepository.existsByNroProveedor(request.nroProveedor())) {
            throw new BusinessException("Ya existe un proveedor con el número: " + request.nroProveedor());
        }

        Proveedor proveedor = proveedorMapper.toEntity(request);

        if (proveedor.getNroProveedor() == null || proveedor.getNroProveedor().isEmpty()) {
            proveedor.setNroProveedor(generarNroProveedor());
        }

        proveedor = proveedorRepository.save(proveedor);

        log.info("Proveedor creado exitosamente con ID: {}", proveedor.getIdProveedor());
        return proveedorMapper.toResponse(proveedor, 0L);
    }

    @Override
    @Transactional
    public ProveedorResponse update(Integer id, UpdateProveedorRequest request) {
        log.info("Actualizando proveedor con ID: {}", id);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        proveedorMapper.updateEntity(proveedor, request);
        proveedor = proveedorRepository.save(proveedor);

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        log.info("Proveedor actualizado exitosamente: {}", id);
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse getById(Integer id) {
        log.debug("Obteniendo proveedor por ID: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));
        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse getByNroProveedor(String nroProveedor) {
        log.debug("Obteniendo proveedor por número: {}", nroProveedor);
        Proveedor proveedor = proveedorRepository.findByNroProveedor(nroProveedor)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "número", nroProveedor));
        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(proveedor.getIdProveedor()).size();
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse getByCuit(String cuit) {
        log.debug("Obteniendo proveedor por CUIT: {}", cuit);
        Proveedor proveedor = proveedorRepository.findByCuit(cuit)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "CUIT", cuit));
        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(proveedor.getIdProveedor()).size();
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProveedorResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los proveedores con paginación");
        return proveedorRepository.findAll(pageable)
                .map(proveedor -> {
                    long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(proveedor.getIdProveedor()).size();
                    return proveedorMapper.toResponse(proveedor, cantidadArticulos);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorSimpleResponse> getAllActive() {
        log.debug("Obteniendo todos los proveedores activos");
        return proveedorRepository.findByActivo(true).stream()
                .map(proveedorMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> getConCuentaCorriente() {
        log.debug("Obteniendo proveedores con cuenta corriente");
        return proveedorRepository.findAll().stream()
                .filter(Proveedor::getCuentaCorrienteHabilitada)
                .filter(Proveedor::getActivo)
                .map(proveedor -> {
                    long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(proveedor.getIdProveedor()).size();
                    return proveedorMapper.toResponse(proveedor, cantidadArticulos);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProveedorResponse activate(Integer id) {
        log.info("Activando proveedor: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        proveedor.setActivo(true);
        proveedor = proveedorRepository.save(proveedor);

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        log.info("Proveedor activado exitosamente: {}", id);
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional
    public ProveedorResponse deactivate(Integer id) {
        log.info("Desactivando proveedor: {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        proveedor.setActivo(false);
        proveedor = proveedorRepository.save(proveedor);

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        log.info("Proveedor desactivado exitosamente: {}", id);
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando proveedor (soft delete): {}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);

        log.info("Proveedor eliminado exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando proveedor permanentemente: {}", id);

        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor", "id", id);
        }

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        if (cantidadArticulos > 0) {
            throw new BusinessException(
                    "No se puede eliminar el proveedor. Tiene " + cantidadArticulos + " artículo(s) asociado(s)");
        }

        proveedorRepository.deleteById(id);
        log.info("Proveedor eliminado permanentemente: {}", id);
    }

    @Override
    @Transactional
    public ProveedorResponse registrarPago(Integer id, BigDecimal monto) {
        log.info("Registrando pago a proveedor {}: {}", id, monto);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        if (!proveedor.getCuentaCorrienteHabilitada()) {
            throw new BusinessException("El proveedor no tiene cuenta corriente habilitada");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto del pago debe ser mayor a cero");
        }

        BigDecimal nuevoSaldo = proveedor.getSaldoActual().subtract(monto);
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            nuevoSaldo = BigDecimal.ZERO;
        }

        proveedor.setSaldoActual(nuevoSaldo);
        proveedor = proveedorRepository.save(proveedor);

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        log.info("Pago registrado exitosamente. Nuevo saldo: {}", nuevoSaldo);
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional
    public ProveedorResponse registrarCompra(Integer id, BigDecimal monto) {
        log.info("Registrando compra al proveedor {}: {}", id, monto);

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", id));

        if (!proveedor.getCuentaCorrienteHabilitada()) {
            throw new BusinessException("El proveedor no tiene cuenta corriente habilitada");
        }

        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto de la compra debe ser mayor a cero");
        }

        BigDecimal nuevoSaldo = proveedor.getSaldoActual().add(monto);

        if (nuevoSaldo.compareTo(proveedor.getLimiteCredito()) > 0) {
            throw new BusinessException("La compra excede el límite de crédito del proveedor");
        }

        proveedor.setSaldoActual(nuevoSaldo);
        proveedor = proveedorRepository.save(proveedor);

        long cantidadArticulos = articuloProveedorRepository.findByProveedor_IdProveedor(id).size();
        log.info("Compra registrada exitosamente. Nuevo saldo: {}", nuevoSaldo);
        return proveedorMapper.toResponse(proveedor, cantidadArticulos);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroProveedor(String nroProveedor) {
        return proveedorRepository.existsByNroProveedor(nroProveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCuit(String cuit) {
        return proveedorRepository.existsByCuit(cuit);
    }

    private String generarNroProveedor() {
        long count = proveedorRepository.count() + 1;
        return String.format("PROV%06d", count);
    }
}