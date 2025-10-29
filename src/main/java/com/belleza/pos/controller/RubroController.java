package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateRubroRequest;
import com.belleza.pos.dto.request.UpdateRubroRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.RubroResponse;
import com.belleza.pos.dto.response.RubroSimpleResponse;
import com.belleza.pos.service.RubroService;
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
 * Controlador REST para gestión de rubros
 */
@Tag(name = "Rubros", description = "Endpoints para gestión de rubros/categorías de productos")
@RestController
@RequestMapping("/rubros")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RubroController {

    private final RubroService rubroService;

    @Operation(summary = "Crear rubro", description = "Crea un nuevo rubro/categoría")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RubroResponse> create(@Valid @RequestBody CreateRubroRequest request) {
        RubroResponse response = rubroService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar rubro", description = "Actualiza un rubro existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RubroResponse> update(
            @Parameter(description = "ID del rubro") @PathVariable Integer id,
            @Valid @RequestBody UpdateRubroRequest request) {
        RubroResponse response = rubroService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener rubro por ID", description = "Obtiene los detalles de un rubro")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<RubroResponse> getById(
            @Parameter(description = "ID del rubro") @PathVariable Integer id) {
        RubroResponse response = rubroService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener rubro por nombre", description = "Busca un rubro por su nombre")
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RubroResponse> getByNombre(
            @Parameter(description = "Nombre del rubro") @PathVariable String nombre) {
        RubroResponse response = rubroService.getByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar rubros", description = "Obtiene todos los rubros con paginación")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<RubroResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<RubroResponse> response = rubroService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar rubros activos", description = "Obtiene todos los rubros activos")
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<RubroSimpleResponse>> getAllActive() {
        List<RubroSimpleResponse> response = rubroService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar rubro", description = "Activa un rubro desactivado")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RubroResponse> activate(
            @Parameter(description = "ID del rubro") @PathVariable Integer id) {
        RubroResponse response = rubroService.activate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar rubro", description = "Desactiva un rubro")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RubroResponse> deactivate(
            @Parameter(description = "ID del rubro") @PathVariable Integer id) {
        RubroResponse response = rubroService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar rubro", description = "Elimina un rubro (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del rubro") @PathVariable Integer id) {
        rubroService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Rubro eliminado exitosamente"));
    }

    @Operation(summary = "Eliminar permanentemente", description = "Elimina un rubro de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del rubro") @PathVariable Integer id) {
        rubroService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Rubro eliminado permanentemente"));
    }

    @Operation(summary = "Verificar nombre", description = "Verifica si un nombre de rubro ya existe")
    @GetMapping("/verificar/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNombre(
            @Parameter(description = "Nombre a verificar") @PathVariable String nombre) {
        boolean exists = rubroService.existsByNombre(nombre);
        String message = exists ? "Nombre ya está en uso" : "Nombre disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}