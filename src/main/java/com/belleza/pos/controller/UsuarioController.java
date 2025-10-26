package com.belleza.pos.controller;

import com.belleza.pos.dto.request.ChangePasswordRequest;
import com.belleza.pos.dto.request.CreateUsuarioRequest;
import com.belleza.pos.dto.request.UpdateUsuarioRequest;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.dto.response.UsuarioResponse;
import com.belleza.pos.dto.response.UsuarioSimpleResponse;
import com.belleza.pos.entity.enums.RolUsuario;
import com.belleza.pos.service.UsuarioService;
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
 * Controlador REST para gestión de usuarios
 */
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios del sistema")
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Crear un nuevo usuario
     */
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody CreateUsuarioRequest request) {
        UsuarioResponse response = usuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualizar un usuario existente
     */
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponse> update(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            @Valid @RequestBody UpdateUsuarioRequest request) {
        UsuarioResponse response = usuarioService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener usuario por ID
     */
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<UsuarioResponse> getById(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        UsuarioResponse response = usuarioService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener usuario por username
     */
    @Operation(summary = "Obtener usuario por username", description = "Busca un usuario por su nombre de usuario")
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponse> getByUsername(
            @Parameter(description = "Username del usuario") @PathVariable String username) {
        UsuarioResponse response = usuarioService.getByUsername(username);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los usuarios con paginación
     */
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios con paginación y ordenamiento")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Page<UsuarioResponse>> getAll(
            @Parameter(description = "Número de página (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo de ordenamiento") @RequestParam(defaultValue = "idUsuario") String sort,
            @Parameter(description = "Dirección de ordenamiento") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        Page<UsuarioResponse> response = usuarioService.getAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener todos los usuarios activos
     */
    @Operation(summary = "Listar usuarios activos", description = "Obtiene todos los usuarios activos (listado simple)")
    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<List<UsuarioSimpleResponse>> getAllActive() {
        List<UsuarioSimpleResponse> response = usuarioService.getAllActive();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener usuarios por rol
     */
    @Operation(summary = "Listar usuarios por rol", description = "Obtiene todos los usuarios de un rol específico")
    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<UsuarioSimpleResponse>> getByRol(
            @Parameter(description = "Rol del usuario") @PathVariable String rol) {
        try {
            RolUsuario rolUsuario = RolUsuario.valueOf(rol.toUpperCase());
            List<UsuarioSimpleResponse> response = usuarioService.getByRol(rolUsuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new com.belleza.pos.exception.BusinessException("Rol inválido: " + rol);
        }
    }

    /**
     * Obtener usuarios por sucursal
     */
    @Operation(summary = "Listar usuarios por sucursal", description = "Obtiene todos los usuarios de una sucursal específica")
    @GetMapping("/sucursal/{idSucursal}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<UsuarioSimpleResponse>> getBySucursal(
            @Parameter(description = "ID de la sucursal") @PathVariable Integer idSucursal) {
        List<UsuarioSimpleResponse> response = usuarioService.getBySucursal(idSucursal);
        return ResponseEntity.ok(response);
    }

    /**
     * Activar un usuario
     */
    @Operation(summary = "Activar usuario", description = "Activa un usuario desactivado")
    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponse> activate(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        UsuarioResponse response = usuarioService.activate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Desactivar un usuario
     */
    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario sin eliminarlo")
    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<UsuarioResponse> deactivate(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        UsuarioResponse response = usuarioService.deactivate(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Eliminar un usuario (soft delete)
     */
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario (soft delete - lo desactiva)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.ok(new MessageResponse("Usuario eliminado exitosamente"));
    }

    /**
     * Eliminar un usuario permanentemente
     */
    @Operation(summary = "Eliminar usuario permanentemente",
            description = "Elimina un usuario de forma permanente de la base de datos (CUIDADO)")
    @DeleteMapping("/{id}/permanente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deletePermanently(
            @Parameter(description = "ID del usuario") @PathVariable Integer id) {
        usuarioService.deletePermanently(id);
        return ResponseEntity.ok(new MessageResponse("Usuario eliminado permanentemente"));
    }

    /**
     * Cambiar contraseña de un usuario
     */
    @Operation(summary = "Cambiar contraseña", description = "Permite a un usuario cambiar su propia contraseña")
    @PatchMapping("/{id}/cambiar-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<MessageResponse> changePassword(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            @Valid @RequestBody ChangePasswordRequest request) {
        usuarioService.changePassword(id, request);
        return ResponseEntity.ok(new MessageResponse("Contraseña cambiada exitosamente"));
    }

    /**
     * Resetear contraseña de un usuario (solo admin)
     */
    @Operation(summary = "Resetear contraseña",
            description = "Permite a un administrador resetear la contraseña de cualquier usuario")
    @PatchMapping("/{id}/resetear-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> resetPassword(
            @Parameter(description = "ID del usuario") @PathVariable Integer id,
            @Parameter(description = "Nueva contraseña") @RequestParam String newPassword) {
        usuarioService.resetPassword(id, newPassword);
        return ResponseEntity.ok(new MessageResponse("Contraseña reseteada exitosamente"));
    }

    /**
     * Verificar si existe un username
     */
    @Operation(summary = "Verificar username", description = "Verifica si un username ya está en uso")
    @GetMapping("/verificar/username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkUsername(
            @Parameter(description = "Username a verificar") @PathVariable String username) {
        boolean exists = usuarioService.existsByUsername(username);
        String message = exists ? "Username ya está en uso" : "Username disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Verificar si existe un email
     */
    @Operation(summary = "Verificar email", description = "Verifica si un email ya está en uso")
    @GetMapping("/verificar/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MessageResponse> checkEmail(
            @Parameter(description = "Email a verificar") @PathVariable String email) {
        boolean exists = usuarioService.existsByEmail(email);
        String message = exists ? "Email ya está en uso" : "Email disponible";
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .data(exists)
                .build());
    }

    /**
     * Obtener lista de roles disponibles
     */
    @Operation(summary = "Listar roles", description = "Obtiene la lista de roles disponibles en el sistema")
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<String>> getRoles() {
        List<String> roles = List.of(
                RolUsuario.ADMIN.name(),
                RolUsuario.GERENTE.name(),
                RolUsuario.VENDEDOR.name(),
                RolUsuario.CAJERO.name()
        );
        return ResponseEntity.ok(roles);
    }
}