package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateProveedorRequest;
import com.belleza.pos.dto.request.UpdateProveedorRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.ProveedorResponse;
import com.belleza.pos.dto.response.ProveedorSimpleResponse;
import com.belleza.pos.service.ProveedorService;
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
 * Controlador REST para gestión de proveedores
 */
@Tag(name = "Proveedores", description = "Endpoints para gestión de proveedores")
@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @Operation(summary = "Crear proveedor", description = "Crea un nuevo proveedor")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> create(@Valid @RequestBody CreateProveedorRequest request) {
        ProveedorResponse response = proveedorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar proveedor", description = "Actualiza un proveedor existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> update(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id,
            @Valid @RequestBody UpdateProveedorRequest request) {
        ProveedorResponse response = proveedorService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener proveedor por ID", description = "Obtiene los detalles de un proveedor")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> getById(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id) {
        ProveedorResponse response = proveedorService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener por número", description = "Busca un proveedor por su número")
    @GetMapping("/numero/{nroProveedor}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> getByNroProveedor(
            @Parameter(description = "Número de proveedor") @PathVariable String nroProveedor) {
        ProveedorResponse response = proveedorService.getByNroProveedor(nroProveedor);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener por CUIT", description = "Busca un proveedor por su CUIT")
    @GetMapping("/cuit/{cuit}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> getByCuit(
            @Parameter(description = "CUIT del proveedor") @PathVariable String cuit) {
        ProveedorResponse response = proveedorService.getByCuit(cuit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar proveedores", description = "Obtiene todos los proveedores con paginación")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<ProveedorResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "razonSocial") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ProveedorResponse> response = proveedorService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar proveedores activos", description = "Obtiene todos los proveedores activos")
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ProveedorSimpleResponse>> getAllActive() {
        List<ProveedorSimpleResponse> response = proveedorService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Proveedores con cuenta corriente", description = "Obtiene proveedores con cuenta corriente")
    @GetMapping("/cuenta-corriente")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ProveedorResponse>> getConCuentaCorriente() {
        List<ProveedorResponse> response = proveedorService.getConCuentaCorriente();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar proveedor", description = "Activa un proveedor desactivado")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> activate(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id) {
        ProveedorResponse response = proveedorService.activate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar proveedor", description = "Desactiva un proveedor")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> deactivate(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id) {
        ProveedorResponse response = proveedorService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id) {
        proveedorService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Proveedor eliminado exitosamente"));
    }

    @Operation(summary = "Eliminar permanentemente", description = "Elimina un proveedor de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id) {
        proveedorService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Proveedor eliminado permanentemente"));
    }

    @Operation(summary = "Registrar pago", description = "Registra un pago al proveedor")
    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> registrarPago(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id,
            @Parameter(description = "Monto del pago") @RequestParam BigDecimal monto) {
        ProveedorResponse response = proveedorService.registrarPago(id, monto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar compra", description = "Registra una compra en cuenta corriente")
    @PostMapping("/{id}/compra")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ProveedorResponse> registrarCompra(
            @Parameter(description = "ID del proveedor") @PathVariable Integer id,
            @Parameter(description = "Monto de la compra") @RequestParam BigDecimal monto) {
        ProveedorResponse response = proveedorService.registrarCompra(id, monto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar número de proveedor", description = "Verifica si un número existe")
    @GetMapping("/verificar/numero/{nroProveedor}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkNroProveedor(
            @Parameter(description = "Número de proveedor") @PathVariable String nroProveedor) {
        boolean exists = proveedorService.existsByNroProveedor(nroProveedor);
        String message = exists ? "Número de proveedor ya existe" : "Número disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    @Operation(summary = "Verificar CUIT", description = "Verifica si un CUIT ya está registrado")
    @GetMapping("/verificar/cuit/{cuit}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkCuit(
            @Parameter(description = "CUIT") @PathVariable String cuit) {
        boolean exists = proveedorService.existsByCuit(cuit);
        String message = exists ? "CUIT ya registrado" : "CUIT disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}