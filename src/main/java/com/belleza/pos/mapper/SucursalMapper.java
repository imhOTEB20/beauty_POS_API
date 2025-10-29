package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateSucursalRequest;
import com.belleza.pos.dto.request.UpdateSucursalRequest;
import com.belleza.pos.dto.response.SucursalResponse;
import com.belleza.pos.dto.response.SucursalSimpleResponse;
import com.belleza.pos.entity.Sucursal;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades Sucursal y DTOs
 */
@Component
public class SucursalMapper {

    /**
     * Convierte CreateSucursalRequest a Sucursal
     */
    public Sucursal toEntity(CreateSucursalRequest request) {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(request.nombre());
        sucursal.setDireccion(request.direccion());
        sucursal.setTelefono(request.telefono());
        sucursal.setEmail(request.email());
        sucursal.setActivo(request.activo() != null ? request.activo() : true);
        return sucursal;
    }

    /**
     * Actualiza una Sucursal existente con UpdateSucursalRequest
     */
    public void updateEntity(Sucursal sucursal, UpdateSucursalRequest request) {
        if (request.nombre() != null) {
            sucursal.setNombre(request.nombre());
        }
        if (request.direccion() != null) {
            sucursal.setDireccion(request.direccion());
        }
        if (request.telefono() != null) {
            sucursal.setTelefono(request.telefono());
        }
        if (request.email() != null) {
            sucursal.setEmail(request.email());
        }
        if (request.activo() != null) {
            sucursal.setActivo(request.activo());
        }
    }

    /**
     * Convierte Sucursal a SucursalResponse
     */
    public SucursalResponse toResponse(Sucursal sucursal) {
        return SucursalResponse.builder()
                .idSucursal(sucursal.getIdSucursal())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .telefono(sucursal.getTelefono())
                .email(sucursal.getEmail())
                .activo(sucursal.getActivo())
                .fechaCreacion(sucursal.getFechaCreacion())
                .fechaModificacion(sucursal.getFechaModificacion())
                .build();
    }

    /**
     * Convierte Sucursal a SucursalSimpleResponse
     */
    public SucursalSimpleResponse toSimpleResponse(Sucursal sucursal) {
        return SucursalSimpleResponse.builder()
                .idSucursal(sucursal.getIdSucursal())
                .nombre(sucursal.getNombre())
                .telefono(sucursal.getTelefono())
                .activo(sucursal.getActivo())
                .build();
    }
}
