package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateVentaRequest;
import com.belleza.pos.dto.request.UpdateVentaRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.VentaResponse;
import com.belleza.pos.dto.response.VentaSimpleResponse;
import com.belleza.pos.service.VentaService;
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
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de ventas
 */
@Tag(name = "Ventas", description = "Endpoints para gestión de ventas y facturación")
@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class VentaController {

    private final VentaService ventaService;

    // ========== CRUD Básico ==========

    /**
     * Crear una nueva venta
     */
    @Operation(summary = "Crear venta", description = "Registra una nueva venta en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<VentaResponse> create(@Valid @RequestBody CreateVentaRequest request) {
        VentaResponse response = ventaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar una venta existente
     */
    @Operation(summary = "Actualizar venta", description = "Actualiza los datos de una venta existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<VentaResponse> update(
            @Parameter(description = "ID de la venta") @PathVariable Integer id,
            @Valid @RequestBody UpdateVentaRequest request) {
        VentaResponse response = ventaService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener venta por ID
     */
    @Operation(summary = "Obtener venta por ID", description = "Obtiene los detalles completos de una venta")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<VentaResponse> getById(
            @Parameter(description = "ID de la venta") @PathVariable Integer id) {
        VentaResponse response = ventaService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener venta por número de transacción
     */
    @Operation(summary = "Obtener venta por número de transacción", description = "Busca una venta por su número de transacción")
    @GetMapping("/transaccion/{nroTransaccion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<VentaResponse> getByNroTransaccion(
            @Parameter(description = "Número de transacción") @PathVariable String nroTransaccion) {
        VentaResponse response = ventaService.getByNroTransaccion(nroTransaccion);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las ventas con paginación
     */
    @Operation(summary = "Listar ventas", description = "Obtiene todas las ventas con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<VentaResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaVenta") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<VentaResponse> response = ventaService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar ventas
     */
    @Operation(summary = "Buscar ventas", description = "Busca ventas por número de transacción o comprobante")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Page<VentaResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Anular una venta
     */
    @Operation(summary = "Anular venta", description = "Anula una venta y restaura el stock")
    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<VentaResponse> anular(
            @Parameter(description = "ID de la venta") @PathVariable Integer id,
            @Parameter(description = "Motivo de anulación") @RequestParam String motivo) {
        VentaResponse response = ventaService.anular(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una venta permanentemente
     */
    @Operation(summary = "Eliminar venta", description = "Elimina una venta de forma permanente")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID de la venta") @PathVariable Integer id) {
        ventaService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Venta eliminada exitosamente"));
    }

    // ========== Consultas por Estado ==========

    /**
     * Obtener ventas por estado
     */
    @Operation(summary = "Listar por estado", description = "Obtiene ventas filtradas por estado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<VentaResponse>> getByEstado(
            @Parameter(description = "Estado de la venta") @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getByEstado(estado, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener ventas completadas
     */
    @Operation(summary = "Ventas completadas", description = "Obtiene todas las ventas completadas")
    @GetMapping("/completadas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<VentaSimpleResponse>> getVentasCompletadas() {
        List<VentaSimpleResponse> response = ventaService.getVentasCompletadas();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener ventas pendientes
     */
    @Operation(summary = "Ventas pendientes", description = "Obtiene todas las ventas pendientes")
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<VentaSimpleResponse>> getVentasPendientes() {
        List<VentaSimpleResponse> response = ventaService.getVentasPendientes();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos
     */
    @Operation(summary = "Presupuestos", description = "Obtiene todos los presupuestos")
    @GetMapping("/presupuestos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<VentaSimpleResponse>> getPresupuestos() {
        List<VentaSimpleResponse> response = ventaService.getPresupuestos();
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Cliente ==========

    /**
     * Obtener ventas de un cliente
     */
    @Operation(summary = "Listar por cliente", description = "Obtiene las ventas de un cliente específico")
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<VentaResponse>> getByCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getByCliente(idCliente, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener historial de compras de un cliente
     */
    @Operation(summary = "Historial del cliente", description = "Obtiene el historial completo de compras de un cliente")
    @GetMapping("/cliente/{idCliente}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<VentaSimpleResponse>> getHistorialCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente) {
        List<VentaSimpleResponse> response = ventaService.getHistorialCliente(idCliente);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Sucursal ==========

    /**
     * Obtener ventas de una sucursal
     */
    @Operation(summary = "Listar por sucursal", description = "Obtiene las ventas de una sucursal específica")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<VentaResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getBySucursal(idSucursal, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener ventas del día de una sucursal
     */
    @Operation(summary = "Ventas del día por sucursal", description = "Obtiene las ventas del día de una sucursal")
    @GetMapping("/sucursal/{idSucursal}/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<VentaSimpleResponse>> getVentasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<VentaSimpleResponse> response = ventaService.getVentasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Usuario ==========

    /**
     * Obtener ventas de un usuario
     */
    @Operation(summary = "Listar por usuario", description = "Obtiene las ventas realizadas por un usuario")
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<VentaResponse>> getByUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getByUsuario(idUsuario, pageable);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Tipo de Comprobante ==========

    /**
     * Obtener ventas por tipo de comprobante
     */
    @Operation(summary = "Listar por tipo de comprobante", description = "Obtiene ventas filtradas por tipo de comprobante")
    @GetMapping("/tipo-comprobante/{tipoComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<VentaResponse>> getByTipoComprobante(
            @Parameter(description = "Tipo de comprobante") @PathVariable String tipoComprobante,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getByTipoComprobante(tipoComprobante, pageable);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Fecha ==========

    /**
     * Obtener ventas del día
     */
    @Operation(summary = "Ventas del día", description = "Obtiene todas las ventas del día actual")
    @GetMapping("/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<VentaSimpleResponse>> getVentasDelDia() {
        List<VentaSimpleResponse> response = ventaService.getVentasDelDia();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener ventas entre fechas
     */
    @Operation(summary = "Listar por período", description = "Obtiene ventas en un rango de fechas")
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<VentaResponse>> getByFechaVentaBetween(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaVenta"));
        Page<VentaResponse> response = ventaService.getByFechaVentaBetween(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener ventas de un período (sin paginación)
     */
    @Operation(summary = "Ventas por período simple", description = "Obtiene ventas de un período sin paginación")
    @GetMapping("/periodo/simple")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<VentaSimpleResponse>> getVentasByPeriodo(
            @Parameter(description = "Fecha inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<VentaSimpleResponse> response = ventaService.getVentasByPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ========== Estadísticas y Totales ==========

    /**
     * Obtener total de ventas del día
     */
    @Operation(summary = "Total ventas del día", description = "Obtiene el monto total vendido en el día")
    @GetMapping("/totales/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> getTotalVentasDelDia() {
        BigDecimal total = ventaService.getTotalVentasDelDia();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de ventas del día")
                .data(total)
                .build());
    }

    /**
     * Obtener total de ventas del día por sucursal
     */
    @Operation(summary = "Total ventas del día por sucursal", description = "Obtiene el monto total vendido en el día en una sucursal")
    @GetMapping("/totales/dia/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> getTotalVentasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        BigDecimal total = ventaService.getTotalVentasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de ventas del día de la sucursal")
                .data(total)
                .build());
    }

    /**
     * Obtener total de ventas por período
     */
    @Operation(summary = "Total ventas por período", description = "Obtiene el monto total vendido en un período")
    @GetMapping("/totales/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> getTotalVentasByPeriodo(
            @Parameter(description = "Fecha inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @Parameter(description = "Fecha fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        BigDecimal total = ventaService.getTotalVentasByPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de ventas del período")
                .data(total)
                .build());
    }

    /**
     * Contar ventas del día
     */
    @Operation(summary = "Contar ventas del día", description = "Obtiene la cantidad de ventas del día")
    @GetMapping("/count/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> countVentasDelDia() {
        Long count = ventaService.countVentasDelDia();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de ventas del día")
                .data(count)
                .build());
    }

    /**
     * Contar ventas del día por sucursal
     */
    @Operation(summary = "Contar ventas del día por sucursal", description = "Obtiene la cantidad de ventas del día en una sucursal")
    @GetMapping("/count/dia/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> countVentasDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        Long count = ventaService.countVentasDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de ventas del día de la sucursal")
                .data(count)
                .build());
    }

    // ========== Utilidades ==========

    /**
     * Verificar número de transacción
     */
    @Operation(summary = "Verificar número de transacción", description = "Verifica si un número de transacción ya existe")
    @GetMapping("/verificar/transaccion/{nroTransaccion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> checkNroTransaccion(
            @Parameter(description = "Número de transacción") @PathVariable String nroTransaccion) {
        boolean exists = ventaService.existsByNroTransaccion(nroTransaccion);
        String message = exists ? "Número de transacción ya existe" : "Número de transacción disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Generar número de transacción
     */
    @Operation(summary = "Generar número de transacción", description = "Genera un número de transacción único")
    @GetMapping("/generar/transaccion")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> generarNroTransaccion() {
        String nroTransaccion = ventaService.generarNroTransaccion();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Número de transacción generado")
                .data(nroTransaccion)
                .build());
    }
}
