package com.belleza.pos.controller;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.service.PresupuestoService;
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
 * Controlador REST para gestión de presupuestos
 */
@Tag(name = "Presupuestos", description = "Endpoints para gestión de presupuestos")
@RestController
@RequestMapping("/presupuestos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class PresupuestoController {

    private final PresupuestoService presupuestoService;

    // ========== CRUD Básico ==========

    /**
     * Crear un nuevo presupuesto
     */
    @Operation(summary = "Crear presupuesto", description = "Crea un nuevo presupuesto en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> create(@Valid @RequestBody CreatePresupuestoRequest request) {
        PresupuestoResponse response = presupuestoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar un presupuesto existente
     */
    @Operation(summary = "Actualizar presupuesto", description = "Actualiza los datos de un presupuesto existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> update(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id,
            @Valid @RequestBody UpdatePresupuestoRequest request) {
        PresupuestoResponse response = presupuestoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuesto por ID
     */
    @Operation(summary = "Obtener presupuesto por ID", description = "Obtiene los detalles completos de un presupuesto")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> getById(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        PresupuestoResponse response = presupuestoService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuesto por número
     */
    @Operation(summary = "Obtener por número", description = "Busca un presupuesto por su número")
    @GetMapping("/numero/{nroPresupuesto}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> getByNroPresupuesto(
            @Parameter(description = "Número de presupuesto") @PathVariable String nroPresupuesto) {
        PresupuestoResponse response = presupuestoService.getByNroPresupuesto(nroPresupuesto);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los presupuestos con paginación
     */
    @Operation(summary = "Listar presupuestos", description = "Obtiene todos los presupuestos con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fechaPresupuesto") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<PresupuestoResponse> response = presupuestoService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los presupuestos pendientes
     */
    @Operation(summary = "Listar pendientes", description = "Obtiene todos los presupuestos pendientes")
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getAllPendientes() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getAllPendientes();
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar presupuestos
     */
    @Operation(summary = "Buscar presupuestos", description = "Busca presupuestos por número o cliente")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PresupuestoResponse> response = presupuestoService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos por cliente
     */
    @Operation(summary = "Listar por cliente", description = "Obtiene presupuestos de un cliente específico")
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> getByCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getByCliente(idCliente, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos por sucursal
     */
    @Operation(summary = "Listar por sucursal", description = "Obtiene presupuestos de una sucursal")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<PresupuestoResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getBySucursal(idSucursal, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos por usuario
     */
    @Operation(summary = "Listar por usuario", description = "Obtiene presupuestos creados por un usuario")
    @GetMapping("/usuario/{idUsuario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<PresupuestoResponse>> getByUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Integer idUsuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getByUsuario(idUsuario, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos por estado
     */
    @Operation(summary = "Listar por estado", description = "Obtiene presupuestos por estado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> getByEstado(
            @Parameter(description = "Estado") @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getByEstado(estado, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos entre fechas
     */
    @Operation(summary = "Listar por período", description = "Obtiene presupuestos entre fechas")
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoResponse>> getByPeriodo(
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PresupuestoResponse> response = presupuestoService.getByFechaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un presupuesto permanentemente
     */
    @Operation(summary = "Eliminar permanentemente", description = "Elimina un presupuesto de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        presupuestoService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Presupuesto eliminado permanentemente"));
    }

    // ========== Gestión de Estados ==========

    /**
     * Aprobar un presupuesto
     */
    @Operation(summary = "Aprobar presupuesto", description = "Aprueba un presupuesto pendiente")
    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PresupuestoResponse> aprobar(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id,
            @Valid @RequestBody AprobarPresupuestoRequest request) {
        PresupuestoResponse response = presupuestoService.aprobar(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Rechazar un presupuesto
     */
    @Operation(summary = "Rechazar presupuesto", description = "Rechaza un presupuesto")
    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PresupuestoResponse> rechazar(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id,
            @Valid @RequestBody RechazarPresupuestoRequest request) {
        PresupuestoResponse response = presupuestoService.rechazar(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Convertir presupuesto en venta
     */
    @Operation(summary = "Convertir en venta", description = "Convierte un presupuesto en una venta")
    @PostMapping("/{id}/convertir-venta")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> convertirEnVenta(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id,
            @Valid @RequestBody ConvertirPresupuestoVentaRequest request) {
        PresupuestoResponse response = presupuestoService.convertirEnVenta(id, request);
        return ResponseEntity.ok(response);
    }

    // ========== Gestión de Detalles ==========

    /**
     * Obtener detalles de un presupuesto
     */
    @Operation(summary = "Obtener detalles", description = "Obtiene los detalles de un presupuesto")
    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoDetalleResponse>> getDetalles(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        List<PresupuestoDetalleResponse> response = presupuestoService.getDetalles(id);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas y Reportes ==========

    /**
     * Obtener presupuestos por cliente y período
     */
    @Operation(summary = "Listar por cliente y período", description = "Obtiene presupuestos de un cliente entre fechas")
    @GetMapping("/cliente/{idCliente}/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoResponse>> getByClienteAndPeriodo(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PresupuestoResponse> response = presupuestoService.getByClienteAndFechaBetween(
                idCliente, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos por sucursal y período
     */
    @Operation(summary = "Listar por sucursal y período", description = "Obtiene presupuestos de una sucursal entre fechas")
    @GetMapping("/sucursal/{idSucursal}/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<PresupuestoResponse>> getBySucursalAndPeriodo(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PresupuestoResponse> response = presupuestoService.getBySucursalAndFechaBetween(
                idSucursal, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos vencidos
     */
    @Operation(summary = "Listar vencidos", description = "Obtiene presupuestos vencidos")
    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosVencidos(
            @Parameter(description = "Días de validez") @RequestParam(defaultValue = "30") Integer diasValidez) {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosVencidos(diasValidez);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos vigentes
     */
    @Operation(summary = "Listar vigentes", description = "Obtiene presupuestos vigentes")
    @GetMapping("/vigentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosVigentes() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosVigentes();
        return ResponseEntity.ok(response);
    }

    /**
     * Contar presupuestos por estado
     */
    @Operation(summary = "Contar por estado", description = "Cuenta presupuestos por estado")
    @GetMapping("/reportes/contar/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countByEstado(
            @Parameter(description = "Estado") @PathVariable String estado) {
        Long count = presupuestoService.countByEstado(estado);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de presupuestos en estado " + estado)
                .data(count)
                .build());
    }

    /**
     * Calcular total por estado y período
     */
    @Operation(summary = "Total por estado y período", description = "Calcula el total de presupuestos por estado en un período")
    @GetMapping("/reportes/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> calcularTotal(
            @Parameter(description = "Estado") @RequestParam String estado,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = presupuestoService.calcularTotalPorEstadoYPeriodo(estado, fechaInicio, fechaFin);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de presupuestos en estado " + estado)
                .data(total)
                .build());
    }

    /**
     * Obtener estadísticas de presupuestos
     */
    @Operation(summary = "Estadísticas", description = "Obtiene estadísticas de presupuestos en un período")
    @GetMapping("/reportes/estadisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EstadisticasPresupuestoResponse> getEstadisticas(
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        EstadisticasPresupuestoResponse response = presupuestoService.getEstadisticas(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ========== Utilidades ==========

    /**
     * Verificar número de presupuesto
     */
    @Operation(summary = "Verificar número", description = "Verifica si un número de presupuesto ya existe")
    @GetMapping("/verificar/numero/{nroPresupuesto}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> checkNroPresupuesto(
            @Parameter(description = "Número de presupuesto") @PathVariable String nroPresupuesto) {
        boolean exists = presupuestoService.existsByNroPresupuesto(nroPresupuesto);
        String message = exists ? "Número de presupuesto ya existe" : "Número de presupuesto disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Obtener presupuestos del día
     */
    @Operation(summary = "Presupuestos del día", description = "Obtiene los presupuestos del día actual")
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosDelDia() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosDelDia();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos del mes
     */
    @Operation(summary = "Presupuestos del mes", description = "Obtiene los presupuestos del mes actual")
    @GetMapping("/mes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosDelMes() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosDelMes();
        return ResponseEntity.ok(response);
    }

    /**
     * Generar número de presupuesto automático
     */
    @Operation(summary = "Generar número", description = "Genera un número de presupuesto automático")
    @GetMapping("/generar-numero")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> generarNroPresupuesto() {
        String nroPresupuesto = presupuestoService.generarNroPresupuesto();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Número de presupuesto generado")
                .data(nroPresupuesto)
                .build());
    }
}