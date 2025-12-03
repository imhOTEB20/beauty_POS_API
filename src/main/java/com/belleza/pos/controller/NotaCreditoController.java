package com.belleza.pos.controller;

import com.belleza.pos.dto.request.AnularNotaCreditoRequest;
import com.belleza.pos.dto.request.CreateNotaCreditoRequest;
import com.belleza.pos.dto.request.UpdateNotaCreditoRequest;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.service.NotaCreditoService;
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
 * Controlador REST para gestión de notas de crédito
 */
@Tag(name = "Notas de Crédito", description = "Endpoints para gestión de notas de crédito")
@RestController
@RequestMapping("/notas-credito")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotaCreditoController {

    private final NotaCreditoService notaCreditoService;

    // ========== CRUD Básico ==========

    /**
     * Crear una nueva nota de crédito
     */
    @Operation(summary = "Crear nota de crédito", description = "Crea una nueva nota de crédito en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<NotaCreditoResponse> create(@Valid @RequestBody CreateNotaCreditoRequest request) {
        NotaCreditoResponse response = notaCreditoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar una nota de crédito existente
     */
    @Operation(summary = "Actualizar nota de crédito", description = "Actualiza los datos de una nota de crédito existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<NotaCreditoResponse> update(
            @Parameter(description = "ID de la nota de crédito") @PathVariable Integer id,
            @Valid @RequestBody UpdateNotaCreditoRequest request) {
        NotaCreditoResponse response = notaCreditoService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener nota de crédito por ID
     */
    @Operation(summary = "Obtener nota de crédito por ID", description = "Obtiene los detalles completos de una nota de crédito")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<NotaCreditoResponse> getById(
            @Parameter(description = "ID de la nota de crédito") @PathVariable Integer id) {
        NotaCreditoResponse response = notaCreditoService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener nota de crédito por número de comprobante
     */
    @Operation(summary = "Obtener por número", description = "Busca una nota de crédito por su número de comprobante")
    @GetMapping("/comprobante/{nroComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<NotaCreditoResponse> getByNroComprobante(
            @Parameter(description = "Número de comprobante") @PathVariable String nroComprobante) {
        NotaCreditoResponse response = notaCreditoService.getByNroComprobante(nroComprobante);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las notas de crédito con paginación
     */
    @Operation(summary = "Listar notas de crédito", description = "Obtiene todas las notas de crédito con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<NotaCreditoResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "fecha") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<NotaCreditoResponse> response = notaCreditoService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las notas de crédito activas
     */
    @Operation(summary = "Listar activas", description = "Obtiene todas las notas de crédito activas")
    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoSimpleResponse>> getAllActive() {
        List<NotaCreditoSimpleResponse> response = notaCreditoService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar notas de crédito
     */
    @Operation(summary = "Buscar notas de crédito", description = "Busca notas de crédito por número o cliente")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<NotaCreditoResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotaCreditoResponse> response = notaCreditoService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito por cliente
     */
    @Operation(summary = "Listar por cliente", description = "Obtiene notas de crédito de un cliente específico")
    @GetMapping("/cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<NotaCreditoResponse>> getByCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<NotaCreditoResponse> response = notaCreditoService.getByCliente(idCliente, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito por sucursal
     */
    @Operation(summary = "Listar por sucursal", description = "Obtiene notas de crédito de una sucursal")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<NotaCreditoResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<NotaCreditoResponse> response = notaCreditoService.getBySucursal(idSucursal, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito por estado
     */
    @Operation(summary = "Listar por estado", description = "Obtiene notas de crédito por estado")
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<NotaCreditoResponse>> getByEstado(
            @Parameter(description = "Estado") @PathVariable String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
        Page<NotaCreditoResponse> response = notaCreditoService.getByEstado(estado, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito entre fechas
     */
    @Operation(summary = "Listar por período", description = "Obtiene notas de crédito entre fechas")
    @GetMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoResponse>> getByPeriodo(
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<NotaCreditoResponse> response = notaCreditoService.getByFechaBetween(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Anular una nota de crédito
     */
    @Operation(summary = "Anular nota de crédito", description = "Anula una nota de crédito existente")
    @PostMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<NotaCreditoResponse> anular(
            @Parameter(description = "ID de la nota de crédito") @PathVariable Integer id,
            @Valid @RequestBody AnularNotaCreditoRequest request) {
        NotaCreditoResponse response = notaCreditoService.anular(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una nota de crédito permanentemente
     */
    @Operation(summary = "Eliminar permanentemente", description = "Elimina una nota de crédito de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID de la nota de crédito") @PathVariable Integer id) {
        notaCreditoService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Nota de crédito eliminada permanentemente"));
    }

    // ========== Gestión de Detalles ==========

    /**
     * Obtener detalles de una nota de crédito
     */
    @Operation(summary = "Obtener detalles", description = "Obtiene los detalles de una nota de crédito")
    @GetMapping("/{id}/detalles")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoDetalleResponse>> getDetalles(
            @Parameter(description = "ID de la nota de crédito") @PathVariable Integer id) {
        List<NotaCreditoDetalleResponse> response = notaCreditoService.getDetalles(id);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas y Reportes ==========

    /**
     * Obtener notas de crédito por cliente y período
     */
    @Operation(summary = "Listar por cliente y período", description = "Obtiene notas de crédito de un cliente entre fechas")
    @GetMapping("/cliente/{idCliente}/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoResponse>> getByClienteAndPeriodo(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<NotaCreditoResponse> response = notaCreditoService.getByClienteAndFechaBetween(
                idCliente, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito por sucursal y período
     */
    @Operation(summary = "Listar por sucursal y período", description = "Obtiene notas de crédito de una sucursal entre fechas")
    @GetMapping("/sucursal/{idSucursal}/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<NotaCreditoResponse>> getBySucursalAndPeriodo(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<NotaCreditoResponse> response = notaCreditoService.getBySucursalAndFechaBetween(
                idSucursal, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito por tipo de comprobante
     */
    @Operation(summary = "Listar por tipo", description = "Obtiene notas de crédito por tipo de comprobante")
    @GetMapping("/tipo/{tipoComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<NotaCreditoResponse>> getByTipo(
            @Parameter(description = "Tipo de comprobante") @PathVariable String tipoComprobante) {
        List<NotaCreditoResponse> response = notaCreditoService.getByTipoComprobante(tipoComprobante);
        return ResponseEntity.ok(response);
    }

    /**
     * Calcular total de notas de crédito en un período
     */
    @Operation(summary = "Calcular total período", description = "Calcula el total de notas de crédito en un período")
    @GetMapping("/reportes/total-periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> calcularTotalPeriodo(
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        BigDecimal total = notaCreditoService.calcularTotalPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de notas de crédito en el período")
                .data(total)
                .build());
    }

    /**
     * Calcular total de notas de crédito por cliente
     */
    @Operation(summary = "Calcular total por cliente", description = "Calcula el total de notas de crédito de un cliente")
    @GetMapping("/reportes/total-cliente/{idCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> calcularTotalCliente(
            @Parameter(description = "ID del cliente") @PathVariable Integer idCliente) {
        BigDecimal total = notaCreditoService.calcularTotalPorCliente(idCliente);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Total de notas de crédito del cliente")
                .data(total)
                .build());
    }

    /**
     * Obtener estadísticas de notas de crédito
     */
    @Operation(summary = "Estadísticas", description = "Obtiene estadísticas de notas de crédito en un período")
    @GetMapping("/reportes/estadisticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<EstadisticasNotaCreditoResponse> getEstadisticas(
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        EstadisticasNotaCreditoResponse response = notaCreditoService.getEstadisticas(fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ========== Utilidades ==========

    /**
     * Verificar número de comprobante
     */
    @Operation(summary = "Verificar número", description = "Verifica si un número de comprobante ya existe")
    @GetMapping("/verificar/numero/{nroComprobante}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> checkNroComprobante(
            @Parameter(description = "Número de comprobante") @PathVariable String nroComprobante) {
        boolean exists = notaCreditoService.existsByNroComprobante(nroComprobante);
        String message = exists ? "Número de comprobante ya existe" : "Número de comprobante disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Obtener notas de crédito del día
     */
    @Operation(summary = "Notas del día", description = "Obtiene las notas de crédito del día actual")
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoSimpleResponse>> getNotasDelDia() {
        List<NotaCreditoSimpleResponse> response = notaCreditoService.getNotasDelDia();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener notas de crédito del mes
     */
    @Operation(summary = "Notas del mes", description = "Obtiene las notas de crédito del mes actual")
    @GetMapping("/mes")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<NotaCreditoSimpleResponse>> getNotasDelMes() {
        List<NotaCreditoSimpleResponse> response = notaCreditoService.getNotasDelMes();
        return ResponseEntity.ok(response);
    }
}