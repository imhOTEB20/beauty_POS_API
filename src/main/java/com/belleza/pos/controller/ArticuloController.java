package com.belleza.pos.controller;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.service.ArticuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para gestión de artículos/productos
 */
@Tag(name = "Artículos", description = "Endpoints para gestión de artículos/productos")
@RestController
@RequestMapping("/articulos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ArticuloController {

    private final ArticuloService articuloService;

    // ========== CRUD Básico ==========

    /**
     * Crear un nuevo artículo
     */
    @Operation(summary = "Crear artículo", description = "Crea un nuevo artículo/producto en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> create(@Valid @RequestBody CreateArticuloRequest request) {
        ArticuloResponse response = articuloService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar un artículo existente
     */
    @Operation(summary = "Actualizar artículo", description = "Actualiza los datos de un artículo existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> update(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Valid @RequestBody UpdateArticuloRequest request) {
        ArticuloResponse response = articuloService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículo por ID
     */
    @Operation(summary = "Obtener artículo por ID", description = "Obtiene los detalles completos de un artículo")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ArticuloResponse> getById(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        ArticuloResponse response = articuloService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículo por código de barras
     */
    @Operation(summary = "Obtener artículo por código de barras", description = "Busca un artículo por su código de barras")
    @GetMapping("/codigo/{codigoBarras}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ArticuloResponse> getByCodigoBarras(
            @Parameter(description = "Código de barras") @PathVariable String codigoBarras) {
        ArticuloResponse response = articuloService.getByCodigoBarras(codigoBarras);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los artículos con paginación
     */
    @Operation(summary = "Listar artículos", description = "Obtiene todos los artículos con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<ArticuloResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "descripcion") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ArticuloResponse> response = articuloService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los artículos activos
     */
    @Operation(summary = "Listar artículos activos", description = "Obtiene todos los artículos activos (listado simple)")
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<ArticuloSimpleResponse>> getAllActive() {
        List<ArticuloSimpleResponse> response = articuloService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar artículos
     */
    @Operation(summary = "Buscar artículos", description = "Busca artículos por código de barras o descripción")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Page<ArticuloResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticuloResponse> response = articuloService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículos por rubro
     */
    @Operation(summary = "Listar por rubro", description = "Obtiene artículos de un rubro específico")
    @GetMapping("/rubro/{idRubro}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<ArticuloResponse>> getByRubro(
            @Parameter(description = "ID del rubro") @PathVariable Integer idRubro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ArticuloResponse> response = articuloService.getByRubro(idRubro, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Activar un artículo
     */
    @Operation(summary = "Activar artículo", description = "Activa un artículo desactivado")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> activate(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        ArticuloResponse response = articuloService.activate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar un artículo
     */
    @Operation(summary = "Desactivar artículo", description = "Desactiva un artículo sin eliminarlo")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> deactivate(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        ArticuloResponse response = articuloService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un artículo (soft delete)
     */
    @Operation(summary = "Eliminar artículo", description = "Elimina un artículo (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        articuloService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Artículo eliminado exitosamente"));
    }

    /**
     * Eliminar un artículo permanentemente
     */
    @Operation(summary = "Eliminar permanentemente", description = "Elimina un artículo de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        articuloService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Artículo eliminado permanentemente"));
    }

    // ========== Gestión de Precios ==========

    /**
     * Agregar o actualizar precio
     */
    @Operation(summary = "Agregar/Actualizar precio", description = "Agrega o actualiza el precio de un artículo en una lista")
    @PostMapping("/{id}/precios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PrecioResponse> addOrUpdatePrecio(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Valid @RequestBody PrecioRequest request) {
        PrecioResponse response = articuloService.addOrUpdatePrecio(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar precio
     */
    @Operation(summary = "Eliminar precio", description = "Elimina el precio de un artículo en una lista específica")
    @DeleteMapping("/{id}/precios/{idLista}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> removePrecio(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Parameter(description = "ID de la lista") @PathVariable Integer idLista) {
        articuloService.removePrecio(id, idLista);
        return ResponseEntity.ok(new MessageResponse("Precio eliminado exitosamente"));
    }

    /**
     * Obtener precios de un artículo
     */
    @Operation(summary = "Obtener precios", description = "Obtiene todos los precios de un artículo")
    @GetMapping("/{id}/precios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PrecioResponse>> getPreciosByArticulo(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        List<PrecioResponse> response = articuloService.getPreciosByArticulo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualizar múltiples precios
     */
    @Operation(summary = "Actualizar múltiples precios", description = "Actualiza todos los precios de un artículo")
    @PutMapping("/{id}/precios")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<PrecioResponse>> updatePrecios(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Valid @RequestBody List<PrecioRequest> precios) {
        List<PrecioResponse> response = articuloService.updatePrecios(id, precios);
        return ResponseEntity.ok(response);
    }

    // ========== Gestión de Proveedores ==========

    /**
     * Agregar proveedor
     */
    @Operation(summary = "Agregar proveedor", description = "Asocia un proveedor a un artículo")
    @PostMapping("/{id}/proveedores")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorArticuloResponse> addProveedor(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Valid @RequestBody ProveedorArticuloRequest request) {
        ProveedorArticuloResponse response = articuloService.addProveedor(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar proveedor
     */
    @Operation(summary = "Eliminar proveedor", description = "Desasocia un proveedor de un artículo")
    @DeleteMapping("/{id}/proveedores/{idProveedor}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> removeProveedor(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Parameter(description = "ID del proveedor") @PathVariable Integer idProveedor) {
        articuloService.removeProveedor(id, idProveedor);
        return ResponseEntity.ok(new MessageResponse("Proveedor eliminado exitosamente"));
    }

    /**
     * Obtener proveedores de un artículo
     */
    @Operation(summary = "Obtener proveedores", description = "Obtiene todos los proveedores de un artículo")
    @GetMapping("/{id}/proveedores")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ProveedorArticuloResponse>> getProveedoresByArticulo(
            @Parameter(description = "ID del artículo") @PathVariable Integer id) {
        List<ProveedorArticuloResponse> response = articuloService.getProveedoresByArticulo(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Establecer proveedor predeterminado
     */
    @Operation(summary = "Establecer proveedor predeterminado", description = "Marca un proveedor como predeterminado")
    @PatchMapping("/{id}/proveedores/{idProveedor}/predeterminado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorArticuloResponse> setProveedorPredeterminado(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Parameter(description = "ID del proveedor") @PathVariable Integer idProveedor) {
        ProveedorArticuloResponse response = articuloService.setProveedorPredeterminado(id, idProveedor);
        return ResponseEntity.ok(response);
    }

    // ========== Gestión de Stock ==========

    /**
     * Ajustar stock
     */
    @Operation(summary = "Ajustar stock", description = "Realiza un ajuste de stock (ingreso, egreso o ajuste)")
    @PostMapping("/{id}/stock/ajustar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> ajustarStock(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Valid @RequestBody AjusteStockRequest request) {
        ArticuloResponse response = articuloService.ajustarStock(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Incrementar stock
     */
    @Operation(summary = "Incrementar stock", description = "Incrementa el stock de un artículo")
    @PostMapping("/{id}/stock/incrementar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> incrementarStock(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Parameter(description = "Cantidad a incrementar") @RequestParam BigDecimal cantidad) {
        ArticuloResponse response = articuloService.incrementarStock(id, cantidad);
        return ResponseEntity.ok(response);
    }

    /**
     * Decrementar stock
     */
    @Operation(summary = "Decrementar stock", description = "Decrementa el stock de un artículo")
    @PostMapping("/{id}/stock/decrementar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ArticuloResponse> decrementarStock(
            @Parameter(description = "ID del artículo") @PathVariable Integer id,
            @Parameter(description = "Cantidad a decrementar") @RequestParam BigDecimal cantidad) {
        ArticuloResponse response = articuloService.decrementarStock(id, cantidad);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículos con stock bajo
     */
    @Operation(summary = "Artículos con stock bajo", description = "Obtiene artículos que tienen stock por debajo del mínimo")
    @GetMapping("/stock/bajo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ArticuloStockBajoResponse>> getArticulosConStockBajo() {
        List<ArticuloStockBajoResponse> response = articuloService.getArticulosConStockBajo();
        return ResponseEntity.ok(response);
    }

    // ========== Gestión de Vencimientos ==========

    /**
     * Obtener artículos próximos a vencer
     */
    @Operation(summary = "Artículos próximos a vencer", description = "Obtiene artículos que vencen en X días")
    @GetMapping("/vencimientos/proximos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ArticuloVencimientoResponse>> getArticulosProximosAVencer(
            @Parameter(description = "Días hacia adelante") @RequestParam(defaultValue = "30") Integer dias) {
        List<ArticuloVencimientoResponse> response = articuloService.getArticulosProximosAVencer(dias);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículos vencidos
     */
    @Operation(summary = "Artículos vencidos", description = "Obtiene artículos con fecha de vencimiento pasada")
    @GetMapping("/vencimientos/vencidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ArticuloVencimientoResponse>> getArticulosVencidos() {
        List<ArticuloVencimientoResponse> response = articuloService.getArticulosVencidos();
        return ResponseEntity.ok(response);
    }

    // ========== Utilidades ==========

    /**
     * Verificar código de barras
     */
    @Operation(summary = "Verificar código de barras", description = "Verifica si un código de barras ya existe")
    @GetMapping("/verificar/codigo/{codigoBarras}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkCodigoBarras(
            @Parameter(description = "Código de barras") @PathVariable String codigoBarras) {
        boolean exists = articuloService.existsByCodigoBarras(codigoBarras);
        String message = exists ? "Código de barras ya existe" : "Código de barras disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Obtener artículos en oferta
     */
    @Operation(summary = "Artículos en oferta", description = "Obtiene artículos marcados como oferta")
    @GetMapping("/ofertas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<ArticuloSimpleResponse>> getArticulosEnOferta() {
        List<ArticuloSimpleResponse> response = articuloService.getArticulosEnOferta();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener artículos para web
     */
    @Operation(summary = "Artículos para web", description = "Obtiene artículos publicados en la web")
    @GetMapping("/web")
    public ResponseEntity<List<ArticuloSimpleResponse>> getArticulosParaWeb() {
        List<ArticuloSimpleResponse> response = articuloService.getArticulosParaWeb();
        return ResponseEntity.ok(response);
    }

    /**
     * Contar artículos por rubro
     */
    @Operation(summary = "Contar por rubro", description = "Obtiene el número de artículos en un rubro")
    @GetMapping("/rubro/{idRubro}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countByRubro(
            @Parameter(description = "ID del rubro") @PathVariable Integer idRubro) {
        long count = articuloService.countByRubro(idRubro);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Número de artículos en el rubro")
                .data(count)
                .build());
    }
}