package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateSucursalRequest;
import com.belleza.pos.dto.request.UpdateSucursalRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.SucursalResponse;
import com.belleza.pos.dto.response.SucursalSimpleResponse;
import com.belleza.pos.service.SucursalService;
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

import java.util.List;

/**
 * Controlador REST para gestión de sucursales
 */
@Tag(name = "Sucursales", description = "Endpoints para gestión de sucursales")
@RestController
@RequestMapping("/sucursales")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SucursalController {

    private final SucursalService sucursalService;

    /**
     * Crear una nueva sucursal
     */
    @Operation(summary = "Crear sucursal", description = "Crea una nueva sucursal en el sistema")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> create(@Valid @RequestBody CreateSucursalRequest request) {
        SucursalResponse response = sucursalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar una sucursal existente
     */
    @Operation(summary = "Actualizar sucursal", description = "Actualiza los datos de una sucursal existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SucursalResponse> update(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id,
            @Valid @RequestBody UpdateSucursalRequest request) {
        SucursalResponse response = sucursalService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener sucursal por ID
     */
    @Operation(summary = "Obtener sucursal por ID", description = "Obtiene los detalles de una sucursal específica")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<SucursalResponse> getById(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        SucursalResponse response = sucursalService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener sucursal por nombre
     */
    @Operation(summary = "Obtener sucursal por nombre", description = "Busca una sucursal por su nombre")
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<SucursalResponse> getByNombre(
            @Parameter(description = "Nombre de la sucursal") @PathVariable String nombre) {
        SucursalResponse response = sucursalService.getByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las sucursales con paginación
     */
    @Operation(summary = "Listar sucursales", description = "Obtiene todas las sucursales con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<SucursalResponse>> getAll(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "nombre") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<SucursalResponse> response = sucursalService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todas las sucursales activas
     */
    @Operation(summary = "Listar sucursales activas", description = "Obtiene todas las sucursales activas (listado simple)")
    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<SucursalSimpleResponse>> getAllActive() {
        List<SucursalSimpleResponse> response = sucursalService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Activar una sucursal
     */
    @Operation(summary = "Activar sucursal", description = "Activa una sucursal desactivada")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> activate(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        SucursalResponse response = sucursalService.activate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar una sucursal
     */
    @Operation(summary = "Desactivar sucursal", description = "Desactiva una sucursal (valida que no tenga usuarios activos)")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SucursalResponse> deactivate(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        SucursalResponse response = sucursalService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar una sucursal (soft delete)
     */
    @Operation(summary = "Eliminar sucursal", description = "Elimina una sucursal (soft delete - la desactiva)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        sucursalService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Sucursal eliminada exitosamente"));
    }

    /**
     * Eliminar una sucursal permanentemente
     */
    @Operation(summary = "Eliminar sucursal permanentemente",
            description = "Elimina una sucursal de forma permanente de la base de datos (CUIDADO)")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        sucursalService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Sucursal eliminada permanentemente"));
    }

    /**
     * Verificar si existe una sucursal por nombre
     */
    @Operation(summary = "Verificar nombre", description = "Verifica si un nombre de sucursal ya está en uso")
    @GetMapping("/verificar/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNombre(
            @Parameter(description = "Nombre a verificar") @PathVariable String nombre) {
        boolean exists = sucursalService.existsByNombre(nombre);
        String message = exists ? "Nombre ya está en uso" : "Nombre disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Contar usuarios de una sucursal
     */
    @Operation(summary = "Contar usuarios", description = "Obtiene el número de usuarios asignados a una sucursal")
    @GetMapping("/{id}/usuarios/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> countUsuarios(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer id) {
        long count = sucursalService.countUsuariosBySucursal(id);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Número de usuarios en la sucursal")
                .data(count)
                .build());
    }
}
