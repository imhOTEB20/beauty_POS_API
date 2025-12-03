// ==========================================
// MovimientoCajaServiceImpl.java (IMPLEMENTATION)
// ==========================================
package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateMovimientoCajaRequest;
import com.belleza.pos.dto.response.MovimientoCajaResponse;
import com.belleza.pos.entity.Caja;
import com.belleza.pos.entity.MovimientoCaja;
import com.belleza.pos.entity.Usuario;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.MovimientoCajaMapper;
import com.belleza.pos.repository.CajaRepository;
import com.belleza.pos.repository.MovimientoCajaRepository;
import com.belleza.pos.repository.UsuarioRepository;
import com.belleza.pos.service.MovimientoCajaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de MovimientoCaja
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoCajaMapper movimientoCajaMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public MovimientoCajaResponse create(CreateMovimientoCajaRequest request) {
        log.info("Creando movimiento de caja - Tipo: {} - Caja: {}",
                request.tipoMovimiento(), request.idCaja());

        // Validar caja
        Caja caja = cajaRepository.findById(request.idCaja())
                .orElseThrow(() -> new ResourceNotFoundException("Caja", "id", request.idCaja()));

        if (!caja.getActivo()) {
            throw new BusinessException("La caja está inactiva y no puede registrar movimientos");
        }

        // Validar usuario
        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        if (!usuario.getActivo()) {
            throw new BusinessException("El usuario está inactivo");
        }

        // Obtener saldo anterior
        BigDecimal saldoAnterior = movimientoCajaRepository.calcularSaldoActualCaja(request.idCaja());

        // Validar si es un egreso que no hay suficiente saldo
        BigDecimal egreso = request.montoEgreso() != null ? request.montoEgreso() : BigDecimal.ZERO;
        if (egreso.compareTo(BigDecimal.ZERO) > 0 && saldoAnterior.compareTo(egreso) < 0) {
            log.warn("Intento de egreso sin saldo suficiente - Saldo: {} - Egreso: {}",
                    saldoAnterior, egreso);
            // No bloquear, solo advertir (podría ser un ajuste autorizado)
        }

        // Crear movimiento
        MovimientoCaja movimiento = movimientoCajaMapper.toEntity(request, caja, usuario, saldoAnterior);
        movimiento = movimientoCajaRepository.save(movimiento);

        log.info("Movimiento de caja creado exitosamente - ID: {} - Saldo nuevo: {}",
                movimiento.getIdMovimiento(), movimiento.getSaldoActual());

        return movimientoCajaMapper.toResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCajaResponse getById(Integer id) {
        log.debug("Obteniendo movimiento de caja por ID: {}", id);
        MovimientoCaja movimiento = movimientoCajaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento de caja", "id", id));
        return movimientoCajaMapper.toResponse(movimiento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getAllByCaja(Integer idCaja) {
        log.debug("Obteniendo todos los movimientos de la caja: {}", idCaja);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCaja_IdCajaOrderByFechaHoraDesc(idCaja).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoCajaResponse> getAllByCaja(Integer idCaja, Pageable pageable) {
        log.debug("Obteniendo movimientos de la caja con paginación: {}", idCaja);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCaja_IdCaja(idCaja, pageable)
                .map(movimientoCajaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByFechaHoraBetween(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Obteniendo movimientos entre fechas - Caja: {} - Desde: {} - Hasta: {}",
                idCaja, fechaInicio, fechaFin);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCajaAndFechaHoraBetween(idCaja, fechaInicio, fechaFin).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MovimientoCajaResponse> getByFechaHoraBetween(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {

        log.debug("Obteniendo movimientos entre fechas con paginación - Caja: {}", idCaja);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCajaAndFechaHoraBetween(
                        idCaja, fechaInicio, fechaFin, pageable)
                .map(movimientoCajaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCajaResponse getUltimoMovimiento(Integer idCaja) {
        log.debug("Obteniendo último movimiento de la caja: {}", idCaja);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findUltimoMovimientoByCaja(idCaja)
                .map(movimientoCajaMapper::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoCajaResponse getPrimerMovimientoDelDia(Integer idCaja, LocalDateTime fecha) {
        log.debug("Obteniendo primer movimiento del día - Caja: {} - Fecha: {}", idCaja, fecha);
        validarCajaExiste(idCaja);

        LocalDateTime inicio = fecha.toLocalDate().atStartOfDay();
        LocalDateTime fin = fecha.toLocalDate().atTime(LocalTime.MAX);

        return movimientoCajaRepository.findPrimerMovimientoDelDia(idCaja, inicio, fin)
                .map(movimientoCajaMapper::toResponse)
                .orElse(null);
    }

    // ========== Consultas por Tipo ==========

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByTipoMovimiento(String tipoMovimiento) {
        log.debug("Obteniendo movimientos por tipo: {}", tipoMovimiento);
        return movimientoCajaRepository.findByTipoMovimiento(tipoMovimiento).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByCajaAndTipoMovimiento(Integer idCaja, String tipoMovimiento) {
        log.debug("Obteniendo movimientos por caja y tipo - Caja: {} - Tipo: {}", idCaja, tipoMovimiento);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCajaAndTipoMovimiento(idCaja, tipoMovimiento).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByCajaAndTipoMovimientoAndFechas(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Obteniendo movimientos por caja, tipo y fechas - Caja: {} - Tipo: {}",
                idCaja, tipoMovimiento);
        validarCajaExiste(idCaja);

        return movimientoCajaRepository.findByCajaAndTipoMovimientoAndFechaHoraBetween(
                        idCaja, tipoMovimiento, fechaInicio, fechaFin).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Usuario ==========

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByUsuario(Integer idUsuario) {
        log.debug("Obteniendo movimientos por usuario: {}", idUsuario);
        validarUsuarioExiste(idUsuario);

        return movimientoCajaRepository.findByUsuario_IdUsuario(idUsuario).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByUsuarioAndFechas(
            Integer idUsuario,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Obteniendo movimientos por usuario y fechas - Usuario: {}", idUsuario);
        validarUsuarioExiste(idUsuario);

        return movimientoCajaRepository.findByUsuarioAndFechaHoraBetween(
                        idUsuario, fechaInicio, fechaFin).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Venta/Compra ==========

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByVenta(Integer idVenta) {
        log.debug("Obteniendo movimientos asociados a venta: {}", idVenta);
        return movimientoCajaRepository.findByIdVenta(idVenta).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> getByCompra(Integer idCompra) {
        log.debug("Obteniendo movimientos asociados a compra: {}", idCompra);
        return movimientoCajaRepository.findByIdCompra(idCompra).stream()
                .map(movimientoCajaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByVenta(Integer idVenta) {
        return movimientoCajaRepository.existsByIdVenta(idVenta);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCompra(Integer idCompra) {
        return movimientoCajaRepository.existsByIdCompra(idCompra);
    }

    // ========== Cálculos ==========

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoActual(Integer idCaja) {
        log.debug("Calculando saldo actual de caja: {}", idCaja);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.calcularSaldoActualCaja(idCaja);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Calculando saldo entre fechas - Caja: {}", idCaja);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.calcularSaldoEntreFechas(idCaja, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalIngresosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Calculando total de ingresos entre fechas - Caja: {}", idCaja);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.calcularTotalIngresosEntreFechas(idCaja, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalEgresosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Calculando total de egresos entre fechas - Caja: {}", idCaja);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.calcularTotalEgresosEntreFechas(idCaja, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalPorTipoMovimiento(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Calculando total por tipo - Caja: {} - Tipo: {}", idCaja, tipoMovimiento);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.calcularTotalPorTipoMovimiento(
                idCaja, tipoMovimiento, fechaInicio, fechaFin);
    }

    // ========== Estadísticas ==========

    @Override
    @Transactional(readOnly = true)
    public Long contarMovimientosEntreFechas(
            Integer idCaja,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Contando movimientos entre fechas - Caja: {}", idCaja);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.contarMovimientosEntreFechas(idCaja, fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarMovimientosPorTipo(
            Integer idCaja,
            String tipoMovimiento,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        log.debug("Contando movimientos por tipo - Caja: {} - Tipo: {}", idCaja, tipoMovimiento);
        validarCajaExiste(idCaja);
        return movimientoCajaRepository.contarMovimientosPorTipo(
                idCaja, tipoMovimiento, fechaInicio, fechaFin);
    }

    // ========== Utilidades ==========

    @Override
    @Transactional
    public void deleteAllByCaja(Integer idCaja) {
        log.warn("Eliminando todos los movimientos de la caja: {}", idCaja);
        validarCajaExiste(idCaja);
        movimientoCajaRepository.deleteByCaja_IdCaja(idCaja);
        log.info("Movimientos eliminados de la caja: {}", idCaja);
    }

    // ========== Métodos Privados de Validación ==========

    private void validarCajaExiste(Integer idCaja) {
        if (!cajaRepository.existsById(idCaja)) {
            throw new ResourceNotFoundException("Caja", "id", idCaja);
        }
    }

    private void validarUsuarioExiste(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }
    }
}