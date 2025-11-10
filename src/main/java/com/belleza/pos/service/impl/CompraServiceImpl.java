package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.CompraDetalleRequest;
import com.belleza.pos.dto.request.CreateCompraRequest;
import com.belleza.pos.dto.response.CompraResponse;
import com.belleza.pos.dto.response.CompraSimpleResponse;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.EstadoCompra;
import com.belleza.pos.entity.enums.FormaPago;
import com.belleza.pos.entity.enums.TipoComprobante;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.CompraMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.CompraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Compra
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final CompraDetalleRepository compraDetalleRepository;
    private final ProveedorRepository proveedorRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository;
    private final ArticuloPrecioRepository articuloPrecioRepository;
    private final CompraMapper compraMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public CompraResponse create(CreateCompraRequest request) {
        log.info("Creando compra del proveedor: {}", request.idProveedor());

        // Validar tipo de comprobante
        TipoComprobante tipoComprobante;
        try {
            tipoComprobante = TipoComprobante.valueOf(request.tipoComprobante());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de comprobante inválido: " + request.tipoComprobante());
        }

        // Validar estado
        EstadoCompra estado;
        try {
            estado = EstadoCompra.valueOf(request.estado());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + request.estado());
        }

        // Validar forma de pago
        FormaPago formaPago;
        try {
            formaPago = FormaPago.valueOf(request.formaPago());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Forma de pago inválida: " + request.formaPago());
        }

        // Obtener entidades relacionadas
        Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.idProveedor()));

        Sucursal sucursal = sucursalRepository.findById(request.idSucursal())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal", "id", request.idSucursal()));

        Usuario usuario = usuarioRepository.findById(request.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", request.idUsuario()));

        // Crear compra
        Compra compra = new Compra();
        compra.setProveedor(proveedor);
        compra.setSucursal(sucursal);
        compra.setUsuario(usuario);
        compra.setTipoComprobante(tipoComprobante);
        compra.setNroComprobante(request.nroComprobante());
        compra.setFechaCompra(request.fechaCompra());
        compra.setImpuestosInternos(request.impuestosInternos());
        compra.setActualizarPrecios(request.actualizarPrecios());
        compra.setActualizarStock(request.actualizarStock());
        compra.setFormaPago(formaPago);
        compra.setEstado(estado);
        compra.setObservaciones(request.observaciones());

        // Procesar detalles y calcular totales
        AtomicInteger numeroLinea = new AtomicInteger(1);
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalIva21 = BigDecimal.ZERO;
        BigDecimal totalIva105 = BigDecimal.ZERO;

        for (CompraDetalleRequest detalleReq : request.detalles()) {
            CompraDetalle detalle = procesarDetalle(compra, detalleReq, numeroLinea.getAndIncrement());
            compra.addDetalle(detalle);

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
        compra.setSubtotal(subtotal);
        compra.setIva21(totalIva21);
        compra.setIva105(totalIva105);

        BigDecimal total = subtotal.add(totalIva21).add(totalIva105).add(request.impuestosInternos());
        compra.setTotal(total);

        // Guardar compra
        compra = compraRepository.save(compra);

        // Actualizar stock y precios si está completada
        if (estado == EstadoCompra.COMPLETADA) {
            if (compra.getActualizarStock()) {
                actualizarStock(compra);
            }
            if (compra.getActualizarPrecios()) {
                actualizarPrecios(compra);
            }
        }

        log.info("Compra creada exitosamente con ID: {}", compra.getIdCompra());
        return compraMapper.toResponse(compra);
    }


    @Override
    @Transactional(readOnly = true)
    public CompraResponse getById(Integer id) {
        log.debug("Obteniendo compra por ID: {}", id);
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", "id", id));
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public CompraResponse getByNroComprobante(String nroComprobante) {
        log.debug("Obteniendo compra por número de comprobante: {}", nroComprobante);
        Compra compra = compraRepository.findByNroComprobante(nroComprobante)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", "número de comprobante", nroComprobante));
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todas las compras con paginación");
        return compraRepository.findAll(pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando compras con término: {}", searchTerm);
        return compraRepository.search(searchTerm, pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional
    public CompraResponse anular(Integer id, String motivo) {
        log.info("Anulando compra: {}", id);

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra", "id", id));

        if (compra.getEstado() == EstadoCompra.ANULADA) {
            throw new BusinessException("La compra ya está anulada");
        }

        // Restaurar stock si estaba completada y se actualizó
        if (compra.getEstado() == EstadoCompra.COMPLETADA && compra.getActualizarStock()) {
            restaurarStock(compra);
        }

        compra.setEstado(EstadoCompra.ANULADA);
        compra.setObservaciones((compra.getObservaciones() != null ? compra.getObservaciones() + "\n" : "")
                + "ANULADA: " + motivo);

        compra = compraRepository.save(compra);

        log.info("Compra anulada exitosamente: {}", id);
        return compraMapper.toResponse(compra);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.warn("Eliminando compra permanentemente: {}", id);

        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra", "id", id);
        }

        Compra compra = compraRepository.findById(id).get();

        // Restaurar stock si estaba completada
        if (compra.getEstado() == EstadoCompra.COMPLETADA && compra.getActualizarStock()) {
            restaurarStock(compra);
        }

        // Eliminar detalles
        compraDetalleRepository.deleteByCompra_IdCompra(id);

        compraRepository.deleteById(id);
        log.info("Compra eliminada permanentemente: {}", id);
    }

    // ========== Consultas por Estado ==========

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getByEstado(String estado, Pageable pageable) {
        log.debug("Obteniendo compras por estado: {}", estado);

        EstadoCompra estadoCompra;
        try {
            estadoCompra = EstadoCompra.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado inválido: " + estado);
        }

        return compraRepository.findByEstado(estadoCompra, pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getComprasCompletadas() {
        log.debug("Obteniendo compras completadas");
        return compraRepository.findByEstado(EstadoCompra.COMPLETADA).stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getComprasPendientes() {
        log.debug("Obteniendo compras pendientes");
        return compraRepository.findByEstado(EstadoCompra.PENDIENTE).stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Proveedor ==========

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getByProveedor(Integer idProveedor, Pageable pageable) {
        log.debug("Obteniendo compras del proveedor: {}", idProveedor);

        if (!proveedorRepository.existsById(idProveedor)) {
            throw new ResourceNotFoundException("Proveedor", "id", idProveedor);
        }

        return compraRepository.findByProveedor_IdProveedor(idProveedor, pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getHistorialProveedor(Integer idProveedor) {
        log.debug("Obteniendo historial del proveedor: {}", idProveedor);

        if (!proveedorRepository.existsById(idProveedor)) {
            throw new ResourceNotFoundException("Proveedor", "id", idProveedor);
        }

        return compraRepository.findByProveedor_IdProveedor(idProveedor).stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Sucursal ==========

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getBySucursal(Integer idSucursal, Pageable pageable) {
        log.debug("Obteniendo compras de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return compraRepository.findBySucursal_IdSucursal(idSucursal, pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getComprasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Obteniendo compras del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return compraRepository.findComprasDelDiaBySucursal(idSucursal).stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Consultas por Usuario ==========

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getByUsuario(Integer idUsuario, Pageable pageable) {
        log.debug("Obteniendo compras del usuario: {}", idUsuario);

        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", "id", idUsuario);
        }

        return compraRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(compraMapper::toResponse);
    }

    // ========== Consultas por Tipo de Comprobante ==========

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getByTipoComprobante(String tipoComprobante, Pageable pageable) {
        log.debug("Obteniendo compras por tipo de comprobante: {}", tipoComprobante);

        TipoComprobante tipo;
        try {
            tipo = TipoComprobante.valueOf(tipoComprobante);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de comprobante inválido: " + tipoComprobante);
        }

        return compraRepository.findByTipoComprobante(tipo, pageable)
                .map(compraMapper::toResponse);
    }

    // ========== Consultas por Fecha ==========

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getComprasDelDia() {
        log.debug("Obteniendo compras del día");
        return compraRepository.findComprasDelDia().stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompraResponse> getByFechaCompraBetween(LocalDate fechaInicio,
                                                        LocalDate fechaFin,
                                                        Pageable pageable) {
        log.debug("Obteniendo compras entre {} y {}", fechaInicio, fechaFin);
        return compraRepository.findByFechaCompraBetween(fechaInicio, fechaFin, pageable)
                .map(compraMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraSimpleResponse> getComprasByPeriodo(LocalDate fechaInicio,
                                                          LocalDate fechaFin) {
        log.debug("Obteniendo compras del período {} - {}", fechaInicio, fechaFin);
        return compraRepository.findByFechaCompraBetween(fechaInicio, fechaFin).stream()
                .map(compraMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    // ========== Estadísticas y Totales ==========

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalComprasDelDia() {
        log.debug("Obteniendo total de compras del día");
        return compraRepository.getTotalComprasDelDia();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalComprasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Obteniendo total de compras del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return compraRepository.getTotalComprasDelDiaBySucursal(idSucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalComprasByPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        log.debug("Obteniendo total de compras del período {} - {}", fechaInicio, fechaFin);
        return compraRepository.getTotalComprasByPeriodo(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countComprasDelDia() {
        log.debug("Contando compras del día");
        return compraRepository.countComprasDelDia();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countComprasDelDiaBySucursal(Integer idSucursal) {
        log.debug("Contando compras del día de la sucursal: {}", idSucursal);

        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return compraRepository.countComprasDelDiaBySucursal(idSucursal);
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNroComprobante(String nroComprobante) {
        return compraRepository.existsByNroComprobante(nroComprobante);
    }

    // ========== Métodos Privados ==========

    private CompraDetalle procesarDetalle(Compra compra, CompraDetalleRequest request, int numeroLinea) {
        Articulo articulo = articuloRepository.findById(request.idArticulo())
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", request.idArticulo()));

        CompraDetalle detalle = new CompraDetalle();
        detalle.setCompra(compra);
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

    private void actualizarStock(Compra compra) {
        log.debug("Actualizando stock para compra: {}", compra.getIdCompra());

        for (CompraDetalle detalle : compra.getDetalles()) {
            Articulo articulo = detalle.getArticulo();

            if (articulo.getUsaControlStock()) {
                BigDecimal nuevoStock = articulo.getStockActual().add(detalle.getCantidad());
                articulo.setStockActual(nuevoStock);
                articuloRepository.save(articulo);
                log.debug("Stock actualizado para artículo {}: {}", articulo.getIdArticulo(), nuevoStock);
            }
        }
    }

    private void restaurarStock(Compra compra) {
        log.debug("Restaurando stock para compra: {}", compra.getIdCompra());

        for (CompraDetalle detalle : compra.getDetalles()) {
            Articulo articulo = detalle.getArticulo();

            if (articulo.getUsaControlStock()) {
                BigDecimal nuevoStock = articulo.getStockActual().subtract(detalle.getCantidad());

                if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                    nuevoStock = BigDecimal.ZERO;
                }

                articulo.setStockActual(nuevoStock);
                articuloRepository.save(articulo);
                log.debug("Stock restaurado para artículo {}: {}", articulo.getIdArticulo(), nuevoStock);
            }
        }
    }

    private void actualizarPrecios(Compra compra) {
        log.debug("Actualizando precios para compra: {}", compra.getIdCompra());

        for (CompraDetalle detalle : compra.getDetalles()) {
            Articulo articulo = detalle.getArticulo();

            // Actualizar precio de costo en todas las listas de precios del artículo
            List<ArticuloPrecio> precios = articuloPrecioRepository.findByArticulo_IdArticulo(articulo.getIdArticulo());

            for (ArticuloPrecio precio : precios) {
                precio.setPrecioCosto(detalle.getPrecioUnitarioConIva());
                articuloPrecioRepository.save(precio);
            }

            log.debug("Precios actualizados para artículo {}: nuevo costo {}",
                    articulo.getIdArticulo(), detalle.getPrecioUnitarioConIva());
        }
    }
}
