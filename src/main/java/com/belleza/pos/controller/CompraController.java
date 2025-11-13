package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateCompraRequest;
import com.belleza.pos.dto.response.CompraResponse;
import com.belleza.pos.dto.response.CompraSimpleResponse;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.service.CompraService;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para gestión de compras
 */
@Tag(name = "Compras", description = "Endpoints para gestión de compras a proveedores")
@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CompraController {

    private final CompraService compraService;

    // ========== CRUD Básico ==========

    /**
     * Crear una nueva compra
     */
    @Operation(summary = "Crear compra", description = "Registra una nueva compra en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CompraResponse> create(@Valid @RequestBody CreateCompraRequest request) {
        CompraResponse response = compraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Obtener compra por ID
     */
    @Operation(summary = "Obtener compra por ID", description = "Obtiene los detalles completos de una compra")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CompraResponse> getById(
            @Parameter(description = "ID de la compra") @PathVariable Integer id) {
        CompraResponse response = compraService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compra por número de comprobante
     */
    @Operation(summary = "Obtener compra por número de comprobante", description = "Busca una compra por su número de comprobante")
    @GetMapping("/comprobante/{nroComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CompraResponse> getByNroComprobante(
            @Parameter(description = "Número de comprobante") @PathVariable String nroComprobante) {
        CompraResponse response = compraService.getByNroComprobante(nroComprobante);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las compras con paginación
     */
    @Operation(summary = "Listar compras", description = "Obtiene todas las compras con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaCompra") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<CompraResponse> response = compraService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar compras
     */
    @Operation(summary = "Buscar compras", description = "Busca compras por número de comprobante o proveedor")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Anular una compra
     */
    @Operation(summary = "Anular compra", description = "Anula una compra y restaura el stock")
    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CompraResponse> anular(
            @Parameter(description = "ID de la compra") @PathVariable Integer id,
            @Parameter(description = "Motivo de anulación") @RequestParam String motivo) {
        CompraResponse response = compraService.anular(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una compra permanentemente
     */
    @Operation(summary = "Eliminar compra", description = "Elimina una compra de forma permanente")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID de la compra") @PathVariable Integer id) {
        compraService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Compra eliminada exitosamente"));
    }

    // ========== Consultas por Estado ==========

    /**
     * Obtener compras por estado
     */
    @Operation(summary = "Listar por estado", description = "Obtiene compras filtradas por estado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getByEstado(
            @Parameter(description = "Estado de la compra") @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getByEstado(estado, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compras completadas
     */
    @Operation(summary = "Compras completadas", description = "Obtiene todas las compras completadas")
    @GetMapping("/completadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getComprasCompletadas() {
        List<CompraSimpleResponse> response = compraService.getComprasCompletadas();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compras pendientes
     */
    @Operation(summary = "Compras pendientes", description = "Obtiene todas las compras pendientes")
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getComprasPendientes() {
        List<CompraSimpleResponse> response = compraService.getComprasPendientes();
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Proveedor ==========

    /**
     * Obtener compras de un proveedor
     */
    @Operation(summary = "Listar por proveedor", description = "Obtiene las compras de un proveedor específico")
    @GetMapping("/proveedor/{idProveedor}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getByProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Integer idProveedor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getByProveedor(idProveedor, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener historial de compras de un proveedor
     */
    @Operation(summary = "Historial del proveedor", description = "Obtiene el historial completo de compras de un proveedor")
    @GetMapping("/proveedor/{idProveedor}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getHistorialProveedor(
            @Parameter(description = "ID del proveedor") @PathVariable Integer idProveedor) {
        List<CompraSimpleResponse> response = compraService.getHistorialProveedor(idProveedor);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Sucursal ==========

    /**
     * Obtener compras de una sucursal
     */
    @Operation(summary = "Listar por sucursal", description = "Obtiene las compras de una sucursal específica")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getBySucursal(idSucursal, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compras del día de una sucursal
     */
    @Operation(summary = "Compras del día por sucursal", description = "Obtiene las compras del día de una sucursal")
    @GetMapping("/sucursal/{idSucursal}/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getComprasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<CompraSimpleResponse> response = compraService.getComprasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Usuario ==========

    /**
     * Obtener compras de un usuario
     */
    @Operation(summary = "Listar por usuario", description = "Obtiene las compras realizadas por un usuario")
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getByUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getByUsuario(idUsuario, pageable);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Tipo de Comprobante ==========

    /**
     * Obtener compras por tipo de comprobante
     */
    @Operation(summary = "Listar por tipo de comprobante", description = "Obtiene compras filtradas por tipo de comprobante")
    @GetMapping("/tipo-comprobante/{tipoComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getByTipoComprobante(
            @Parameter(description = "Tipo de comprobante") @PathVariable String tipoComprobante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getByTipoComprobante(tipoComprobante, pageable);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Fecha ==========

    /**
     * Obtener compras del día
     */
    @Operation(summary = "Compras del día", description = "Obtiene todas las compras del día actual")
    @GetMapping("/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getComprasDelDia() {
        List<CompraSimpleResponse> response = compraService.getComprasDelDia();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compras entre fechas
     */
    @Operation(summary = "Listar por período", description = "Obtiene compras en un rango de fechas")
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CompraResponse>> getByFechaCompraBetween(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaCompra"));
        Page<CompraResponse> response = compraService.getByFechaCompraBetween(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener compras de un período (sin paginación)
     */
    @Operation(summary = "Compras por período simple", description = "Obtiene compras de un período sin paginación")
    @GetMapping("/periodo/simple")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<CompraSimpleResponse>> getComprasByPeriodo(
            @Parameter(description = "Fecha inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<CompraSimpleResponse> response = compraService.getComprasByPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ========== Estadísticas y Totales ==========

    /**
     * Obtener total de compras del día
     */
    @Operation(summary = "Total compras del día", description = "Obtiene el monto total de compras del día")
    @GetMapping("/totales/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> getTotalComprasDelDia() {
        BigDecimal total = compraService.getTotalComprasDelDia();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de compras del día")
                .data(total)
                .build());
    }

    /**
     * Obtener total de compras del día por sucursal
     */
    @Operation(summary = "Total compras del día por sucursal", description = "Obtiene el monto total de compras del día en una sucursal")
    @GetMapping("/totales/dia/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> getTotalComprasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        BigDecimal total = compraService.getTotalComprasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de compras del día de la sucursal")
                .data(total)
                .build());
    }

    /**
     * Obtener total de compras por período
     */
    @Operation(summary = "Total compras por período", description = "Obtiene el monto total de compras en un período")
    @GetMapping("/totales/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> getTotalComprasByPeriodo(
            @Parameter(description = "Fecha inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = compraService.getTotalComprasByPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de compras del período")
                .data(total)
                .build());
    }

    /**
     * Contar compras del día
     */
    @Operation(summary = "Contar compras del día", description = "Obtiene la cantidad de compras del día")
    @GetMapping("/count/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countComprasDelDia() {
        Long count = compraService.countComprasDelDia();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de compras del día")
                .data(count)
                .build());
    }

    /**
     * Contar compras del día por sucursal
     */
    @Operation(summary = "Contar compras del día por sucursal", description = "Obtiene la cantidad de compras del día en una sucursal")
    @GetMapping("/count/dia/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countComprasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        Long count = compraService.countComprasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de compras del día de la sucursal")
                .data(count)
                .build());
    }

    // ========== Utilidades ==========

    /**
     * Verificar número de comprobante
     */
    @Operation(summary = "Verificar número de comprobante", description = "Verifica si un número de comprobante ya existe")
    @GetMapping("/verificar/comprobante/{nroComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNroComprobante(
            @Parameter(description = "Número de comprobante") @PathVariable String nroComprobante) {
        boolean exists = compraService.existsByNroComprobante(nroComprobante);
        String message = exists ? "Número de comprobante ya existe" : "Número de comprobante disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}