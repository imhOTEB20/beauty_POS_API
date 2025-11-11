package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreatePresupuestoRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.PresupuestoResponse;
import com.belleza.pos.dto.response.PresupuestoSimpleResponse;
import com.belleza.pos.dto.response.VentaResponse;
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
    @Operation(summary = "Crear presupuesto", description = "Registra un nuevo presupuesto en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<PresupuestoResponse> create(@Valid @RequestBody CreatePresupuestoRequest request) {
        PresupuestoResponse response = presupuestoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
    @Operation(summary = "Obtener presupuesto por número", description = "Busca un presupuesto por su número")
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
     * Buscar presupuestos
     */
    @Operation(summary = "Buscar presupuestos", description = "Busca presupuestos por número o cliente")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un presupuesto permanentemente
     */
    @Operation(summary = "Eliminar presupuesto", description = "Elimina un presupuesto de forma permanente")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        presupuestoService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Presupuesto eliminado exitosamente"));
    }

    // ========== Gestión de Estado ==========

    /**
     * Aprobar un presupuesto
     */
    @Operation(summary = "Aprobar presupuesto", description = "Aprueba un presupuesto pendiente")
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PresupuestoResponse> aprobar(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        PresupuestoResponse response = presupuestoService.aprobar(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Rechazar un presupuesto
     */
    @Operation(summary = "Rechazar presupuesto", description = "Rechaza un presupuesto pendiente")
    @PatchMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<PresupuestoResponse> rechazar(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id,
            @Parameter(description = "Motivo del rechazo") @RequestParam String motivo) {
        PresupuestoResponse response = presupuestoService.rechazar(id, motivo);
        return ResponseEntity.ok(response);
    }

    /**
     * Convertir presupuesto a venta
     */
    @Operation(summary = "Convertir a venta", description = "Convierte un presupuesto aprobado en una venta")
    @PostMapping("/{id}/convertir-venta")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<VentaResponse> convertirAVenta(
            @Parameter(description = "ID del presupuesto") @PathVariable Integer id) {
        VentaResponse response = presupuestoService.convertirAVenta(id);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Estado ==========

    /**
     * Obtener presupuestos por estado
     */
    @Operation(summary = "Listar por estado", description = "Obtiene presupuestos filtrados por estado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> getByEstado(
            @Parameter(description = "Estado del presupuesto") @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getByEstado(estado, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos pendientes
     */
    @Operation(summary = "Presupuestos pendientes", description = "Obtiene todos los presupuestos pendientes")
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosPendientes() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosPendientes();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos aprobados
     */
    @Operation(summary = "Presupuestos aprobados", description = "Obtiene todos los presupuestos aprobados")
    @GetMapping("/aprobados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosAprobados() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosAprobados();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos rechazados
     */
    @Operation(summary = "Presupuestos rechazados", description = "Obtiene todos los presupuestos rechazados")
    @GetMapping("/rechazados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosRechazados() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosRechazados();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos convertidos a venta
     */
    @Operation(summary = "Presupuestos convertidos", description = "Obtiene todos los presupuestos convertidos a venta")
    @GetMapping("/convertidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosConvertidos() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosConvertidos();
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Cliente ==========

    /**
     * Obtener presupuestos de un cliente
     */
    @Operation(summary = "Listar por cliente", description = "Obtiene los presupuestos de un cliente específico")
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
     * Obtener historial de presupuestos de un cliente
     */
    @Operation(summary = "Historial del cliente", description = "Obtiene el historial completo de presupuestos de un cliente")
    @GetMapping("/cliente/{idCliente}/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getHistorialCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente) {
        List<PresupuestoSimpleResponse> response = presupuestoService.getHistorialCliente(idCliente);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Sucursal ==========

    /**
     * Obtener presupuestos de una sucursal
     */
    @Operation(summary = "Listar por sucursal", description = "Obtiene los presupuestos de una sucursal específica")
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
     * Obtener presupuestos del día de una sucursal
     */
    @Operation(summary = "Presupuestos del día por sucursal", description = "Obtiene los presupuestos del día de una sucursal")
    @GetMapping("/sucursal/{idSucursal}/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas por Usuario ==========

    /**
     * Obtener presupuestos de un usuario
     */
    @Operation(summary = "Listar por usuario", description = "Obtiene los presupuestos realizados por un usuario")
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

    // ========== Consultas por Fecha ==========

    /**
     * Obtener presupuestos del día
     */
    @Operation(summary = "Presupuestos del día", description = "Obtiene todos los presupuestos del día actual")
    @GetMapping("/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosDelDia() {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosDelDia();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos entre fechas
     */
    @Operation(summary = "Listar por período", description = "Obtiene presupuestos en un rango de fechas")
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<PresupuestoResponse>> getByFechaPresupuestoBetween(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPresupuesto"));
        Page<PresupuestoResponse> response = presupuestoService.getByFechaPresupuestoBetween(fechaInicio, fechaFin, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener presupuestos de un período (sin paginación)
     */
    @Operation(summary = "Presupuestos por período simple", description = "Obtiene presupuestos de un período sin paginación")
    @GetMapping("/periodo/simple")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<PresupuestoSimpleResponse>> getPresupuestosByPeriodo(
            @Parameter(description = "Fecha inicio")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<PresupuestoSimpleResponse> response = presupuestoService.getPresupuestosByPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ========== Estadísticas ==========

    /**
     * Contar presupuestos del día
     */
    @Operation(summary = "Contar presupuestos del día", description = "Obtiene la cantidad de presupuestos del día")
    @GetMapping("/count/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> countPresupuestosDelDia() {
        Long count = presupuestoService.countPresupuestosDelDia();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de presupuestos del día")
                .data(count)
                .build());
    }

    /**
     * Contar presupuestos del día por sucursal
     */
    @Operation(summary = "Contar presupuestos del día por sucursal", description = "Obtiene la cantidad de presupuestos del día en una sucursal")
    @GetMapping("/count/dia/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> countPresupuestosDelDiaBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        Long count = presupuestoService.countPresupuestosDelDiaBySucursal(idSucursal);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de presupuestos del día de la sucursal")
                .data(count)
                .build());
    }

    /**
     * Contar presupuestos pendientes
     */
    @Operation(summary = "Contar presupuestos pendientes", description = "Obtiene la cantidad total de presupuestos pendientes")
    @GetMapping("/count/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countPresupuestosPendientes() {
        Long count = presupuestoService.countPresupuestosPendientes();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de presupuestos pendientes")
                .data(count)
                .build());
    }

    /**
     * Contar presupuestos convertidos
     */
    @Operation(summary = "Contar presupuestos convertidos", description = "Obtiene la cantidad total de presupuestos convertidos a venta")
    @GetMapping("/count/convertidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countPresupuestosConvertidos() {
        Long count = presupuestoService.countPresupuestosConvertidos();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Cantidad de presupuestos convertidos")
                .data(count)
                .build());
    }

    // ========== Utilidades ==========

    /**
     * Verificar número de presupuesto
     */
    @Operation(summary = "Verificar número de presupuesto", description = "Verifica si un número de presupuesto ya existe")
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
     * Generar número de presupuesto
     */
    @Operation(summary = "Generar número de presupuesto", description = "Genera un número de presupuesto único")
    @GetMapping("/generar/numero")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> generarNroPresupuesto() {
        String nroPresupuesto = presupuestoService.generarNroPresupuesto();
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Número de presupuesto generado")
                .data(nroPresupuesto)
                .build());
    }
}