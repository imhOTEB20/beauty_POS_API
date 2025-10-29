package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.UnidadVenta;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.ArticuloMapper;
import com.belleza.pos.repository.*;
import com.belleza.pos.service.ArticuloService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Artículo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticuloServiceImpl implements ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final RubroRepository rubroRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final ProveedorRepository proveedorRepository;
    private final ArticuloPrecioRepository articuloPrecioRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;
    private final ArticuloMapper articuloMapper;

    // ========== CRUD Básico ==========

    @Override
    @Transactional
    public ArticuloResponse create(CreateArticuloRequest request) {
        log.info("Creando artículo con código: {}", request.codigoBarras());

        // Validar que el código de barras no exista
        if (articuloRepository.existsByCodigoBarras(request.codigoBarras())) {
            throw new BusinessException("Ya existe un artículo con el código de barras: " + request.codigoBarras());
        }

        // Validar unidad de venta
        try {
            UnidadVenta.valueOf(request.unidadVenta());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Unidad de venta inválida: " + request.unidadVenta());
        }

        // Obtener rubro si se proporciona
        Rubro rubro = null;
        if (request.idRubro() != null) {
            rubro = rubroRepository.findById(request.idRubro())
                    .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", request.idRubro()));
        }

        // Crear artículo
        Articulo articulo = articuloMapper.toEntity(request, rubro);
        articulo = articuloRepository.save(articulo);

        // Agregar precios si se proporcionan
        if (request.precios() != null && !request.precios().isEmpty()) {
            for (PrecioRequest precioReq : request.precios()) {
                agregarPrecio(articulo, precioReq);
            }
        }

        // Agregar proveedores si se proporcionan
        if (request.proveedores() != null && !request.proveedores().isEmpty()) {
            for (ProveedorArticuloRequest provReq : request.proveedores()) {
                agregarProveedor(articulo, provReq);
            }
        }

        articulo = articuloRepository.save(articulo);
        log.info("Artículo creado exitosamente con ID: {}", articulo.getIdArticulo());
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional
    public ArticuloResponse update(Integer id, UpdateArticuloRequest request) {
        log.info("Actualizando artículo con ID: {}", id);

        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));

        // Validar código de barras si se está cambiando
        if (request.codigoBarras() != null && !request.codigoBarras().equals(articulo.getCodigoBarras())) {
            if (articuloRepository.existsByCodigoBarras(request.codigoBarras())) {
                throw new BusinessException("Ya existe un artículo con el código de barras: " + request.codigoBarras());
            }
        }

        // Validar unidad de venta si se está cambiando
        if (request.unidadVenta() != null) {
            try {
                UnidadVenta.valueOf(request.unidadVenta());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Unidad de venta inválida: " + request.unidadVenta());
            }
        }

        // Obtener rubro si se está cambiando
        Rubro rubro = null;
        if (request.idRubro() != null) {
            rubro = rubroRepository.findById(request.idRubro())
                    .orElseThrow(() -> new ResourceNotFoundException("Rubro", "id", request.idRubro()));
        }

        // Actualizar artículo
        articuloMapper.updateEntity(articulo, request, rubro);
        articulo = articuloRepository.save(articulo);

        log.info("Artículo actualizado exitosamente: {}", id);
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloResponse getById(Integer id) {
        log.debug("Obteniendo artículo por ID: {}", id);
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticuloResponse getByCodigoBarras(String codigoBarras) {
        log.debug("Obteniendo artículo por código de barras: {}", codigoBarras);
        Articulo articulo = articuloRepository.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "código de barras", codigoBarras));
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticuloResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los artículos con paginación");
        return articuloRepository.findAll(pageable)
                .map(articuloMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloSimpleResponse> getAllActive() {
        log.debug("Obteniendo todos los artículos activos");
        return articuloRepository.findByActivo(true).stream()
                .map(articuloMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticuloResponse> search(String searchTerm, Pageable pageable) {
        log.debug("Buscando artículos con término: {}", searchTerm);
        return articuloRepository.search(searchTerm, pageable)
                .map(articuloMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ArticuloResponse> getByRubro(Integer idRubro, Pageable pageable) {
        log.debug("Obteniendo artículos por rubro: {}", idRubro);

        if (!rubroRepository.existsById(idRubro)) {
            throw new ResourceNotFoundException("Rubro", "id", idRubro);
        }

        return articuloRepository.findByRubro_IdRubro(idRubro, pageable)
                .map(articuloMapper::toResponse);
    }

    @Override
    @Transactional
    public ArticuloResponse activate(Integer id) {
        log.info("Activando artículo: {}", id);
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));

        articulo.setActivo(true);
        articulo = articuloRepository.save(articulo);

        log.info("Artículo activado exitosamente: {}", id);
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional
    public ArticuloResponse deactivate(Integer id) {
        log.info("Desactivando artículo: {}", id);
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));

        articulo.setActivo(false);
        articulo = articuloRepository.save(articulo);

        log.info("Artículo desactivado exitosamente: {}", id);
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando artículo (soft delete): {}", id);
        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));

        articulo.setActivo(false);
        articuloRepository.save(articulo);

        log.info("Artículo eliminado exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando artículo permanentemente: {}", id);

        if (!articuloRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artículo", "id", id);
        }

        // Eliminar precios y proveedores asociados
        articuloPrecioRepository.deleteByArticulo_IdArticulo(id);
        articuloProveedorRepository.deleteByArticulo_IdArticulo(id);

        articuloRepository.deleteById(id);
        log.info("Artículo eliminado permanentemente: {}", id);
    }

    // ========== Gestión de Precios ==========

    @Override
    @Transactional
    public PrecioResponse addOrUpdatePrecio(Integer idArticulo, PrecioRequest request) {
        log.info("Agregando/actualizando precio para artículo: {}", idArticulo);

        Articulo articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", idArticulo));

        ListaPrecio lista = listaPrecioRepository.findById(request.idLista())
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", request.idLista()));

        // Buscar si ya existe el precio
        ArticuloPrecio precio = articuloPrecioRepository
                .findByArticulo_IdArticuloAndListaPrecio_IdLista(idArticulo, request.idLista())
                .orElse(new ArticuloPrecio());

        precio.setArticulo(articulo);
        precio.setListaPrecio(lista);
        precio.setPrecioCosto(request.precioCosto());
        precio.setPrecioVenta(request.precioVenta());
        precio.setPorcentajeUtilidad(request.porcentajeUtilidad());

        precio = articuloPrecioRepository.save(precio);

        log.info("Precio agregado/actualizado exitosamente");
        return articuloMapper.toPrecioResponse(precio);
    }

    @Override
    @Transactional
    public void removePrecio(Integer idArticulo, Integer idLista) {
        log.info("Eliminando precio de artículo: {} en lista: {}", idArticulo, idLista);

        ArticuloPrecio precio = articuloPrecioRepository
                .findByArticulo_IdArticuloAndListaPrecio_IdLista(idArticulo, idLista)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Precio no encontrado para artículo " + idArticulo + " en lista " + idLista));

        articuloPrecioRepository.delete(precio);
        log.info("Precio eliminado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrecioResponse> getPreciosByArticulo(Integer idArticulo) {
        log.debug("Obteniendo precios del artículo: {}", idArticulo);

        if (!articuloRepository.existsById(idArticulo)) {
            throw new ResourceNotFoundException("Artículo", "id", idArticulo);
        }

        return articuloPrecioRepository.findByArticulo_IdArticulo(idArticulo).stream()
                .map(articuloMapper::toPrecioResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<PrecioResponse> updatePrecios(Integer idArticulo, List<PrecioRequest> precios) {
        log.info("Actualizando múltiples precios para artículo: {}", idArticulo);

        Articulo articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", idArticulo));

        // Eliminar precios existentes
        articuloPrecioRepository.deleteByArticulo_IdArticulo(idArticulo);

        // Agregar nuevos precios
        for (PrecioRequest precioReq : precios) {
            agregarPrecio(articulo, precioReq);
        }

        return getPreciosByArticulo(idArticulo);
    }

    // Método auxiliar para agregar precio
    private void agregarPrecio(Articulo articulo, PrecioRequest request) {
        ListaPrecio lista = listaPrecioRepository.findById(request.idLista())
                .orElseThrow(() -> new ResourceNotFoundException("Lista de precios", "id", request.idLista()));

        ArticuloPrecio precio = new ArticuloPrecio();
        precio.setArticulo(articulo);
        precio.setListaPrecio(lista);
        precio.setPrecioCosto(request.precioCosto());
        precio.setPrecioVenta(request.precioVenta());
        precio.setPorcentajeUtilidad(request.porcentajeUtilidad());

        if (articulo.getPrecios() == null) {
            articulo.setPrecios(new HashSet<>());
        }
        articulo.getPrecios().add(precio);
    }

    // ========== Gestión de Proveedores ==========

    @Override
    @Transactional
    public ProveedorArticuloResponse addProveedor(Integer idArticulo, ProveedorArticuloRequest request) {
        log.info("Agregando proveedor {} al artículo: {}", request.idProveedor(), idArticulo);

        Articulo articulo = articuloRepository.findById(idArticulo)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", idArticulo));

        Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.idProveedor()));

        // Verificar si ya existe la relación
        if (articuloProveedorRepository.findByArticulo_IdArticuloAndProveedor_IdProveedor(
                idArticulo, request.idProveedor()).isPresent()) {
            throw new BusinessException("El proveedor ya está asociado a este artículo");
        }

        // Si se marca como predeterminado, quitar el predeterminado anterior
        if (request.esPredeterminado()) {
            articuloProveedorRepository.findProveedorPredeterminadoByArticulo(idArticulo)
                    .ifPresent(ap -> {
                        ap.setEsPredeterminado(false);
                        articuloProveedorRepository.save(ap);
                    });
        }

        ArticuloProveedor articuloProveedor = new ArticuloProveedor();
        articuloProveedor.setArticulo(articulo);
        articuloProveedor.setProveedor(proveedor);
        articuloProveedor.setCosto(request.costo());
        articuloProveedor.setEsPredeterminado(request.esPredeterminado());

        articuloProveedor = articuloProveedorRepository.save(articuloProveedor);

        log.info("Proveedor agregado exitosamente");
        return articuloMapper.toProveedorArticuloResponse(articuloProveedor);
    }

    @Override
    @Transactional
    public void removeProveedor(Integer idArticulo, Integer idProveedor) {
        log.info("Eliminando proveedor {} del artículo: {}", idProveedor, idArticulo);

        ArticuloProveedor articuloProveedor = articuloProveedorRepository
                .findByArticulo_IdArticuloAndProveedor_IdProveedor(idArticulo, idProveedor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado para artículo " + idArticulo));

        articuloProveedorRepository.delete(articuloProveedor);
        log.info("Proveedor eliminado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorArticuloResponse> getProveedoresByArticulo(Integer idArticulo) {
        log.debug("Obteniendo proveedores del artículo: {}", idArticulo);

        if (!articuloRepository.existsById(idArticulo)) {
            throw new ResourceNotFoundException("Artículo", "id", idArticulo);
        }

        return articuloProveedorRepository.findByArticulo_IdArticulo(idArticulo).stream()
                .map(articuloMapper::toProveedorArticuloResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProveedorArticuloResponse setProveedorPredeterminado(Integer idArticulo, Integer idProveedor) {
        log.info("Estableciendo proveedor {} como predeterminado para artículo: {}", idProveedor, idArticulo);

        // Quitar predeterminado actual
        articuloProveedorRepository.findProveedorPredeterminadoByArticulo(idArticulo)
                .ifPresent(ap -> {
                    ap.setEsPredeterminado(false);
                    articuloProveedorRepository.save(ap);
                });

        // Establecer nuevo predeterminado
        ArticuloProveedor articuloProveedor = articuloProveedorRepository
                .findByArticulo_IdArticuloAndProveedor_IdProveedor(idArticulo, idProveedor)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado para artículo " + idArticulo));

        articuloProveedor.setEsPredeterminado(true);
        articuloProveedor = articuloProveedorRepository.save(articuloProveedor);

        log.info("Proveedor predeterminado establecido exitosamente");
        return articuloMapper.toProveedorArticuloResponse(articuloProveedor);
    }

    // Método auxiliar para agregar proveedor
    private void agregarProveedor(Articulo articulo, ProveedorArticuloRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.idProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", "id", request.idProveedor()));

        ArticuloProveedor articuloProveedor = new ArticuloProveedor();
        articuloProveedor.setArticulo(articulo);
        articuloProveedor.setProveedor(proveedor);
        articuloProveedor.setCosto(request.costo());
        articuloProveedor.setEsPredeterminado(request.esPredeterminado());

        if (articulo.getProveedores() == null) {
            articulo.setProveedores(new HashSet<>());
        }
        articulo.getProveedores().add(articuloProveedor);
    }

    // ========== Gestión de Stock ==========

    @Override
    @Transactional
    public ArticuloResponse ajustarStock(Integer id, AjusteStockRequest request) {
        log.info("Ajustando stock del artículo: {} - Tipo: {}", id, request.tipoAjuste());

        Articulo articulo = articuloRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo", "id", id));

        if (!articulo.getUsaControlStock()) {
            throw new BusinessException("El artículo no tiene control de stock habilitado");
        }

        BigDecimal stockActual = articulo.getStockActual();
        BigDecimal nuevoStock;

        switch (request.tipoAjuste().toUpperCase()) {
            case "INGRESO":
                nuevoStock = stockActual.add(request.cantidad());
                break;
            case "EGRESO":
                nuevoStock = stockActual.subtract(request.cantidad());
                if (nuevoStock.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessException("No hay suficiente stock disponible");
                }
                break;
            case "AJUSTE":
                nuevoStock = request.cantidad();
                break;
            default:
                throw new BusinessException("Tipo de ajuste inválido: " + request.tipoAjuste());
        }

        articulo.setStockActual(nuevoStock);
        articulo = articuloRepository.save(articulo);

        log.info("Stock ajustado exitosamente. Nuevo stock: {}", nuevoStock);
        return articuloMapper.toResponse(articulo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloStockBajoResponse> getArticulosConStockBajo() {
        log.debug("Obteniendo artículos con stock bajo");
        return articuloRepository.findArticulosConStockBajo().stream()
                .map(articuloMapper::toStockBajoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ArticuloResponse incrementarStock(Integer id, BigDecimal cantidad) {
        log.info("Incrementando stock del artículo: {} en {}", id, cantidad);

        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a cero");
        }

        AjusteStockRequest request = new AjusteStockRequest(cantidad, "INGRESO", "Incremento de stock");

        return ajustarStock(id, request);
    }

    @Override
    @Transactional
    public ArticuloResponse decrementarStock(Integer id, BigDecimal cantidad) {
        log.info("Decrementando stock del artículo: {} en {}", id, cantidad);

        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("La cantidad debe ser mayor a cero");
        }

        AjusteStockRequest request = new AjusteStockRequest(cantidad, "EGRESO", "Decremento de stock");

        return ajustarStock(id, request);
    }

    // ========== Gestión de Vencimientos ==========

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloVencimientoResponse> getArticulosProximosAVencer(Integer dias) {
        log.debug("Obteniendo artículos próximos a vencer en {} días", dias);

        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(dias);

        return articuloRepository.findArticulosProximosAVencer(hoy, fechaLimite).stream()
                .map(articuloMapper::toVencimientoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloVencimientoResponse> getArticulosVencidos() {
        log.debug("Obteniendo artículos vencidos");

        LocalDate hoy = LocalDate.now();
        LocalDate fechaInicio = LocalDate.of(2000, 1, 1); // Fecha muy antigua

        return articuloRepository.findArticulosProximosAVencer(fechaInicio, hoy).stream()
                .filter(a -> a.getFechaVencimiento() != null && a.getFechaVencimiento().isBefore(hoy))
                .map(articuloMapper::toVencimientoResponse)
                .collect(Collectors.toList());
    }

    // ========== Utilidades ==========

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodigoBarras(String codigoBarras) {
        return articuloRepository.existsByCodigoBarras(codigoBarras);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloSimpleResponse> getArticulosEnOferta() {
        log.debug("Obteniendo artículos en oferta");
        return articuloRepository.findByEnOferta(true).stream()
                .map(articuloMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticuloSimpleResponse> getArticulosParaWeb() {
        log.debug("Obteniendo artículos para web");
        return articuloRepository.findByPublicarEnWeb(true).stream()
                .map(articuloMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByRubro(Integer idRubro) {
        log.debug("Contando artículos del rubro: {}", idRubro);

        if (!rubroRepository.existsById(idRubro)) {
            throw new ResourceNotFoundException("Rubro", "id", idRubro);
        }

        return articuloRepository.findByRubro_IdRubro(idRubro).size();
    }
}