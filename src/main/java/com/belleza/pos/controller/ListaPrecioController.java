package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateListaPrecioRequest;
import com.belleza.pos.dto.request.UpdateListaPrecioRequest;
import com.belleza.pos.dto.response.ListaPrecioResponse;
import com.belleza.pos.dto.response.ListaPrecioSimpleResponse;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.service.ListaPrecioService;
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
 * Controlador REST para gestión de listas de precios
 */
@Tag(name = "Listas de Precios", description = "Endpoints para gestión de listas de precios")
@RestController
@RequestMapping("/listas-precios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ListaPrecioController {

    private final ListaPrecioService listaPrecioService;

    @Operation(summary = "Crear lista de precios", description = "Crea una nueva lista de precios")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListaPrecioResponse> create(@Valid @RequestBody CreateListaPrecioRequest request) {
        ListaPrecioResponse response = listaPrecioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar lista", description = "Actualiza una lista de precios existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ListaPrecioResponse> update(
            @Parameter(description = "ID de la lista") @PathVariable Integer id,
            @Valid @RequestBody UpdateListaPrecioRequest request) {
        ListaPrecioResponse response = listaPrecioService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener lista por ID", description = "Obtiene los detalles de una lista de precios")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ListaPrecioResponse> getById(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        ListaPrecioResponse response = listaPrecioService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener lista por nombre", description = "Busca una lista por su nombre")
    @GetMapping("/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ListaPrecioResponse> getByNombre(
            @Parameter(description = "Nombre de la lista") @PathVariable String nombre) {
        ListaPrecioResponse response = listaPrecioService.getByNombre(nombre);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar listas de precios", description = "Obtiene todas las listas con paginación")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<ListaPrecioResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ListaPrecioResponse> response = listaPrecioService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar listas activas", description = "Obtiene todas las listas activas")
    @GetMapping("/activas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<ListaPrecioSimpleResponse>> getAllActive() {
        List<ListaPrecioSimpleResponse> response = listaPrecioService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener lista predeterminada", description = "Obtiene la lista de precios predeterminada")
    @GetMapping("/predeterminada")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ListaPrecioResponse> getPredeterminada() {
        ListaPrecioResponse response = listaPrecioService.getPredeterminada();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Establecer predeterminada", description = "Marca una lista como predeterminada")
    @PatchMapping("/{id}/predeterminada")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListaPrecioResponse> setPredeterminada(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        ListaPrecioResponse response = listaPrecioService.setPredeterminada(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar lista", description = "Activa una lista desactivada")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ListaPrecioResponse> activate(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        ListaPrecioResponse response = listaPrecioService.activate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar lista", description = "Desactiva una lista")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ListaPrecioResponse> deactivate(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        ListaPrecioResponse response = listaPrecioService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar lista", description = "Elimina una lista (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        listaPrecioService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Lista de precios eliminada exitosamente"));
    }

    @Operation(summary = "Eliminar permanentemente", description = "Elimina una lista de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID de la lista") @PathVariable Integer id) {
        listaPrecioService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Lista de precios eliminada permanentemente"));
    }

    @Operation(summary = "Verificar nombre", description = "Verifica si un nombre ya existe")
    @GetMapping("/verificar/nombre/{nombre}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNombre(
            @Parameter(description = "Nombre a verificar") @PathVariable String nombre) {
        boolean exists = listaPrecioService.existsByNombre(nombre);
        String message = exists ? "Nombre ya está en uso" : "Nombre disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}
