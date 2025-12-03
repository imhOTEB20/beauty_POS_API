package com.belleza.pos.controller;

import com.belleza.pos.dto.request.*;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.service.CajaService;
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
 * Controlador REST para gestión de cajas
 */
@Tag(name = "Cajas", description = "Endpoints para gestión de cajas")
@RestController
@RequestMapping("/cajas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CajaController {

    private final CajaService cajaService;

    // ========== CRUD Básico ==========

    /**
     * Crear una nueva caja
     */
    @Operation(summary = "Crear caja", description = "Crea una nueva caja en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CajaResponse> create(@Valid @RequestBody CreateCajaRequest request) {
        CajaResponse response = cajaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar una caja existente
     */
    @Operation(summary = "Actualizar caja", description = "Actualiza los datos de una caja existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CajaResponse> update(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Valid @RequestBody UpdateCajaRequest request) {
        CajaResponse response = cajaService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener caja por ID
     */
    @Operation(summary = "Obtener caja por ID", description = "Obtiene los detalles completos de una caja")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<CajaResponse> getById(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        CajaResponse response = cajaService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener caja por número
     */
    @Operation(summary = "Obtener caja por número", description = "Busca una caja por su número")
    @GetMapping("/numero/{numeroCaja}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<CajaResponse> getByNumeroCaja(
            @Parameter(description = "Número de caja") @PathVariable String numeroCaja) {
        CajaResponse response = cajaService.getByNumeroCaja(numeroCaja);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las cajas con paginación
     */
    @Operation(summary = "Listar cajas", description = "Obtiene todas las cajas con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<CajaResponse>> getAll(
            @Parameter(description = "Número de página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "numeroCaja") String sort,
            @Parameter(description = "Dirección") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<CajaResponse> response = cajaService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las cajas activas
     */
    @Operation(summary = "Listar cajas activas", description = "Obtiene todas las cajas activas")
    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<CajaSimpleResponse>> getAllActive() {
        List<CajaSimpleResponse> response = cajaService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener cajas por sucursal
     */
    @Operation(summary = "Listar cajas por sucursal", description = "Obtiene todas las cajas de una sucursal")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<CajaSimpleResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<CajaSimpleResponse> response = cajaService.getBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener cajas activas por sucursal
     */
    @Operation(summary = "Listar cajas activas por sucursal", description = "Obtiene las cajas activas de una sucursal")
    @GetMapping("/sucursal/{idSucursal}/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<CajaSimpleResponse>> getActiveBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<CajaSimpleResponse> response = cajaService.getActiveBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    /**
     * Activar una caja
     */
    @Operation(summary = "Activar caja", description = "Activa una caja desactivada")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CajaResponse> activate(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        CajaResponse response = cajaService.activate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar una caja
     */
    @Operation(summary = "Desactivar caja", description = "Desactiva una caja sin eliminarla")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<CajaResponse> deactivate(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        CajaResponse response = cajaService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una caja (soft delete)
     */
    @Operation(summary = "Eliminar caja", description = "Elimina una caja (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        cajaService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Caja eliminada exitosamente"));
    }

    // ========== Gestión de Movimientos ==========

    /**
     * Obtener movimientos de una caja
     */
    @Operation(summary = "Listar movimientos", description = "Obtiene todos los movimientos de una caja")
    @GetMapping("/{id}/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<List<MovimientoCajaResponse>> getMovimientos(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        List<MovimientoCajaResponse> response = cajaService.getMovimientosByCaja(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener movimientos de una caja con paginación
     */
    @Operation(summary = "Listar movimientos paginados", description = "Obtiene movimientos de una caja con paginación")
    @GetMapping("/{id}/movimientos/paginado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<Page<MovimientoCajaResponse>> getMovimientosPaginados(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MovimientoCajaResponse> response = cajaService.getMovimientosByCaja(id, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener movimientos entre fechas
     */
    @Operation(summary = "Movimientos por período", description = "Obtiene movimientos de una caja entre fechas")
    @GetMapping("/{id}/movimientos/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<List<MovimientoCajaResponse>> getMovimientosPorPeriodo(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        List<MovimientoCajaResponse> response = cajaService.getMovimientosByCajaAndFechas(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener último movimiento
     */
    @Operation(summary = "Último movimiento", description = "Obtiene el último movimiento de una caja")
    @GetMapping("/{id}/movimientos/ultimo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MovimientoCajaResponse> getUltimoMovimiento(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        MovimientoCajaResponse response = cajaService.getUltimoMovimiento(id);
        return ResponseEntity.ok(response);
    }

    // ========== Operaciones de Caja ==========

    /**
     * Apertura de caja
     */
    @Operation(summary = "Apertura de caja", description = "Realiza la apertura de una caja")
    @PostMapping("/{id}/apertura")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MovimientoCajaResponse> aperturaCaja(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Valid @RequestBody AperturaCajaRequest request) {
        // Asegurar que el ID coincida
        AperturaCajaRequest adjustedRequest = new AperturaCajaRequest(
                id,
                request.idUsuario(),
                request.montoInicial(),
                request.observaciones()
        );
        MovimientoCajaResponse response = cajaService.aperturaCaja(adjustedRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Cierre de caja
     */
    @Operation(summary = "Cierre de caja", description = "Realiza el cierre de una caja")
    @PostMapping("/{id}/cierre")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MovimientoCajaResponse> cierreCaja(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Valid @RequestBody CierreCajaRequest request) {
        // Asegurar que el ID coincida
        CierreCajaRequest adjustedRequest = new CierreCajaRequest(
                id,
                request.idUsuario(),
                request.montoFinal(),
                request.observaciones()
        );
        MovimientoCajaResponse response = cajaService.cierreCaja(adjustedRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Retiro de efectivo
     */
    @Operation(summary = "Retiro de efectivo", description = "Realiza un retiro de efectivo de la caja")
    @PostMapping("/{id}/retiro")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MovimientoCajaResponse> retiroEfectivo(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Valid @RequestBody RetiroEfectivoRequest request) {
        // Asegurar que el ID coincida
        RetiroEfectivoRequest adjustedRequest = new RetiroEfectivoRequest(
                id,
                request.idUsuario(),
                request.monto(),
                request.motivo(),
                request.observaciones()
        );
        MovimientoCajaResponse response = cajaService.retiroEfectivo(adjustedRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Ingreso de efectivo
     */
    @Operation(summary = "Ingreso de efectivo", description = "Realiza un ingreso de efectivo a la caja")
    @PostMapping("/{id}/ingreso")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MovimientoCajaResponse> ingresoEfectivo(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Valid @RequestBody IngresoEfectivoRequest request) {
        // Asegurar que el ID coincida
        IngresoEfectivoRequest adjustedRequest = new IngresoEfectivoRequest(
                id,
                request.idUsuario(),
                request.monto(),
                request.motivo(),
                request.observaciones()
        );
        MovimientoCajaResponse response = cajaService.ingresoEfectivo(adjustedRequest);
        return ResponseEntity.ok(response);
    }

    // ========== Consultas y Reportes ==========

    /**
     * Obtener estado actual de la caja
     */
    @Operation(summary = "Estado de caja", description = "Obtiene el estado actual de una caja")
    @GetMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<EstadoCajaResponse> getEstadoCaja(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        EstadoCajaResponse response = cajaService.getEstadoCaja(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener resumen del día
     */
    @Operation(summary = "Resumen del día", description = "Obtiene el resumen de movimientos del día")
    @GetMapping("/{id}/resumen/dia")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<ResumenCajaResponse> getResumenDia(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Parameter(description = "Fecha") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        ResumenCajaResponse response = cajaService.getResumenDia(id, fechaConsulta);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener resumen entre fechas
     */
    @Operation(summary = "Resumen por período", description = "Obtiene el resumen de movimientos entre fechas")
    @GetMapping("/{id}/resumen/periodo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ResumenCajaResponse> getResumenPeriodo(
            @Parameter(description = "ID de la caja") @PathVariable Integer id,
            @Parameter(description = "Fecha inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @Parameter(description = "Fecha fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        ResumenCajaResponse response = cajaService.getResumenEntreFechas(id, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si la caja está abierta
     */
    @Operation(summary = "Verificar si está abierta", description = "Verifica si una caja está abierta")
    @GetMapping("/{id}/abierta")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<MessageResponse> isCajaAbierta(
            @Parameter(description = "ID de la caja") @PathVariable Integer id) {
        boolean abierta = cajaService.isCajaAbierta(id);
        String message = abierta ? "La caja está abierta" : "La caja está cerrada";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(abierta)
                .build());
    }

    /**
     * Verificar número de caja
     */
    @Operation(summary = "Verificar número de caja", description = "Verifica si un número de caja ya existe")
    @GetMapping("/verificar/numero/{numeroCaja}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNumeroCaja(
            @Parameter(description = "Número de caja") @PathVariable String numeroCaja) {
        boolean exists = cajaService.existsByNumeroCaja(numeroCaja);
        String message = exists ? "Número de caja ya existe" : "Número de caja disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}