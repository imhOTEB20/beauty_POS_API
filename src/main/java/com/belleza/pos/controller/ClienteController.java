package com.belleza.pos.controller;

import com.belleza.pos.dto.request.CreateClienteRequest;
import com.belleza.pos.dto.request.UpdateClienteRequest;
import com.belleza.pos.dto.response.ClienteResponse;
import com.belleza.pos.dto.response.ClienteSimpleResponse;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.service.ClienteService;
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
 * Controlador REST para gestión de clientes
 */
@Tag(name = "Clientes", description = "Endpoints para gestión de clientes")
@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Crear cliente", description = "Crea un nuevo cliente")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody CreateClienteRequest request) {
        ClienteResponse response = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar cliente", description = "Actualiza un cliente existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<ClienteResponse> update(
            @Parameter(description = "ID del cliente") @PathVariable Integer id,
            @Valid @RequestBody UpdateClienteRequest request) {
        ClienteResponse response = clienteService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener cliente por ID", description = "Obtiene los detalles de un cliente")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ClienteResponse> getById(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        ClienteResponse response = clienteService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener por número", description = "Busca un cliente por su número")
    @GetMapping("/numero/{nroCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ClienteResponse> getByNroCliente(
            @Parameter(description = "Número de cliente") @PathVariable String nroCliente) {
        ClienteResponse response = clienteService.getByNroCliente(nroCliente);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener por documento", description = "Busca un cliente por su número de documento")
    @GetMapping("/documento/{nroDocumento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<ClienteResponse> getByNroDocumento(
            @Parameter(description = "Número de documento") @PathVariable String nroDocumento) {
        ClienteResponse response = clienteService.getByNroDocumento(nroDocumento);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar clientes", description = "Obtiene todos los clientes con paginación")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<Page<ClienteResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sort,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<ClienteResponse> response = clienteService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar clientes activos", description = "Obtiene todos los clientes activos")
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<ClienteSimpleResponse>> getAllActive() {
        List<ClienteSimpleResponse> response = clienteService.getAllActive();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar clientes", description = "Busca clientes por nombre, apellido o documento")
    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<Page<ClienteResponse>> search(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ClienteResponse> response = clienteService.search(q, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Clientes con cuenta corriente", description = "Obtiene clientes que tienen cuenta corriente")
    @GetMapping("/cuenta-corriente")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ClienteResponse>> getConCuentaCorriente() {
        List<ClienteResponse> response = clienteService.getConCuentaCorriente();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Clientes con límite excedido", description = "Obtiene clientes con límite de crédito excedido")
    @GetMapping("/limite-excedido")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ClienteResponse>> getConLimiteCreditoExcedido() {
        List<ClienteResponse> response = clienteService.getConLimiteCreditoExcedido();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Clientes con saldo pendiente", description = "Obtiene clientes con saldo pendiente de pago")
    @GetMapping("/saldo-pendiente")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<ClienteResponse>> getConSaldoPendiente() {
        List<ClienteResponse> response = clienteService.getConSaldoPendiente();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar cliente", description = "Activa un cliente desactivado")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ClienteResponse> activate(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        ClienteResponse response = clienteService.activate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Desactivar cliente", description = "Desactiva un cliente")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<ClienteResponse> deactivate(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        ClienteResponse response = clienteService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar cliente", description = "Elimina un cliente (soft delete)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        clienteService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Cliente eliminado exitosamente"));
    }

    @Operation(summary = "Eliminar permanentemente", description = "Elimina un cliente de forma permanente")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del cliente") @PathVariable Integer id) {
        clienteService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Cliente eliminado permanentemente"));
    }

    @Operation(summary = "Registrar pago", description = "Registra un pago del cliente")
    @PostMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    public ResponseEntity<ClienteResponse> registrarPago(
            @Parameter(description = "ID del cliente") @PathVariable Integer id,
            @Parameter(description = "Monto del pago") @RequestParam BigDecimal monto) {
        ClienteResponse response = clienteService.registrarPago(id, monto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar venta", description = "Registra una venta en cuenta corriente")
    @PostMapping("/{id}/venta")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<ClienteResponse> registrarVenta(
            @Parameter(description = "ID del cliente") @PathVariable Integer id,
            @Parameter(description = "Monto de la venta") @RequestParam BigDecimal monto) {
        ClienteResponse response = clienteService.registrarVenta(id, monto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Verificar número de cliente", description = "Verifica si un número de cliente existe")
    @GetMapping("/verificar/numero/{nroCliente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> checkNroCliente(
            @Parameter(description = "Número de cliente") @PathVariable String nroCliente) {
        boolean exists = clienteService.existsByNroCliente(nroCliente);
        String message = exists ? "Número de cliente ya existe" : "Número disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    @Operation(summary = "Verificar documento", description = "Verifica si un documento ya está registrado")
    @GetMapping("/verificar/documento/{nroDocumento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> checkNroDocumento(
            @Parameter(description = "Número de documento") @PathVariable String nroDocumento) {
        boolean exists = clienteService.existsByNroDocumento(nroDocumento);
        String message = exists ? "Documento ya registrado" : "Documento disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    @Operation(summary = "Verificar email", description = "Verifica si un email ya está registrado")
    @GetMapping("/verificar/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<MessageResponse> checkEmail(
            @Parameter(description = "Email") @PathVariable String email) {
        boolean exists = clienteService.existsByEmail(email);
        String message = exists ? "Email ya registrado" : "Email disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }
}
