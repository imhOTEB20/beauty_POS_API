package com.belleza.pos.service;

import com.belleza.pos.dto.request.AjusteStockRequest;
import com.belleza.pos.dto.request.CreateArticuloRequest;
import com.belleza.pos.dto.request.PrecioRequest;
import com.belleza.pos.dto.request.ProveedorArticuloRequest;
import com.belleza.pos.dto.request.UpdateArticuloRequest;
import com.belleza.pos.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interfaz del servicio de Artículo
 */
public interface ArticuloService {

    // ========== CRUD Básico ==========

    /**
     * Crea un nuevo artículo
     */
    ArticuloResponse create(CreateArticuloRequest request);

    /**
     * Actualiza un artículo existente
     */
    ArticuloResponse update(Integer id, UpdateArticuloRequest request);

    /**
     * Obtiene un artículo por ID
     */
    ArticuloResponse getById(Integer id);

    /**
     * Obtiene un artículo por código de barras
     */
    ArticuloResponse getByCodigoBarras(String codigoBarras);

    /**
     * Obtiene todos los artículos con paginación
     */
    Page<ArticuloResponse> getAll(Pageable pageable);

    /**
     * Obtiene todos los artículos activos
     */
    List<ArticuloSimpleResponse> getAllActive();

    /**
     * Busca artículos por término de búsqueda
     */
    Page<ArticuloResponse> search(String searchTerm, Pageable pageable);

    /**
     * Obtiene artículos por rubro
     */
    Page<ArticuloResponse> getByRubro(Integer idRubro, Pageable pageable);

    /**
     * Activa un artículo
     */
    ArticuloResponse activate(Integer id);

    /**
     * Desactiva un artículo
     */
    ArticuloResponse deactivate(Integer id);

    /**
     * Elimina un artículo (soft delete)
     */
    void delete(Integer id);

    /**
     * Elimina un artículo permanentemente
     */
    void deletePermanently(Integer id);

    // ========== Gestión de Precios ==========

    /**
     * Agrega o actualiza un precio para un artículo en una lista
     */
    PrecioResponse addOrUpdatePrecio(Integer idArticulo, PrecioRequest request);

    /**
     * Elimina un precio de un artículo en una lista específica
     */
    void removePrecio(Integer idArticulo, Integer idLista);

    /**
     * Obtiene todos los precios de un artículo
     */
    List<PrecioResponse> getPreciosByArticulo(Integer idArticulo);

    /**
     * Actualiza múltiples precios de un artículo
     */
    List<PrecioResponse> updatePrecios(Integer idArticulo, List<PrecioRequest> precios);

    // ========== Gestión de Proveedores ==========

    /**
     * Agrega un proveedor a un artículo
     */
    ProveedorArticuloResponse addProveedor(Integer idArticulo, ProveedorArticuloRequest request);

    /**
     * Elimina un proveedor de un artículo
     */
    void removeProveedor(Integer idArticulo, Integer idProveedor);

    /**
     * Obtiene todos los proveedores de un artículo
     */
    List<ProveedorArticuloResponse> getProveedoresByArticulo(Integer idArticulo);

    /**
     * Establece un proveedor como predeterminado
     */
    ProveedorArticuloResponse setProveedorPredeterminado(Integer idArticulo, Integer idProveedor);

    // ========== Gestión de Stock ==========

    /**
     * Ajusta el stock de un artículo
     */
    ArticuloResponse ajustarStock(Integer id, AjusteStockRequest request);

    /**
     * Obtiene artículos con stock bajo
     */
    List<ArticuloStockBajoResponse> getArticulosConStockBajo();

    /**
     * Incrementa el stock (entrada de mercadería)
     */
    ArticuloResponse incrementarStock(Integer id, java.math.BigDecimal cantidad);

    /**
     * Decrementa el stock (salida de mercadería)
     */
    ArticuloResponse decrementarStock(Integer id, java.math.BigDecimal cantidad);

    // ========== Gestión de Vencimientos ==========

    /**
     * Obtiene artículos próximos a vencer
     */
    List<ArticuloVencimientoResponse> getArticulosProximosAVencer(Integer dias);

    /**
     * Obtiene artículos vencidos
     */
    List<ArticuloVencimientoResponse> getArticulosVencidos();

    // ========== Utilidades ==========

    /**
     * Verifica si existe un código de barras
     */
    boolean existsByCodigoBarras(String codigoBarras);

    /**
     * Obtiene artículos en oferta
     */
    List<ArticuloSimpleResponse> getArticulosEnOferta();

    /**
     * Obtiene artículos publicados en web
     */
    List<ArticuloSimpleResponse> getArticulosParaWeb();

    /**
     * Cuenta artículos por rubro
     */
    long countByRubro(Integer idRubro);
}
