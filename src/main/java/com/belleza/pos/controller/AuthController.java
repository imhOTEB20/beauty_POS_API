package com.belleza.pos.controller;

import com.belleza.pos.dto.request.LoginRequest;
import com.belleza.pos.dto.request.RefreshTokenRequest;
import com.belleza.pos.dto.request.RegisterRequest;
import com.belleza.pos.dto.response.AuthResponse;
import com.belleza.pos.dto.response.MessageResponse;
import com.belleza.pos.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación
 */
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para login
     */
    @Operation(summary = "Login de usuario", description = "Autentica un usuario y devuelve tokens JWT")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para registro
     */
    @Operation(summary = "Registro de usuario", description = "Registra un nuevo usuario en el sistema")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para refrescar token
     */
    @Operation(summary = "Refrescar token", description = "Genera un nuevo token de acceso usando el refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para logout
     */
    @Operation(summary = "Logout de usuario", description = "Cierra la sesión del usuario")
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout() {
        // En JWT stateless, el logout se maneja en el cliente eliminando el token
        // Aquí podrías agregar lógica para invalidar el token en una blacklist si lo necesitas
        return ResponseEntity.ok(new MessageResponse("Logout exitoso"));
    }
}