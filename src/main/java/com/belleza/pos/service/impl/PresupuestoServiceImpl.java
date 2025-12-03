package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    private final PresupuestoMapper presupuestoMapper;

    private static final int DIAS_VALIDEZ_DEFAULT = 30;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public PresupuestoResponse create(CreatePresupuestoRequest request) {
        log.info("Creando presupuesto: {}", request.nroPresupuesto());

        // Validar que no exista el número de presupuesto
        if (presupuestoRepository.existsByNroPresupuesto(request.nroPresupuesto())) {
            throw new BusinessException("Ya existe un presupuesto con el número: " +
                    request.nroPresupuesto());
        }

        // Validar cliente
        Cliente cliente = clienteRepository.findById(request.idCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));

        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        // Validar sucursal
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        // Validar lista de precios
        ListaPrecio listaPrecio = listaPrecioRepository.findById(request.idListaPrecio())
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id",
                        request.idListaPrecio()));

        // Crear presupuesto
        Presupuesto presupuesto = presupuestoMapper.toEntity(request, cliente, usuario, sucursal, listaPrecio);

        // Agregar detalles
        for (PresupuestoDetalleRequest detalleReq : request.detalles()) {
            Articulo articulo = articuloRepository.findById(detalleReq.idArticulo())
                    .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id",
                            detalleReq.idArticulo()));

            PresupuestoDetalle detalle = presupuestoMapper.toDetalleEntity(
                    detalleReq, presupuesto, articulo);
            presupuesto.getDetalles().add(detalle);
        }

        // Calcular totales
        presupuestoMapper.calcularTotales(presupuesto);

        // Guardar
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

        // No se puede modificar un presupuesto convertido o rechazado
        if (EstadoPresupuesto.CONVERTIDO_VENTA.name().equals(presupuesto.getEstado()) ||
                EstadoPresupuesto.RECHAZADO.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("No se puede modificar un presupuesto " +
                    presupuesto.getEstado().toLowerCase());
        }

        // Validar número de presupuesto si se está cambiando
        if (request.nroPresupuesto() != null &&
                !request.nroPresupuesto().equals(presupuesto.getNroPresupuesto())) {
            if (presupuestoRepository.existsByNroPresupuesto(request.nroPresupuesto())) {
                throw new BusinessException("Ya existe un presupuesto con el número: " +
                        request.nroPresupuesto());
            }
        }

        // Validar cliente si se está cambiando
        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));
        }

        // Validar lista de precios si se está cambiando
        ListaPrecio listaPrecio = null;
        if (request.idListaPrecio() != null) {
            listaPrecio = listaPrecioRepository.findById(request.idListaPrecio())
                    .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id",
                            request.idListaPrecio()));
        }

        // Actualizar presupuesto
        presupuestoMapper.updateEntity(presupuesto, request, cliente, listaPrecio);
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
    public List<PresupuestoSimpleResponse> getAllPendientes() {
        log.debug("Obteniendo todos los presupuestos pendientes");
        return presupuestoRepository.findByEstado(EstadoPresupuesto.PENDIENTE.name()).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando presupuestos con término: {}", searchTerm);
        return presupuestoRepository.search(searchTerm, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByCliente(Integer idCliente, Pageable pageable) {
        log.debug("Obteniendo presupuestos por cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return presupuestoRepository.findByCliente_IdCliente(idCliente, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getBySucursal(Integer idSucursal, Pageable pageable) {
        log.debug("Obteniendo presupuestos por sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return presupuestoRepository.findBySucursal_IdSucursal(idSucursal, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByUsuario(Integer idUsuario, Pageable pageable) {
        log.debug("Obteniendo presupuestos por usuario: {}", idUsuario);

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }

        return presupuestoRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PresupuestoResponse> getByEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo presupuestos por estado: {}", estado);
        return presupuestoRepository.findByEstado(estado, pageable)
                .map(presupuestoMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> getByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo presupuestos entre fechas: {} - {}", fechaInicio, fechaFin);
        return presupuestoRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(presupuestoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando presupuesto permanentemente: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Solo se puede eliminar si está rechazado
        if (!EstadoPresupuesto.RECHAZADO.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("Solo se pueden eliminar presupuestos rechazados");
        }

        presupuestoRepository.deleteById(id);
        log.info("Presupuesto eliminado permanentemente: {}", id);
    }

    // ========== Gestión de Estados ==========

    @Override
    @Transactional
    public PresupuestoResponse aprobar(Integer id, AprobarPresupuestoRequest request) {
        log.info("Aprobando presupuesto: {}", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Solo se puede aprobar si está pendiente
        if (!EstadoPresupuesto.PENDIENTE.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("Solo se pueden aprobar presupuestos pendientes");
        }

        // Validar usuario
        if (!usuarioRepository.existsById(request.idUsuario())) {
            throw new ResourceNotFoundException("Usuario", "id", request.idUsuario());
        }

        // Aprobar
        presupuesto.setEstado(EstadoPresupuesto.APROBADO.name());

        if (request.observaciones() != null) {
            String obs = presupuesto.getObservaciones() != null ?
                    presupuesto.getObservaciones() + " | " : "";
            presupuesto.setObservaciones(obs + "APROBADO: " + request.observaciones());
        }

        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto aprobado exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional
    public PresupuestoResponse rechazar(Integer id, RechazarPresupuestoRequest request) {
        log.info("Rechazando presupuesto: {} - Motivo: {}", id, request.motivo());

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Solo se puede rechazar si está pendiente o aprobado
        if (EstadoPresupuesto.CONVERTIDO_VENTA.name().equals(presupuesto.getEstado()) ||
                EstadoPresupuesto.RECHAZADO.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("No se puede rechazar un presupuesto " +
                    presupuesto.getEstado().toLowerCase());
        }

        // Validar usuario
        if (!usuarioRepository.existsById(request.idUsuario())) {
            throw new ResourceNotFoundException("Usuario", "id", request.idUsuario());
        }

        // Rechazar
        presupuesto.setEstado(EstadoPresupuesto.RECHAZADO.name());

        String observacionRechazo = "RECHAZADO - Motivo: " + request.motivo();
        if (presupuesto.getObservaciones() != null) {
            presupuesto.setObservaciones(presupuesto.getObservaciones() + " | " + observacionRechazo);
        } else {
            presupuesto.setObservaciones(observacionRechazo);
        }

        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto rechazado exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

    @Override
    @Transactional
    public PresupuestoResponse convertirEnVenta(Integer id, ConvertirPresupuestoVentaRequest request) {
        log.info("Convirtiendo presupuesto {} en venta", id);

        Presupuesto presupuesto = presupuestoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Presupuesto", "id", id));

        // Solo se puede convertir si está pendiente o aprobado
        if (EstadoPresupuesto.CONVERTIDO_VENTA.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("El presupuesto ya fue convertido en venta");
        }
        if (EstadoPresupuesto.RECHAZADO.name().equals(presupuesto.getEstado())) {
            throw new BusinessException("No se puede convertir un presupuesto rechazado");
        }

        // Validar usuario
        if (!usuarioRepository.existsById(request.idUsuario())) {
            throw new ResourceNotFoundException("Usuario", "id", request.idUsuario());
        }

        // Marcar como convertido
        // NOTA: Cuando se implemente el módulo de Ventas, aquí se creará la venta real
        // y se guardará el ID en idVentaGenerada
        presupuesto.setEstado(EstadoPresupuesto.CONVERTIDO_VENTA.name());

        String obs = "CONVERTIDO A VENTA";
        if (request.observaciones() != null) {
            obs += " - " + request.observaciones();
        }

        if (presupuesto.getObservaciones() != null) {
            presupuesto.setObservaciones(presupuesto.getObservaciones() + " | " + obs);
        } else {
            presupuesto.setObservaciones(obs);
        }

        // TODO: Cuando se implemente Ventas, crear la venta aquí
        // Integer idVenta = ventaService.crearDesdePresupuesto(presupuesto);
        // presupuesto.setIdVentaGenerada(idVenta);

        presupuesto = presupuestoRepository.save(presupuesto);

        log.info("Presupuesto convertido exitosamente: {}", id);
        return presupuestoMapper.toResponse(presupuesto);
    }

// ========== Gestión de Detalles (Continuación) ==========

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoDetalleResponse> getDetalles(Integer idPresupuesto) {
        log.debug("Obteniendo detalles de presupuesto: {}", idPresupuesto);

        if (!presupuestoRepository.existsById(idPresupuesto)) {
            throw new ResourceNotFoundException("Presupuesto", "id", idPresupuesto);
        }

        return presupuestoDetalleRepository.findByPresupuesto_IdPresupuesto(idPresupuesto).stream()
                .map(presupuestoMapper::toDetalleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas y Reportes ==========

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> getByClienteAndFechaBetween(
            Integer idCliente,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo presupuestos por cliente y fechas - Cliente: {} - Período: {} a {}",
                idCliente, fechaInicio, fechaFin);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return presupuestoRepository.findByClienteAndFechaBetween(idCliente, fechaInicio, fechaFin).stream()
                .map(presupuestoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoResponse> getBySucursalAndFechaBetween(
            Integer idSucursal,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo presupuestos por sucursal y fechas - Sucursal: {} - Período: {} a {}",
                idSucursal, fechaInicio, fechaFin);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return presupuestoRepository.findBySucursalAndFechaBetween(idSucursal, fechaInicio, fechaFin).stream()
                .map(presupuestoMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosVencidos(Integer diasValidez) {
        log.debug("Obteniendo presupuestos vencidos (más de {} días)", diasValidez);

        LocalDate fechaLimite = LocalDate.now().minusDays(diasValidez);

        return presupuestoRepository.findPresupuestosVencidos(fechaLimite).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosVigentes() {
        log.debug("Obteniendo presupuestos vigentes");

        LocalDate fechaLimite = LocalDate.now().minusDays(DIAS_VALIDEZ_DEFAULT);

        return presupuestoRepository.findByEstado(EstadoPresupuesto.PENDIENTE.name()).stream()
                .filter(p -> p.getFechaPresupuesto().isAfter(fechaLimite))
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByEstado(String estado) {
        log.debug("Contando presupuestos por estado: {}", estado);
        return presupuestoRepository.countByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorEstadoYPeriodo(
            String estado,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Calculando total de presupuestos - Estado: {} - Período: {} a {}",
                estado, fechaInicio, fechaFin);
        return presupuestoRepository.calcularTotalPorEstadoYPeriodo(estado, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasPresupuestoResponse getEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo estadísticas de presupuestos - Período: {} a {}",
                fechaInicio, fechaFin);

        List<Presupuesto> presupuestos = presupuestoRepository.findByFechaBetween(fechaInicio, fechaFin);

        int cantidadTotal = presupuestos.size();

        int cantidadPendientes = (int) presupuestos.stream()
                .filter(p -> EstadoPresupuesto.PENDIENTE.name().equals(p.getEstado()))
                .count();

        int cantidadAprobados = (int) presupuestos.stream()
                .filter(p -> EstadoPresupuesto.APROBADO.name().equals(p.getEstado()))
                .count();

        int cantidadRechazados = (int) presupuestos.stream()
                .filter(p -> EstadoPresupuesto.RECHAZADO.name().equals(p.getEstado()))
                .count();

        int cantidadConvertidos = (int) presupuestos.stream()
                .filter(p -> EstadoPresupuesto.CONVERTIDO_VENTA.name().equals(p.getEstado()))
                .count();

        BigDecimal totalPendientes = presupuestos.stream()
                .filter(p -> EstadoPresupuesto.PENDIENTE.name().equals(p.getEstado()))
                .map(Presupuesto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAprobados = presupuestos.stream()
                .filter(p -> EstadoPresupuesto.APROBADO.name().equals(p.getEstado()))
                .map(Presupuesto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRechazados = presupuestos.stream()
                .filter(p -> EstadoPresupuesto.RECHAZADO.name().equals(p.getEstado()))
                .map(Presupuesto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalConvertidos = presupuestos.stream()
                .filter(p -> EstadoPresupuesto.CONVERTIDO_VENTA.name().equals(p.getEstado()))
                .map(Presupuesto::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalGeneral = totalPendientes.add(totalAprobados)
                .add(totalRechazados).add(totalConvertidos);

        // Calcular tasa de conversión
        BigDecimal tasaConversion = BigDecimal.ZERO;
        if (cantidadTotal > 0) {
            tasaConversion = BigDecimal.valueOf(cantidadConvertidos)
                    .divide(BigDecimal.valueOf(cantidadTotal), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return EstadisticasPresupuestoResponse.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .cantidadTotal(cantidadTotal)
                .cantidadPendientes(cantidadPendientes)
                .cantidadAprobados(cantidadAprobados)
                .cantidadRechazados(cantidadRechazados)
                .cantidadConvertidos(cantidadConvertidos)
                .totalPendientes(totalPendientes)
                .totalAprobados(totalAprobados)
                .totalRechazados(totalRechazados)
                .totalConvertidos(totalConvertidos)
                .totalGeneral(totalGeneral)
                .tasaConversion(tasaConversion)
                .build();
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroPresupuesto(String nroPresupuesto) {
        return presupuestoRepository.existsByNroPresupuesto(nroPresupuesto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosDelDia() {
        log.debug("Obteniendo presupuestos del día");
        LocalDate hoy = LocalDate.now();
        return presupuestoRepository.findByFechaBetween(hoy, hoy).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PresupuestoSimpleResponse> getPresupuestosDelMes() {
        log.debug("Obteniendo presupuestos del mes");
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        return presupuestoRepository.findByFechaBetween(inicioMes, finMes).stream()
                .map(presupuestoMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public String generarNroPresupuesto() {
        log.debug("Generando número de presupuesto automático");

        // Formato: PRES-YYYYMMDD-NNNN
        LocalDate hoy = LocalDate.now();
        String fecha = hoy.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Buscar el último presupuesto del día
        String prefijo = "PRES-" + fecha + "-";

        List<Presupuesto> presupuestosDelDia = presupuestoRepository.findByFechaBetween(hoy, hoy);

        int ultimoNumero = presupuestosDelDia.stream()
                .map(Presupuesto::getNroPresupuesto)
                .filter(nro -> nro != null && nro.startsWith(prefijo))
                .map(nro -> nro.substring(prefijo.length()))
                .filter(nro -> nro.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        int nuevoNumero = ultimoNumero + 1;
        String nroPresupuesto = prefijo + String.format("%04d", nuevoNumero);

        log.debug("Número de presupuesto generado: {}", nroPresupuesto);
        return nroPresupuesto;
    }
}