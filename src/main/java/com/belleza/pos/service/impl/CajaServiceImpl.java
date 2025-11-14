package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.TipoMovimientoCaja;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.CajaMapper;
import com.belleza.pos.mapper.MovimientoCajaMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.CajaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Caja
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaMapper cajaMapper;
    private final MovimientoCajaMapper movimientoCajaMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public CajaResponse create(CreateCajaRequest request) {
        log.info("Creando caja con número: {}", request.numeroCaja());

        // Validar que no exista el número de caja en la sucursal
        if (cajaRepository.existsByNumeroCajaAndSucursal(request.numeroCaja(), request.idSucursal())) {
            throw new BusinessException(
                    "Ya existe una caja con el número " + request.numeroCaja() +
                            " en esta sucursal");
        }

        // Obtener sucursal
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        // Crear caja
        Caja caja = cajaMapper.toEntity(request, sucursal);
        caja = cajaRepository.save(caja);

        log.info("Caja creada exitosamente con ID: {}", caja.getIdCaja());
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional
    public CajaResponse update(Integer id, UpdateCajaRequest request) {
        log.info("Actualizando caja con ID: {}", id);

        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", id));

        // Validar número de caja si se está cambiando
        if (request.numeroCaja() != null && !request.numeroCaja().equals(caja.getNumeroCaja())) {
            if (cajaRepository.existsByNumeroCajaAndSucursal(
                    request.numeroCaja(),
                    caja.getSucursal().getIdSucursal())) {
                throw new BusinessException(
                        "Ya existe una caja con el número " + request.numeroCaja() +
                                " en esta sucursal");
            }
        }

        // Obtener sucursal si se está cambiando
        Sucursal sucursal = null;
        if (request.idSucursal() != null) {
            sucursal = sucursalRepository.findById(request.idSucursal())
                    .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));
        }

        // Actualizar caja
        cajaMapper.updateEntity(caja, request, sucursal);
        caja = cajaRepository.save(caja);

        log.info("Caja actualizada exitosamente: {}", id);
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse getById(Integer id) {
        log.debug("Obteniendo caja por ID: {}", id);
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", id));
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(id);
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse getByNumeroCaja(String numeroCaja) {
        log.debug("Obteniendo caja por número: {}", numeroCaja);
        Caja caja = cajaRepository.findByNumeroCaja(numeroCaja)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "número", numeroCaja));
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CajaResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las cajas con paginación");
        return cajaRepository.findAll(pageable)
                .map(caja -> {
                    BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
                    return cajaMapper.toResponse(caja, saldo);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaSimpleResponse> getAllActive() {
        log.debug("Obteniendo todas las cajas activas");
        return cajaRepository.findByActivo(true).stream()
                .map(caja -> {
                    BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
                    return cajaMapper.toSimpleResponse(caja, saldo);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaSimpleResponse> getBySucursal(Integer idSucursal) {
        log.debug("Obteniendo cajas por sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return cajaRepository.findBySucursal_IdSucursal(idSucursal).stream()
                .map(caja -> {
                    BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
                    return cajaMapper.toSimpleResponse(caja, saldo);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CajaSimpleResponse> getActiveBySucursal(Integer idSucursal) {
        log.debug("Obteniendo cajas activas por sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return cajaRepository.findBySucursal_IdSucursalAndActivo(idSucursal, true).stream()
                .map(caja -> {
                    BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(caja.getIdCaja());
                    return cajaMapper.toSimpleResponse(caja, saldo);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CajaResponse activate(Integer id) {
        log.info("Activando caja: {}", id);
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", id));

        caja.setActivo(true);
        caja = cajaRepository.save(caja);

        log.info("Caja activada exitosamente: {}", id);
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(id);
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional
    public CajaResponse deactivate(Integer id) {
        log.info("Desactivando caja: {}", id);
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", id));

        // Verificar que la caja esté cerrada
        if (isCajaAbierta(id)) {
            throw new BusinessException("No se puede desactivar una caja abierta. Debe cerrarla primero.");
        }

        caja.setActivo(false);
        caja = cajaRepository.save(caja);

        log.info("Caja desactivada exitosamente: {}", id);
        BigDecimal saldo = movimientoCajaRepository.calcularSaldoActualCaja(id);
        return cajaMapper.toResponse(caja, saldo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando caja (soft delete): {}", id);
        Caja caja = cajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", id));

        // Verificar que la caja esté cerrada
        if (isCajaAbierta(id)) {
            throw new BusinessException("No se puede eliminar una caja abierta. Debe cerrarla primero.");
        }

        caja.setActivo(false);
        cajaRepository.save(caja);

        log.info("Caja eliminada exitosamente (soft delete): {}", id);
    }

    // ========== Gestión de Movimientos ==========

    @Override
    @Transactional
    public MovimientoCajaResponse createMovimiento(CreateMovimientoCajaRequest request) {
        log.info("Creando movimiento de caja tipo: {}", request.tipoMovimiento());

        // Validar caja
        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", request.idCaja()));

        if (!caja.getActivo()) {
            throw new BusinessException("La caja está inactiva");
        }

        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        // Obtener saldo anterior
        BigDecimal saldoAnterior = movimientoCajaRepository.calcularSaldoActualCaja(request.idCaja());

        // Crear movimiento
        MovimientoCaja movimiento = movimientoCajaMapper.toEntity(request, caja, usuario, saldoAnterior);
        movimiento = movimientoCajaRepository.save(movimiento);

        log.info("Movimiento de caja creado exitosamente con ID: {}", movimiento.getIdMovimiento());
        return movimientoCajaMapper.toResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCajaResponse getMovimientoById(Integer id) {
        log.debug("Obteniendo movimiento de caja por ID: {}", id);
        MovimientoCaja movimiento = movimientoCajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento de caja", "id", id));
        return movimientoCajaMapper.toResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getMovimientosByCaja(Integer idCaja) {
        log.debug("Obteniendo movimientos de la caja: {}", idCaja);

        if (!cajaRepository.existsById(idCaja)) {
            throw new ResourceNotFoundException("Caja", "id", idCaja);
        }

        return movimientoCajaRepository.findByCaja_IdCajaOrderByFechaHoraDesc(idCaja).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoCajaResponse> getMovimientosByCaja(Integer idCaja, Pageable pageable) {
        log.debug("Obteniendo movimientos de la caja con paginación: {}", idCaja);

        if (!cajaRepository.existsById(idCaja)) {
            throw new ResourceNotFoundException("Caja", "id", idCaja);
        }

        return movimientoCajaRepository.findByCaja_IdCaja(idCaja, pageable)
                .map(movimientoCajaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getMovimientosByCajaAndFechas(
            Integer idCaja,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo movimientos de caja entre fechas: {} - {}", fechaInicio, fechaFin);

        if (!cajaRepository.existsById(idCaja)) {
            throw new ResourceNotFoundException("Caja", "id", idCaja);
        }

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        return movimientoCajaRepository.findByCajaAndFechaHoraBetween(idCaja, inicio, fin).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCajaResponse getUltimoMovimiento(Integer idCaja) {
        log.debug("Obteniendo último movimiento de la caja: {}", idCaja);

        if (!cajaRepository.existsById(idCaja)) {
            throw new ResourceNotFoundException("Caja", "id", idCaja);
        }

        return movimientoCajaRepository.findUltimoMovimientoByCaja(idCaja)
                .map(movimientoCajaMapper::toResponse)
                .orElse(null);
    }

// ========== Operaciones de Caja (Continuación) ==========

    @Override
    @Transactional
    public MovimientoCajaResponse aperturaCaja(AperturaCajaRequest request) {
        log.info("Apertura de caja: {}", request.idCaja());

        // Validar que la caja no esté ya abierta
        if (isCajaAbierta(request.idCaja())) {
            throw new BusinessException("La caja ya está abierta");
        }

        CreateMovimientoCajaRequest movRequest = new CreateMovimientoCajaRequest(
                request.idCaja(),
                request.idUsuario(),
                TipoMovimientoCaja.APERTURA.name(),
                "Apertura de caja",
                request.montoInicial(),
                BigDecimal.ZERO,
                request.observaciones(),
                null,
                null
        );

        return createMovimiento(movRequest);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse cierreCaja(CierreCajaRequest request) {
        log.info("Cierre de caja: {}", request.idCaja());

        // Validar que la caja esté abierta
        if (!isCajaAbierta(request.idCaja())) {
            throw new BusinessException("La caja no está abierta");
        }

        // Obtener saldo actual
        BigDecimal saldoActual = movimientoCajaRepository.calcularSaldoActualCaja(request.idCaja());

        // Calcular diferencia
        BigDecimal diferencia = request.montoFinal().subtract(saldoActual);

        String concepto = "Cierre de caja";
        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
            concepto += " - Diferencia: " + diferencia;
        }

        CreateMovimientoCajaRequest movRequest = new CreateMovimientoCajaRequest(
                request.idCaja(),
                request.idUsuario(),
                TipoMovimientoCaja.CIERRE.name(),
                concepto,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                request.observaciones(),
                null,
                null
        );

        return createMovimiento(movRequest);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse retiroEfectivo(RetiroEfectivoRequest request) {
        log.info("Retiro de efectivo de caja: {} - Monto: {}", request.idCaja(), request.monto());

        // Validar que la caja esté abierta
        if (!isCajaAbierta(request.idCaja())) {
            throw new BusinessException("La caja no está abierta");
        }

        // Validar que haya suficiente saldo
        BigDecimal saldoActual = movimientoCajaRepository.calcularSaldoActualCaja(request.idCaja());
        if (saldoActual.compareTo(request.monto()) < 0) {
            throw new BusinessException("No hay suficiente saldo en caja para realizar el retiro");
        }

        CreateMovimientoCajaRequest movRequest = new CreateMovimientoCajaRequest(
                request.idCaja(),
                request.idUsuario(),
                TipoMovimientoCaja.RETIRO.name(),
                "Retiro: " + request.motivo(),
                BigDecimal.ZERO,
                request.monto(),
                request.observaciones(),
                null,
                null
        );

        return createMovimiento(movRequest);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse ingresoEfectivo(IngresoEfectivoRequest request) {
        log.info("Ingreso de efectivo a caja: {} - Monto: {}", request.idCaja(), request.monto());

        // Validar que la caja esté abierta
        if (!isCajaAbierta(request.idCaja())) {
            throw new BusinessException("La caja no está abierta");
        }

        CreateMovimientoCajaRequest movRequest = new CreateMovimientoCajaRequest(
                request.idCaja(),
                request.idUsuario(),
                TipoMovimientoCaja.INGRESO.name(),
                "Ingreso: " + request.motivo(),
                request.monto(),
                BigDecimal.ZERO,
                request.observaciones(),
                null,
                null
        );

        return createMovimiento(movRequest);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse registrarVentaEfectivo(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            BigDecimal monto) {

        log.info("Registrando venta en efectivo - Caja: {} - Venta: {} - Monto: {}",
                idCaja, idVenta, monto);

        CreateMovimientoCajaRequest request = new CreateMovimientoCajaRequest(
                idCaja,
                idUsuario,
                TipoMovimientoCaja.VENTA_EFECTIVO.name(),
                "Venta en efectivo #" + idVenta,
                monto,
                BigDecimal.ZERO,
                null,
                idVenta,
                null
        );

        return createMovimiento(request);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse registrarVentaTarjetaDebito(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            BigDecimal monto) {

        log.info("Registrando venta con tarjeta débito - Caja: {} - Venta: {} - Monto: {}",
                idCaja, idVenta, monto);

        CreateMovimientoCajaRequest request = new CreateMovimientoCajaRequest(
                idCaja,
                idUsuario,
                TipoMovimientoCaja.VENTA_TARJETA_DEBITO.name(),
                "Venta con tarjeta débito #" + idVenta,
                monto,
                BigDecimal.ZERO,
                null,
                idVenta,
                null
        );

        return createMovimiento(request);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse registrarVentaTarjetaCredito(
            Integer idCaja,
            Integer idUsuario,
            Integer idVenta,
            BigDecimal monto) {

        log.info("Registrando venta con tarjeta crédito - Caja: {} - Venta: {} - Monto: {}",
                idCaja, idVenta, monto);

        CreateMovimientoCajaRequest request = new CreateMovimientoCajaRequest(
                idCaja,
                idUsuario,
                TipoMovimientoCaja.VENTA_TARJETA_CREDITO.name(),
                "Venta con tarjeta crédito #" + idVenta,
                monto,
                BigDecimal.ZERO,
                null,
                idVenta,
                null
        );

        return createMovimiento(request);
    }

    // ========== Consultas y Reportes ==========

    @Override
    @Transactional(readOnly = true)
    public EstadoCajaResponse getEstadoCaja(Integer idCaja) {
        log.debug("Obteniendo estado de caja: {}", idCaja);

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", idCaja));

        BigDecimal saldoActual = movimientoCajaRepository.calcularSaldoActualCaja(idCaja);
        boolean abierta = isCajaAbierta(idCaja);

        MovimientoCaja ultimoMov = movimientoCajaRepository.findUltimoMovimientoByCaja(idCaja)
                .orElse(null);

        String nombreUsuario = null;
        LocalDateTime fechaUltimoMov = null;
        String tipoUltimoMov = null;

        if (ultimoMov != null) {
            nombreUsuario = ultimoMov.getUsuario().getNombre() + " " +
                    ultimoMov.getUsuario().getApellido();
            fechaUltimoMov = ultimoMov.getFechaHora();
            tipoUltimoMov = ultimoMov.getTipoMovimiento();
        }

        return cajaMapper.toEstadoResponse(
                caja, abierta, saldoActual, fechaUltimoMov, tipoUltimoMov, nombreUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCajaResponse getResumenDia(Integer idCaja, LocalDate fecha) {
        log.debug("Obteniendo resumen de caja del día: {} - Fecha: {}", idCaja, fecha);

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", idCaja));

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByCajaAndFechaHoraBetween(idCaja, inicio, fin);

        return calcularResumen(caja, movimientos, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenCajaResponse getResumenEntreFechas(
            Integer idCaja,
            LocalDate fechaInicio,
            LocalDate fechaFin) {

        log.debug("Obteniendo resumen de caja entre fechas: {} - {} a {}",
                idCaja, fechaInicio, fechaFin);

        Caja caja = cajaRepository.findById(idCaja)
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", idCaja));

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<MovimientoCaja> movimientos = movimientoCajaRepository
                .findByCajaAndFechaHoraBetween(idCaja, inicio, fin);

        return calcularResumen(caja, movimientos, fechaInicio);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCajaAbierta(Integer idCaja) {
        return movimientoCajaRepository.findUltimoMovimientoByCaja(idCaja)
                .map(mov -> TipoMovimientoCaja.APERTURA.name().equals(mov.getTipoMovimiento()))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroCaja(String numeroCaja) {
        return cajaRepository.existsByNumeroCaja(numeroCaja);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNumeroCajaAndSucursal(String numeroCaja, Integer idSucursal) {
        return cajaRepository.existsByNumeroCajaAndSucursal(numeroCaja, idSucursal);
    }

    // ========== Métodos Auxiliares ==========

    private ResumenCajaResponse calcularResumen(Caja caja, List<MovimientoCaja> movimientos, LocalDate fecha) {
        if (movimientos.isEmpty()) {
            ResumenCajaResponse resumen = movimientoCajaMapper.createEmptyResumen(caja);
            resumen.setFecha(fecha);
            return resumen;
        }

        BigDecimal saldoInicial = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.APERTURA.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoIngreso)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        BigDecimal totalIngresos = movimientos.stream()
                .map(MovimientoCaja::getMontoIngreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEgresos = movimientos.stream()
                .map(MovimientoCaja::getMontoEgreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasEfectivo = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.VENTA_EFECTIVO.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoIngreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasTarjetaDebito = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.VENTA_TARJETA_DEBITO.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoIngreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasTarjetaCredito = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.VENTA_TARJETA_CREDITO.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoIngreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal retiros = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.RETIRO.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoEgreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ingresos = movimientos.stream()
                .filter(m -> TipoMovimientoCaja.INGRESO.name().equals(m.getTipoMovimiento()))
                .map(MovimientoCaja::getMontoIngreso)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = saldoInicial.add(totalIngresos).subtract(totalEgresos);

        return ResumenCajaResponse.builder()
                .idCaja(caja.getIdCaja())
                .numeroCaja(caja.getNumeroCaja())
                .nombreSucursal(caja.getSucursal().getNombre())
                .fecha(fecha)
                .saldoInicial(saldoInicial)
                .totalIngresos(totalIngresos)
                .totalEgresos(totalEgresos)
                .saldoFinal(saldoFinal)
                .ventasEfectivo(ventasEfectivo)
                .ventasTarjetaDebito(ventasTarjetaDebito)
                .ventasTarjetaCredito(ventasTarjetaCredito)
                .retiros(retiros)
                .ingresos(ingresos)
                .cantidadMovimientos(movimientos.size())
                .build();
    }
}