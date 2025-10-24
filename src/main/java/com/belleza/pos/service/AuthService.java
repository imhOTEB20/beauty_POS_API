package com.belleza.pos.service;

import com.belleza.pos.dto.request.LoginRequest;
import com.belleza.pos.dto.request.RefreshTokenRequest;
import com.belleza.pos.dto.request.RegisterRequest;
import com.belleza.pos.dto.response.AuthResponse;
import com.belleza.pos.entity.Sucursal;
import com.belleza.pos.entity.Usuario;
import com.belleza.pos.entity.enums.RolUsuario;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.repository.SucursalRepository;
import com.belleza.pos.repository.UsuarioRepository;
import com.belleza.pos.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Servicio de autenticación
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Realiza el login de un usuario
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generar tokens
        String token = jwtUtil.generateToken(authentication);
        String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        // Actualizar último login
        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        log.info("Usuario autenticado exitosamente: {}", request.getUsername());

        // Construir respuesta
        return buildAuthResponse(usuario, token, refreshToken);
    }

    /**
     * Registra un nuevo usuario
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("El username ya está en uso");
        }

        // Validar que el email no exista (si se proporciona)
        if (request.getEmail() != null && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("El email ya está en uso");
        }

        // Validar rol
        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(request.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rol inválido: " + request.getRol());
        }

        // Validar y obtener sucursal
        Sucursal sucursal = null;
        if (request.getIdSucursal() != null) {
            sucursal = sucursalRepository.findById(request.getIdSucursal())
                    .orElseThrow(() -> new BusinessException("Sucursal no encontrada"));
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setUsername(request.getUsername());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setRol(rol);
        usuario.setSucursal(sucursal);
        usuario.setActivo(true);

        usuario = usuarioRepository.save(usuario);

        log.info("Usuario registrado exitosamente: {}", request.getUsername());

        // Generar tokens
        String token = jwtUtil.generateTokenFromUsername(usuario.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(usuario.getUsername());

        // Construir respuesta
        return buildAuthResponse(usuario, token, refreshToken);
    }

    /**
     * Refresca el token de acceso
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validar refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException("Refresh token inválido o expirado");
        }

        // Obtener username del token
        String username = jwtUtil.getUsernameFromToken(refreshToken);

        // Obtener usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new BusinessException("Usuario inactivo");
        }

        // Generar nuevo token de acceso
        String newToken = jwtUtil.generateTokenFromUsername(username);

        log.info("Token refrescado para usuario: {}", username);

        // Construir respuesta
        return buildAuthResponse(usuario, newToken, refreshToken);
    }

    /**
     * Construye la respuesta de autenticación
     */
    private AuthResponse buildAuthResponse(Usuario usuario, String token, String refreshToken) {
        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .idSucursal(usuario.getSucursal() != null ? usuario.getSucursal().getIdSucursal() : null)
                .nombreSucursal(usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null)
                .build();
    }
}