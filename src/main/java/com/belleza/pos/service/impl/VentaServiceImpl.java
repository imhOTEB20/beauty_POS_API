package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CreateVentaRequest;
import com.belleza.pos.dto.request.UpdateVentaRequest;
import com.belleza.pos.dto.request.VentaDetalleRequest;
import com.belleza.pos.dto.request.VentaFormaPagoRequest;
import com.belleza.pos.dto.response.VentaResponse;
import com.belleza.pos.dto.response.VentaSimpleResponse;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.EstadoVenta;
import com.belleza.pos.entity.enums.FormaPago;
import com.belleza.pos.entity.enums.TipoComprobante;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.VentaMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Venta
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final VentaDetalleRepository ventaDetalleRepository;
    private final VentaFormaPagoRepository ventaFormaPagoRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final ArticuloRepository articuloRepository;
    private final ArticuloPrecioRepository articuloPrecioRepository;
    private final VentaMapper ventaMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public VentaResponse create(CreateVentaRequest request) {
        log.info("Creando venta con número de transacción: {}", request.nroTransaccion());

        // Validar número de transacción único
        if (ventaRepository.existsByNroTransaccion(request.nroTransaccion())) {
            throw new BusinessException("Ya existe una venta con el número de transacción: " + request.nroTransaccion());
        }

        // Validar tipo de comprobante
        TipoComprobante tipoComprobante;
        try {
            tipoComprobante = TipoComprobante.valueOf(request.tipoComprobante());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de comprobante inválido: " + request.tipoComprobante());
        }

        // Validar estado
        EstadoVenta estado;
        try {
            estado = EstadoVenta.valueOf(request.estado());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + request.estado());
        }

        // Obtener entidades relacionadas
        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        Cliente cliente = null;
        if (request.idCliente() != null) {
            cliente = clienteRepository.findById(request.idCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", request.idCliente()));
        }

        ListaPrecio listaPrecio = listaPrecioRepository.findById(request.idListaPrecio())
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", request.idListaPrecio()));

        // Crear venta
        Venta venta = new Venta();
        venta.setNroTransaccion(request.nroTransaccion());
        venta.setNroCaja(request.nroCaja());
        venta.setSucursal(sucursal);
        venta.setUsuario(usuario);
        venta.setCliente(cliente);
        venta.setTipoComprobante(tipoComprobante);
        venta.setNroComprobante(request.nroComprobante());
        venta.setCae(request.cae());
        venta.setFechaVencimientoCae(request.fechaVencimientoCae());
        venta.setListaPrecio(listaPrecio);
        venta.setEstado(estado);
        venta.setObservaciones(request.observaciones());

        // Procesar detalles
        AtomicInteger numeroLinea = new AtomicInteger(1);
        BigDecimal subtotalVenta = BigDecimal.ZERO;

        for (VentaDetalleRequest detalleReq : request.detalles()) {
            VentaDetalle detalle = procesarDetalle(venta, detalleReq, numeroLinea.getAndIncrement());
            venta.addDetalle(detalle);
            subtotalVenta = subtotalVenta.add(detalle.getSubtotal());
        }

        // Calcular totales
        venta.setSubtotal(subtotalVenta);
        venta.setDescuentoPorcentaje(request.descuentoPorcentaje());
        venta.setDescuentoMonto(request.descuentoMonto());
        venta.setRecargoPorcentaje(request.recargoPorcentaje());
        venta.setRecargoMonto(request.recargoMonto());

        BigDecimal total = calcularTotal(subtotalVenta, request.descuentoPorcentaje(),
                request.descuentoMonto(), request.recargoPorcentaje(),
                request.recargoMonto());
        venta.setTotal(total);

        // Validar formas de pago
        validarFormasPago(request.formasPago(), total);

        // Procesar formas de pago
        for (VentaFormaPagoRequest formaPagoReq : request.formasPago()) {
            VentaFormaPago formaPago = procesarFormaPago(venta, formaPagoReq);
            venta.addFormaPago(formaPago);
        }

        // Guardar venta
        venta = ventaRepository.save(venta);

        // Actualizar stock si la venta está completada
        if (estado == EstadoVenta.COMPLETADA) {
            actualizarStock(venta);
        }

        log.info("Venta creada exitosamente con ID: {}", venta.getIdVenta());
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public VentaResponse update(Integer id, UpdateVentaRequest request) {
        log.info("Actualizando venta con ID: {}", id);

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));

        // Validar que no esté anulada
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException("No se puede actualizar una venta anulada");
        }

        // Actualizar campos permitidos
        if (request.nroComprobante() != null) {
            venta.setNroComprobante(request.nroComprobante());
        }
        if (request.cae() != null) {
            venta.setCae(request.cae());
        }
        if (request.fechaVencimientoCae() != null) {
            venta.setFechaVencimientoCae(request.fechaVencimientoCae());
        }
        if (request.estado() != null) {
            EstadoVenta nuevoEstado;
            try {
                nuevoEstado = EstadoVenta.valueOf(request.estado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado inválido: " + request.estado());
            }

            // Si cambia de PENDIENTE a COMPLETADA, actualizar stock
            if (venta.getEstado() == EstadoVenta.PENDIENTE && nuevoEstado == EstadoVenta.COMPLETADA) {
                actualizarStock(venta);
            }

            venta.setEstado(nuevoEstado);
        }
        if (request.observaciones() != null) {
            venta.setObservaciones(request.observaciones());
        }

        venta = ventaRepository.save(venta);

        log.info("Venta actualizada exitosamente: {}", id);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse getById(Integer id) {
        log.debug("Obteniendo venta por ID: {}", id);
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponse getByNroTransaccion(String nroTransaccion) {
        log.debug("Obteniendo venta por número de transacción: {}", nroTransaccion);
        Venta venta = ventaRepository.findByNroTransaccion(nroTransaccion)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "número de transacción", nroTransaccion));
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las ventas con paginación");
        return ventaRepository.findAll(pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando ventas con término: {}", searchTerm);
        return ventaRepository.search(searchTerm, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional
    public VentaResponse anular(Integer id, String motivo) {
        log.info("Anulando venta: {}", id);

        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", "id", id));

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException("La venta ya está anulada");
        }

        // Restaurar stock si estaba completada
        if (venta.getEstado() == EstadoVenta.COMPLETADA) {
            restaurarStock(venta);
        }

        venta.setEstado(EstadoVenta.ANULADA);
        venta.setObservaciones((venta.getObservaciones() != null ? venta.getObservaciones() + "\n" : "")
                + "ANULADA: " + motivo);

        venta = ventaRepository.save(venta);

        log.info("Venta anulada exitosamente: {}", id);
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.warn("Eliminando venta permanentemente: {}", id);

        if (!ventaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta", "id", id);
        }

        Venta venta = ventaRepository.findById(id).get();

        // Restaurar stock si estaba completada
        if (venta.getEstado() == EstadoVenta.COMPLETADA) {
            restaurarStock(venta);
        }

        // Eliminar detalles y formas de pago
        ventaDetalleRepository.deleteByVenta_IdVenta(id);
        ventaFormaPagoRepository.deleteByVenta_IdVenta(id);

        ventaRepository.deleteById(id);
        log.info("Venta eliminada permanentemente: {}", id);
    }

    // ========== Consultas por Estado ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getByEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo ventas por estado: {}", estado);

        EstadoVenta estadoVenta;
        try {
            estadoVenta = EstadoVenta.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + estado);
        }

        return ventaRepository.findByEstado(estadoVenta, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getVentasCompletadas() {
        log.debug("Obteniendo ventas completadas");
        return ventaRepository.findByEstado(EstadoVenta.COMPLETADA).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getVentasPendientes() {
        log.debug("Obteniendo ventas pendientes");
        return ventaRepository.findByEstado(EstadoVenta.PENDIENTE).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getPresupuestos() {
        log.debug("Obteniendo presupuestos");
        return ventaRepository.findByEstado(EstadoVenta.PRESUPUESTO).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Cliente ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getByCliente(Integer idCliente, Pageable pageable) {
        log.debug("Obteniendo ventas del cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return ventaRepository.findByCliente_IdCliente(idCliente, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getHistorialCliente(Integer idCliente) {
        log.debug("Obteniendo historial del cliente: {}", idCliente);

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResourceNotFoundException("Cliente", "id", idCliente);
        }

        return ventaRepository.findByCliente_IdCliente(idCliente).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Sucursal ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getBySucursal(Integer idSucursal, Pageable pageable) {
        log.debug("Obteniendo ventas de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return ventaRepository.findBySucursal_IdSucursal(idSucursal, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getVentasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Obteniendo ventas del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return ventaRepository.findVentasDelDiaBySucursal(idSucursal).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Usuario ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getByUsuario(Integer idUsuario, Pageable pageable) {
        log.debug("Obteniendo ventas del usuario: {}", idUsuario);

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }

        return ventaRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(ventaMapper::toResponse);
    }

    // ========== Consultas por Tipo de Comprobante ==========

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getByTipoComprobante(String tipoComprobante, Pageable pageable) {
        log.debug("Obteniendo ventas por tipo de comprobante: {}", tipoComprobante);

        TipoComprobante tipo;
        try {
            tipo = TipoComprobante.valueOf(tipoComprobante);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de comprobante inválido: " + tipoComprobante);
        }

        return ventaRepository.findByTipoComprobante(tipo, pageable)
                .map(ventaMapper::toResponse);
    }

    // ========== Consultas por Fecha ==========

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getVentasDelDia() {
        log.debug("Obteniendo ventas del día");
        return ventaRepository.findVentasDelDia().stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VentaResponse> getByFechaVentaBetween(LocalDateTime fechaInicio,
                                                      LocalDateTime fechaFin,
                                                      Pageable pageable) {
        log.debug("Obteniendo ventas entre {} y {}", fechaInicio, fechaFin);
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin, pageable)
                .map(ventaMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaSimpleResponse> getVentasByPeriodo(LocalDateTime fechaInicio,
                                                        LocalDateTime fechaFin) {
        log.debug("Obteniendo ventas del período {} - {}", fechaInicio, fechaFin);
        return ventaRepository.findByFechaVentaBetween(fechaInicio, fechaFin).stream()
                .map(ventaMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Estadísticas y Totales ==========

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalVentasDelDia() {
        log.debug("Obteniendo total de ventas del día");
        return ventaRepository.getTotalVentasDelDia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalVentasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Obteniendo total de ventas del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return ventaRepository.getTotalVentasDelDiaBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalVentasByPeriodo(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Obteniendo total de ventas del período {} - {}", fechaInicio, fechaFin);
        return ventaRepository.getTotalVentasByPeriodo(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVentasDelDia() {
        log.debug("Contando ventas del día");
        return ventaRepository.countVentasDelDia();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countVentasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Contando ventas del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return ventaRepository.countVentasDelDiaBySucursal(idSucursal);
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroTransaccion(String nroTransaccion) {
        return ventaRepository.existsByNroTransaccion(nroTransaccion);
    }

    @Override
    @Transactional(readOnly = true)
    public String generarNroTransaccion() {
        String prefix = "VTA-";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return prefix + timestamp;
    }

    // ========== Métodos Privados ==========

    private VentaDetalle procesarDetalle(Venta venta, VentaDetalleRequest request, int numeroLinea) {
        Articulo articulo = articuloRepository.findById(request.idArticulo())
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", request.idArticulo()));

        VentaDetalle detalle = new VentaDetalle();
        detalle.setVenta(venta);
        detalle.setNumeroLinea(numeroLinea);
        detalle.setArticulo(articulo);
        detalle.setCodigoBarras(articulo.getCodigoBarras());
        detalle.setDescripcion(articulo.getDescripcion());
        detalle.setCantidad(request.cantidad());
        detalle.setPrecioUnitario(request.precioUnitario());
        detalle.setDescuentoPorcentaje(request.descuentoPorcentaje());
        detalle.setDescuentoMonto(request.descuentoMonto());
        detalle.calcularSubtotal();

        return detalle;
    }

    private VentaFormaPago procesarFormaPago(Venta venta, VentaFormaPagoRequest request) {
        FormaPago formaPago;
        try {
            formaPago = FormaPago.valueOf(request.formaPago());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Forma de pago inválida: " + request.formaPago());
        }

        VentaFormaPago ventaFormaPago = new VentaFormaPago();
        ventaFormaPago.setVenta(venta);
        ventaFormaPago.setFormaPago(formaPago);
        ventaFormaPago.setMonto(request.monto());
        ventaFormaPago.setDetalle(request.detalle());

        return ventaFormaPago;
    }

    private BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal descuentoPorcentaje,
                                     BigDecimal descuentoMonto, BigDecimal recargoPorcentaje,
                                     BigDecimal recargoMonto) {
        BigDecimal total = subtotal;

        // Aplicar descuento por porcentaje
        if (descuentoPorcentaje != null && descuentoPorcentaje.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuento = total.multiply(descuentoPorcentaje).divide(BigDecimal.valueOf(100));
            total = total.subtract(descuento);
        }

        // Aplicar descuento por monto
        if (descuentoMonto != null && descuentoMonto.compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(descuentoMonto);
        }

        // Aplicar recargo por porcentaje
        if (recargoPorcentaje != null && recargoPorcentaje.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal recargo = total.multiply(recargoPorcentaje).divide(BigDecimal.valueOf(100));
            total = total.add(recargo);
        }

        // Aplicar recargo por monto
        if (recargoMonto != null && recargoMonto.compareTo(BigDecimal.ZERO) > 0) {
            total = total.add(recargoMonto);
        }

        return total;
    }

    private void validarFormasPago(List<VentaFormaPagoRequest> formasPago, BigDecimal total) {
        BigDecimal totalPagado = formasPago.stream()
                .map(VentaFormaPagoRequest::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Permitir un margen de error de 0.01 por redondeos
        if (totalPagado.subtract(total).abs().compareTo(new BigDecimal("0.01")) > 0) {
            throw new BusinessException(
                    String.format("El total de las formas de pago (%.2f) no coincide con el total de la venta (%.2f)",
                            totalPagado, total)
            );
        }
    }

    private void actualizarStock(Venta venta) {
        log.debug("Actualizando stock para venta: {}", venta.getIdVenta());

        for (VentaDetalle detalle : venta.getDetalles()) {
            Articulo articulo = detalle.getArticulo();

            if (articulo.getUsaControlStock()) {
                BigDecimal nuevoStock = articulo.getStockActual().subtract(detalle.getCantidad());

                if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException(
                            String.format("Stock insuficiente para el artículo: %s. Stock actual: %s, Requerido: %s",
                                    articulo.getDescripcion(), articulo.getStockActual(), detalle.getCantidad())
                    );
                }

                articulo.setStockActual(nuevoStock);
                articuloRepository.save(articulo);
                log.debug("Stock actualizado para artículo {}: {}", articulo.getIdArticulo(), nuevoStock);
            }
        }
    }

    private void restaurarStock(Venta venta) {
        log.debug("Restaurando stock para venta: {}", venta.getIdVenta());

        for (VentaDetalle detalle : venta.getDetalles()) {
            Articulo articulo = detalle.getArticulo();

            if (articulo.getUsaControlStock()) {
                BigDecimal nuevoStock = articulo.getStockActual().add(detalle.getCantidad());
                articulo.setStockActual(nuevoStock);
                articuloRepository.save(articulo);
                log.debug("Stock restaurado para artículo {}: {}", articulo.getIdArticulo(), nuevoStock);
            }
        }
    }
}