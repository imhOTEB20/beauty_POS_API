package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.AnularNotaCreditoRequest;
import com.belleza.pos.dto.request.CreateNotaCreditoRequest;
import com.belleza.pos.dto.request.NotaCreditoDetalleRequest;
import com.belleza.pos.dto.request.UpdateNotaCreditoRequest;
import com.belleza.pos.dto.response.EstadisticasNotaCreditoResponse;
import com.belleza.pos.dto.response.NotaCreditoDetalleResponse;
import com.belleza.pos.dto.response.NotaCreditoResponse;
import com.belleza.pos.dto.response.NotaCreditoSimpleResponse;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.EstadoNotaCredito;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.NotaCreditoMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.NotaCreditoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de NotaCredito
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotaCreditoServiceImpl implements NotaCreditoService {

    private final NotaCreditoRepository notaCreditoRepository;
    private final NotaCreditoDetalleRepository notaCreditoDetalleRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final ArticuloRepository articuloRepository;
    private final NotaCreditoMapper notaCreditoMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public NotaCreditoResponse create(CreateNotaCreditoRequest request) {
        log.info("Creando nota de crédito - Tipo: {}", request.tipoComprobante());

        // Validar número de comprobante si se proporciona
        if (request.nroComprobante() != null &&
                notaCreditoRepository.existsByNroComprobante(request.nroComprobante())) {
            throw new BusinessException("Ya existe una nota de crédito con el número: " +
                    request.nroComprobante());
        }

        // Validar cliente (opcional)
        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));
        }

        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        // Crear nota de crédito
        NotaCredito notaCredito = notaCreditoMapper.toEntity(request, cliente, usuario, sucursal);

        // Agregar detalles
        for (NotaCreditoDetalleRequest detalleReq : request.detalles()) {
            Articulo articulo = null;
            if (detalleReq.idArticulo() != null) {
                articulo = articuloRepository.findById(detalleReq.idArticulo())
                        .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id",
                                detalleReq.idArticulo()));
            }

            NotaCreditoDetalle detalle = notaCreditoMapper.toDetalleEntity(
                    detalleReq, notaCredito, articulo);
            notaCredito.getDetalles().add(detalle);
        }

        // Guardar
        notaCredito = notaCreditoRepository.save(notaCredito);

        // Si tiene cliente con cuenta corriente, registrar movimiento
        if (cliente != null && cliente.getCuentaCorrienteHabilitada()) {
            // Aquí se registraría el movimiento en cuenta corriente
            // Lo implementaremos cuando hagamos CuentaCorrienteClientes
            log.info("Cliente con cuenta corriente - Se debe registrar crédito por: {}",
                    notaCredito.getTotal());
        }

        log.info("Nota de crédito creada exitosamente con ID: {}", notaCredito.getIdNotaCredito());
        return notaCreditoMapper.toResponse(notaCredito);
    }

    @Override
    @Transactional
    public NotaCreditoResponse update(Integer id, UpdateNotaCreditoRequest request) {
        log.info("Actualizando nota de crédito con ID: {}", id);

        NotaCredito notaCredito = notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de crédito", "id", id));

        // No se puede modificar una nota de crédito anulada
        if (EstadoNotaCredito.ANULADA.name().equals(notaCredito.getEstado())) {
            throw new BusinessException("No se puede modificar una nota de crédito anulada");
        }

        // Validar número de comprobante si se está cambiando
        if (request.nroComprobante() != null &&
                !request.nroComprobante().equals(notaCredito.getNroComprobante())) {
            if (notaCreditoRepository.existsByNroComprobante(request.nroComprobante())) {
                throw new BusinessException("Ya existe una nota de crédito con el número: " +
                        request.nroComprobante());
            }
        }

        // Validar cliente si se está cambiando
        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));
        }

        // Actualizar nota de crédito
        notaCreditoMapper.updateEntity(notaCredito, request, cliente);
        notaCredito = notaCreditoRepository.save(notaCredito);

        log.info("Nota de crédito actualizada exitosamente: {}", id);
        return notaCreditoMapper.toResponse(notaCredito);
    }

    @Override
    @Transactional(readOnly = true)
    public NotaCreditoResponse getById(Integer id) {
        log.debug("Obteniendo nota de crédito por ID: {}", id);
        NotaCredito notaCredito = notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de crédito", "id", id));
        return notaCreditoMapper.toResponse(notaCredito);
    }

    @Override
    @Transactional(readOnly = true)
    public NotaCreditoResponse getByNroComprobante(String nroComprobante) {
        log.debug("Obteniendo nota de crédito por número: {}", nroComprobante);
        NotaCredito notaCredito = notaCreditoRepository.findByNroComprobante(nroComprobante)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de crédito", "número", nroComprobante));
        return notaCreditoMapper.toResponse(notaCredito);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotaCreditoResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las notas de crédito con paginación");
        return notaCreditoRepository.findAll(pageable)
                .map(notaCreditoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoSimpleResponse> getAllActive() {
        log.debug("Obteniendo todas las notas de crédito activas");
        return notaCreditoRepository.findByEstado(EstadoNotaCredito.ACTIVA.name()).stream()
                .map(notaCreditoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotaCreditoResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando notas de crédito con término: {}", searchTerm);
        return notaCreditoRepository.search(searchTerm, pageable)
                .map(notaCreditoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotaCreditoResponse> getByCliente(Integer idCliente, Pageable pageable) {
        log.debug("Obteniendo notas de crédito por cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return notaCreditoRepository.findByCliente_IdCliente(idCliente, pageable)
                .map(notaCreditoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotaCreditoResponse> getBySucursal(Integer idSucursal, Pageable pageable) {
        log.debug("Obteniendo notas de crédito por sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return notaCreditoRepository.findBySucursal_IdSucursal(idSucursal, pageable)
                .map(notaCreditoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotaCreditoResponse> getByEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo notas de crédito por estado: {}", estado);
        return notaCreditoRepository.findByEstado(estado, pageable)
                .map(notaCreditoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoResponse> getByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo notas de crédito entre fechas: {} - {}", fechaInicio, fechaFin);
        return notaCreditoRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(notaCreditoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NotaCreditoResponse anular(Integer id, AnularNotaCreditoRequest request) {
        log.info("Anulando nota de crédito: {} - Motivo: {}", id, request.motivo());

        NotaCredito notaCredito = notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de crédito", "id", id));

        // Validar que no esté ya anulada
        if (EstadoNotaCredito.ANULADA.name().equals(notaCredito.getEstado())) {
            throw new BusinessException("La nota de crédito ya está anulada");
        }

        // Validar usuario
        if (!usuarioRepository.existsById(request.idUsuario())) {
            throw new ResourceNotFoundException("Usuario", "id", request.idUsuario());
        }

        // Anular
        notaCredito.setEstado(EstadoNotaCredito.ANULADA.name());

        String observacionAnulacion = "ANULADA - Motivo: " + request.motivo();
        if (notaCredito.getObservaciones() != null) {
            notaCredito.setObservaciones(notaCredito.getObservaciones() + " | " + observacionAnulacion);
        } else {
            notaCredito.setObservaciones(observacionAnulacion);
        }

        notaCredito = notaCreditoRepository.save(notaCredito);

        // Si tiene cliente con cuenta corriente, reversar movimiento
        if (notaCredito.getCliente() != null &&
                notaCredito.getCliente().getCuentaCorrienteHabilitada()) {
            log.info("Cliente con cuenta corriente - Se debe reversar crédito por: {}",
                    notaCredito.getTotal());
        }

        log.info("Nota de crédito anulada exitosamente: {}", id);
        return notaCreditoMapper.toResponse(notaCredito);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando nota de crédito permanentemente: {}", id);

        NotaCredito notaCredito = notaCreditoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de crédito", "id", id));

        // Solo se puede eliminar si está anulada
        if (!EstadoNotaCredito.ANULADA.name().equals(notaCredito.getEstado())) {
            throw new BusinessException("Solo se pueden eliminar notas de crédito anuladas");
        }

        notaCreditoRepository.deleteById(id);
        log.info("Nota de crédito eliminada permanentemente: {}", id);
    }

    // ========== Gestión de Detalles ==========

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoDetalleResponse> getDetalles(Integer idNotaCredito) {
        log.debug("Obteniendo detalles de nota de crédito: {}", idNotaCredito);

        if (!notaCreditoRepository.existsById(idNotaCredito)) {
            throw new ResourceNotFoundException("Nota de crédito", "id", idNotaCredito);
        }

        return notaCreditoDetalleRepository.findByNotaCredito_IdNotaCredito(idNotaCredito).stream()
                .map(notaCreditoMapper::toDetalleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas y Reportes ==========

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoResponse> getByClienteAndFechaBetween(
            Integer idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo notas de crédito por cliente y fechas - Cliente: {} - Período: {} a {}",
                idCliente, fechaInicio, fechaFin);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return notaCreditoRepository.findByClienteAndFechaBetween(idCliente, fechaInicio, fechaFin).stream()
                .map(notaCreditoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoResponse> getBySucursalAndFechaBetween(
            Integer idSucursal,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo notas de crédito por sucursal y fechas - Sucursal: {} - Período: {} a {}",
                idSucursal, fechaInicio, fechaFin);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return notaCreditoRepository.findBySucursalAndFechaBetween(idSucursal, fechaInicio, fechaFin).stream()
                .map(notaCreditoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoResponse> getByTipoComprobante(String tipoComprobante) {
        log.debug("Obteniendo notas de crédito por tipo: {}", tipoComprobante);
        return notaCreditoRepository.findByTipoComprobante(tipoComprobante).stream()
                .map(notaCreditoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Calculando total de notas de crédito entre: {} - {}", fechaInicio, fechaFin);
        return notaCreditoRepository.calcularTotalPeriodo(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorCliente(Integer idCliente) {
        log.debug("Calculando total de notas de crédito por cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return notaCreditoRepository.calcularTotalPorCliente(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasNotaCreditoResponse getEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo estadísticas de notas de crédito - Período: {} a {}",
                fechaInicio, fechaFin);

        List<NotaCredito> notas = notaCreditoRepository.findByFechaBetween(fechaInicio, fechaFin);

        int cantidadTotal = notas.size();
        int cantidadActivas = (int) notas.stream()
                .filter(nc -> EstadoNotaCredito.ACTIVA.name().equals(nc.getEstado()))
                .count();
        int cantidadAnuladas = (int) notas.stream()
                .filter(nc -> EstadoNotaCredito.ANULADA.name().equals(nc.getEstado()))
                .count();

        BigDecimal totalActivas = notas.stream()
                .filter(nc -> EstadoNotaCredito.ACTIVA.name().equals(nc.getEstado()))
                .map(NotaCredito::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAnuladas = notas.stream()
                .filter(nc -> EstadoNotaCredito.ANULADA.name().equals(nc.getEstado()))
                .map(NotaCredito::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeneral = totalActivas.add(totalAnuladas);

        return EstadisticasNotaCreditoResponse.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .cantidadTotal(cantidadTotal)
                .cantidadActivas(cantidadActivas)
                .cantidadAnuladas(cantidadAnuladas)
                .totalActivas(totalActivas)
                .totalAnuladas(totalAnuladas)
                .totalGeneral(totalGeneral)
                .build();
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroComprobante(String nroComprobante) {
        return notaCreditoRepository.existsByNroComprobante(nroComprobante);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoSimpleResponse> getNotasDelDia() {
        log.debug("Obteniendo notas de crédito del día");
        LocalDate hoy = LocalDate.now();
        return notaCreditoRepository.findByFechaBetween(hoy, hoy).stream()
                .map(notaCreditoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaCreditoSimpleResponse> getNotasDelMes() {
        log.debug("Obteniendo notas de crédito del mes");
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        return notaCreditoRepository.findByFechaBetween(inicioMes, finMes).stream()
                .map(notaCreditoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }
}