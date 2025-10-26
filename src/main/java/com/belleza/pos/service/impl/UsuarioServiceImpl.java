package com.belleza.pos.service.impl;

import com.belleza.pos.dto.request.ChangePasswordRequest;
import com.belleza.pos.dto.request.CreateUsuarioRequest;
import com.belleza.pos.dto.request.UpdateUsuarioRequest;
import com.belleza.pos.dto.response.UsuarioResponse;
import com.belleza.pos.dto.response.UsuarioSimpleResponse;
import com.belleza.pos.entity.Sucursal;
import com.belleza.pos.entity.Usuario;
import com.belleza.pos.entity.enums.RolUsuario;
import com.belleza.pos.exception.BusinessException;
import com.belleza.pos.exception.ResourceNotFoundException;
import com.belleza.pos.mapper.UsuarioMapper;
import com.belleza.pos.repository.SucursalRepository;
import com.belleza.pos.repository.UsuarioRepository;
import com.belleza.pos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Usuario
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalRepository sucursalRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponse create(CreateUsuarioRequest request) {
        log.info("Creando usuario con username: {}", request.username());

        // Validar que el username no exista
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BusinessException("El username ya está en uso: " + request.username());
        }

        // Validar que el email no exista (si se proporciona)
        if (request.email() != null && usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("El email ya está en uso: " + request.email());
        }

        // Validar rol
        RolUsuario rol;
        try {
            rol = RolUsuario.valueOf(request.rol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rol inválido: " + request.rol());
        }

        // Obtener sucursal si se proporciona
        Sucursal sucursal = null;
        if (request.idSucursal() != null) {
            sucursal = sucursalRepository.findById(request.idSucursal())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal", "id", request.idSucursal()));
        }

        // Encodear contraseña
        String encodedPassword = passwordEncoder.encode(request.password());

        // Crear usuario
        Usuario usuario = usuarioMapper.toEntity(request, encodedPassword, sucursal);
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario creado exitosamente con ID: {}", usuario.getIdUsuario());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse update(Integer id, UpdateUsuarioRequest request) {
        log.info("Actualizando usuario con ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar email si se está cambiando
        if (request.email() != null && !request.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(request.email())) {
                throw new BusinessException("El email ya está en uso: " + request.email());
            }
        }

        // Validar rol si se está cambiando
        if (request.rol() != null) {
            try {
                RolUsuario.valueOf(request.rol().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Rol inválido: " + request.rol());
            }
        }

        // Obtener sucursal si se está cambiando
        Sucursal sucursal = null;
        if (request.idSucursal() != null) {
            sucursal = sucursalRepository.findById(request.idSucursal())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Sucursal", "id", request.idSucursal()));
        }

        // Actualizar usuario
        usuarioMapper.updateEntity(usuario, request, sucursal);
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario actualizado exitosamente: {}", id);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getById(Integer id) {
        log.debug("Obteniendo usuario por ID: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getByUsername(String username) {
        log.debug("Obteniendo usuario por username: {}", username);
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "username", username));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> getAll(Pageable pageable) {
        log.debug("Obteniendo todos los usuarios con paginación");
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioSimpleResponse> getAllActive() {
        log.debug("Obteniendo todos los usuarios activos");
        return usuarioRepository.findByActivo(true).stream()
                .map(usuarioMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioSimpleResponse> getByRol(RolUsuario rol) {
        log.debug("Obteniendo usuarios por rol: {}", rol);
        return usuarioRepository.findByRol(rol).stream()
                .map(usuarioMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioSimpleResponse> getBySucursal(Integer idSucursal) {
        log.debug("Obteniendo usuarios por sucursal: {}", idSucursal);

        // Validar que la sucursal existe
        if (!sucursalRepository.existsById(idSucursal)) {
            throw new ResourceNotFoundException("Sucursal", "id", idSucursal);
        }

        return usuarioRepository.findActiveUsuariosBySucursal(idSucursal).stream()
                .map(usuarioMapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse activate(Integer id) {
        log.info("Activando usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario activado exitosamente: {}", id);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse deactivate(Integer id) {
        log.info("Desactivando usuario: {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setActivo(false);
        usuario = usuarioRepository.save(usuario);

        log.info("Usuario desactivado exitosamente: {}", id);
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Eliminando usuario (soft delete): {}", id);
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuario eliminado exitosamente (soft delete): {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando usuario permanentemente: {}", id);

        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", "id", id);
        }

        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado permanentemente: {}", id);
    }

    @Override
    @Transactional
    public void changePassword(Integer id, ChangePasswordRequest request) {
        log.info("Cambiando contraseña para usuario: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.currentPassword(), usuario.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        // Validar que las nuevas contraseñas coincidan
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new BusinessException("Las contraseñas no coinciden");
        }

        // Cambiar contraseña
        usuario.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        usuarioRepository.save(usuario);

        log.info("Contraseña cambiada exitosamente para usuario: {}", id);
    }

    @Override
    @Transactional
    public void resetPassword(Integer id, String newPassword) {
        log.info("Reseteando contraseña para usuario: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));

        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);

        log.info("Contraseña reseteada exitosamente para usuario: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}