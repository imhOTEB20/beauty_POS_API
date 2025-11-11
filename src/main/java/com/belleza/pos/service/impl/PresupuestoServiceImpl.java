package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreatePresupuestoRequest;
import com.belleza.pos.dto.request.PresupuestoDetalleRequest;
import com.belleza.pos.dto.request.UpdatePresupuestoRequest;
import com.belleza.pos.dto.response.PresupuestoResponse;
import com.belleza.pos.dto.response.PresupuestoSimpleResponse;
import com.belleza.pos.dto.response.VentaResponse;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.EstadoPresupuesto;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.PresupuestoMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.PresupuestoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Presupuesto
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PresupuestoServiceImpl implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;
    private final PresupuestoDetalleRepository presupuestoDetalleRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final ArticuloRepository articuloRepository;
    private final VentaRepository ventaRepository;
    private final PresupuestoMapper presupuestoMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public PresupuestoResponse create(CreatePresupuestoRequest request) {
        log.info("Creando presupuesto con número: {}", request.nroPresupuesto());

        // Validar número de presupuesto único
        if (presupuestoRepository.existsByNroPresupuesto(request.nroPresupuesto())) {
            throw new BusinessException("Ya existe un presupuesto con el número: " + request.nroPresupuesto());
        }

        // Validar estado
        EstadoPresupuesto estado;
        try {
            estado = EstadoPresupuesto.valueOf(request.estado());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + request.estado());
        }

        // Obtener entidades relacionadas
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        ListaPrecio listaPrecio = listaPrecioRepository.findById(request.idListaPrecio())
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", request.idListaPrecio()));

        // Crear presupuesto
        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setNroPresupuesto(request.nroPresupuesto());
        presupuesto.setCliente(cliente);
        presupuesto.setUsuario(usuario);
        presupuesto.setSucursal(sucursal);
        presupuesto.setFechaPresupuesto(request.fechaPresupuesto());
        presupuesto.setListaPrecio(listaPrecio);
        presupuesto.setEstado(estado);
        presupuesto.setObservaciones(request.observaciones());

        // Procesar detalles y calcular totales
        AtomicInteger numeroLinea = new AtomicInteger(1);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalIva21 = BigDecimal.ZERO;
        BigDecimal totalIva105 = BigDecimal.ZERO;

        for (PresupuestoDetalleRequest detalleReq : request.detalles()) {
            PresupuestoDetalle detalle = procesarDetalle(presupuesto, detalleReq, numeroLinea.getAndIncrement());
            presupuesto.addDetalle(detalle);

            subtotal = subtotal.add(detalle.getTotalSinImpuestos());

            // Calcular IVA por tipo
            BigDecimal montoIva = detalle.getTotalSinImpuestos()
                    .multiply(detalle.getPorcentajeIva())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            if (detalle.getPorcentajeIva().compareTo(BigDecimal.valueOf(21)) == 0) {
                totalIva21 = totalIva21.add(montoIva);
            } else if (detalle.getPorcentajeIva().compareTo(BigDecimal.valueOf(10.5)) == 0) {
                totalIva105 = totalIva105.add(montoIva);
            }
        }

        // Establecer totales
        presupuesto.setSubtotal(subtotal);
        presupuesto.setIva21(totalIva21);
        presupuesto.setIva105(totalIva105);

        BigDecimal total = subtotal.add(totalIva21).add(totalIva105);
        presupuesto.setTotal(total);

        // Guardar presupuesto
        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto creado exitosamente con ID: {}", presupuesto.getIdPresupuesto());
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional
    public PresupuestoResponse update(Integer id, UpdatePresupuestoRequest request) {
        log.info("Actualizando presupuesto con ID: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Validar que no esté convertido a venta
        if (presupuesto.getEstado() == EstadoPresupuesto.CONVERTIDO_VENTA) {
            throw new BusinessException("No se puede actualizar un presupuesto convertido a venta");
        }

        // Actualizar campos permitidos
        if (request.fechaPresupuesto() != null) {
            presupuesto.setFechaPresupuesto(request.fechaPresupuesto());
        }
        if (request.estado() != null) {
            EstadoPresupuesto nuevoEstado;
            try {
                nuevoEstado = EstadoPresupuesto.valueOf(request.estado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado inválido: " + request.estado());
            }
            presupuesto.setEstado(nuevoEstado);
        }
        if (request.observaciones() != null) {
            presupuesto.setObservaciones(request.observaciones());
        }

        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto actualizado exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public PresupuestoResponse getById(Integer id) {
        log.debug("Obteniendo presupuesto por ID: {}", id);
        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public PresupuestoResponse getByNroPresupuesto(String nroPresupuesto) {
        log.debug("Obteniendo presupuesto por número: {}", nroPresupuesto);
        Presupuesto presupuesto = presupuestoRepository.findByNroPresupuesto(nroPresupuesto)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "número", nroPresupuesto));
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los presupuestos con paginación");
        return presupuestoRepository.findAll(pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando presupuestos con término: {}", searchTerm);
        return presupuestoRepository.search(searchTerm, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.warn("Eliminando presupuesto permanentemente: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // No permitir eliminar si ya fue convertido a venta
        if (presupuesto.getEstado() == EstadoPresupuesto.CONVERTIDO_VENTA) {
            throw new BusinessException("No se puede eliminar un presupuesto convertido a venta");
        }

        // Eliminar detalles
        presupuestoDetalleRepository.deleteByPresupuesto_IdPresupuesto(id);

        presupuestoRepository.deleteById(id);
        log.info("Presupuesto eliminado permanentemente: {}", id);
    }

    // ========== Gestión de Estado ==========

    @Override
    @Transactional
    public PresupuestoResponse aprobar(Integer id) {
        log.info("Aprobando presupuesto: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        if (presupuesto.getEstado() != EstadoPresupuesto.PENDIENTE) {
            throw new BusinessException("Solo se pueden aprobar presupuestos pendientes");
        }

        presupuesto.setEstado(EstadoPresupuesto.APROBADO);
        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto aprobado exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional
    public PresupuestoResponse rechazar(Integer id, String motivo) {
        log.info("Rechazando presupuesto: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        if (presupuesto.getEstado() != EstadoPresupuesto.PENDIENTE) {
            throw new BusinessException("Solo se pueden rechazar presupuestos pendientes");
        }

        presupuesto.setEstado(EstadoPresupuesto.RECHAZADO);
        presupuesto.setObservaciones((presupuesto.getObservaciones() != null ? presupuesto.getObservaciones() + "\n" : "")
                + "RECHAZADO: " + motivo);
        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto rechazado exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional
    public VentaResponse convertirAVenta(Integer id) {
        log.info("Convirtiendo presupuesto {} a venta", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Validar estado
        if (presupuesto.getEstado() == EstadoPresupuesto.CONVERTIDO_VENTA) {
            throw new BusinessException("El presupuesto ya fue convertido a venta");
        }
        if (presupuesto.getEstado() == EstadoPresupuesto.RECHAZADO) {
            throw new BusinessException("No se puede convertir un presupuesto rechazado");
        }

        // TODO: Crear la venta basada en el presupuesto
        // Por ahora solo marcamos el presupuesto como convertido
        // En una implementación completa, aquí se crearía la venta usando VentaService

        presupuesto.setEstado(EstadoPresupuesto.CONVERTIDO_VENTA);
        presupuestoRepository.save(presupuesto);

        log.info("Presupuesto convertido exitosamente (pendiente crear venta real)");

        // Por ahora retornamos null - en implementación completa retornaría la VentaResponse creada
        throw new BusinessException("Función de conversión a venta pendiente de implementación completa");
    }

    // ========== Consultas por Estado ==========

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo presupuestos por estado: {}", estado);

        EstadoPresupuesto estadoPresupuesto;
        try {
            estadoPresupuesto = EstadoPresupuesto.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + estado);
        }

        return presupuestoRepository.findByEstado(estadoPresupuesto, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosPendientes() {
        log.debug("Obteniendo presupuestos pendientes");
        return presupuestoRepository.findByEstado(EstadoPresupuesto.PENDIENTE).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosAprobados() {
        log.debug("Obteniendo presupuestos aprobados");
        return presupuestoRepository.findByEstado(EstadoPresupuesto.APROBADO).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosRechazados() {
        log.debug("Obteniendo presupuestos rechazados");
        return presupuestoRepository.findByEstado(EstadoPresupuesto.RECHAZADO).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosConvertidos() {
        log.debug("Obteniendo presupuestos convertidos");
        return presupuestoRepository.findByEstado(EstadoPresupuesto.CONVERTIDO_VENTA).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Cliente ==========

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByCliente(Integer idCliente, Pageable pageable) {
        log.debug("Obteniendo presupuestos del cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return presupuestoRepository.findByCliente_IdCliente(idCliente, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getHistorialCliente(Integer idCliente) {
        log.debug("Obteniendo historial de presupuestos del cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return presupuestoRepository.findByCliente_IdCliente(idCliente).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Sucursal ==========

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getBySucursal(Integer idSucursal, Pageable pageable) {
        log.debug("Obteniendo presupuestos de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return presupuestoRepository.findBySucursal_IdSucursal(idSucursal, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosDelDiaBySucursal(Integer idSucursal) {
        log.debug("Obteniendo presupuestos del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return presupuestoRepository.findPresupuestosDelDiaBySucursal(idSucursal).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Usuario ==========

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByUsuario(Integer idUsuario, Pageable pageable) {
        log.debug("Obteniendo presupuestos del usuario: {}", idUsuario);

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }

        return presupuestoRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(presupuestoMapper::toResponse);
    }

    // ========== Consultas por Fecha ==========

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosDelDia() {
        log.debug("Obteniendo presupuestos del día");
        return presupuestoRepository.findPresupuestosDelDia().stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByFechaPresupuestoBetween(LocalDate fechaInicio,
                                                                  LocalDate fechaFin,
                                                                  Pageable pageable) {
        log.debug("Obteniendo presupuestos entre {} y {}", fechaInicio, fechaFin);
        return presupuestoRepository.findByFechaPresupuestoBetween(fechaInicio, fechaFin, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosByPeriodo(LocalDate fechaInicio,
                                                                    LocalDate fechaFin) {
        log.debug("Obteniendo presupuestos del período {} - {}", fechaInicio, fechaFin);
        return presupuestoRepository.findByFechaPresupuestoBetween(fechaInicio, fechaFin).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Estadísticas ==========

    @Override
    @Transactional(readOnly = true)
    public Long countPresupuestosDelDia() {
        log.debug("Contando presupuestos del día");
        return presupuestoRepository.countPresupuestosDelDia();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPresupuestosDelDiaBySucursal(Integer idSucursal) {
        log.debug("Contando presupuestos del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return presupuestoRepository.countPresupuestosDelDiaBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPresupuestosPendientes() {
        log.debug("Contando presupuestos pendientes");
        return presupuestoRepository.countPresupuestosPendientes();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countPresupuestosConvertidos() {
        log.debug("Contando presupuestos convertidos");
        return presupuestoRepository.countPresupuestosConvertidos();
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroPresupuesto(String nroPresupuesto) {
        return presupuestoRepository.existsByNroPresupuesto(nroPresupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public String generarNroPresupuesto() {
        String prefix = "PRES-";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + timestamp;
    }

    // ========== Métodos Privados ==========

    private PresupuestoDetalle procesarDetalle(Presupuesto presupuesto, PresupuestoDetalleRequest request, int numeroLinea) {
        Articulo articulo = articuloRepository.findById(request.idArticulo())
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", request.idArticulo()));

        PresupuestoDetalle detalle = new PresupuestoDetalle();
        detalle.setPresupuesto(presupuesto);
        detalle.setNumeroLinea(numeroLinea);
        detalle.setArticulo(articulo);
        detalle.setCodigoBarras(articulo.getCodigoBarras());
        detalle.setDescripcion(articulo.getDescripcion());
        detalle.setCantidad(request.cantidad());
        detalle.setPrecioSinIva(request.precioSinIva());
        detalle.setPorcentajeIva(request.porcentajeIva());
        detalle.calcularTotales();

        return detalle;
    }
}