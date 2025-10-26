package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateUsuarioRequest;
import com.belleza.pos.dto.request.UpdateUsuarioRequest;
import com.belleza.pos.dto.response.UsuarioResponse;
import com.belleza.pos.dto.response.UsuarioSimpleResponse;
import com.belleza.pos.entity.Sucursal;
import com.belleza.pos.entity.Usuario;
import com.belleza.pos.entity.enums.RolUsuario;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades Usuario y DTOs
 */
@Component
public class UsuarioMapper {

    /**
     * Convierte CreateUsuarioRequest a Usuario
     */
    public Usuario toEntity(CreateUsuarioRequest request, String encodedPassword, Sucursal sucursal) {
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setPasswordHash(encodedPassword);
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setEmail(request.email());
        usuario.setRol(RolUsuario.valueOf(request.rol().toUpperCase()));
        usuario.setSucursal(sucursal);
        usuario.setActivo(request.activo() != null ? request.activo() : true);
        return usuario;
    }

    /**
     * Actualiza un Usuario existente con UpdateUsuarioRequest
     */
    public void updateEntity(Usuario usuario, UpdateUsuarioRequest request, Sucursal sucursal) {
        if (request.nombre() != null) {
            usuario.setNombre(request.nombre());
        }
        if (request.apellido() != null) {
            usuario.setApellido(request.apellido());
        }
        if (request.email() != null) {
            usuario.setEmail(request.email());
        }
        if (request.rol() != null) {
            usuario.setRol(RolUsuario.valueOf(request.rol().toUpperCase()));
        }
        if (request.idSucursal() != null) {
            usuario.setSucursal(sucursal);
        }
        if (request.activo() != null) {
            usuario.setActivo(request.activo());
        }
    }

    /**
     * Convierte Usuario a UsuarioResponse
     */
    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .idSucursal(usuario.getSucursal() != null ? usuario.getSucursal().getIdSucursal() : null)
                .nombreSucursal(usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null)
                .activo(usuario.getActivo())
                .ultimoLogin(usuario.getUltimoLogin())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaModificacion(usuario.getFechaModificacion())
                .build();
    }

    /**
     * Convierte Usuario a UsuarioSimpleResponse
     */
    public UsuarioSimpleResponse toSimpleResponse(Usuario usuario) {
        return UsuarioSimpleResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .rol(usuario.getRol().name())
                .activo(usuario.getActivo())
                .build();
    }
}