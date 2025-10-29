package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateRubroRequest;
import com.belleza.pos.dto.request.UpdateRubroRequest;
import com.belleza.pos.dto.response.RubroResponse;
import com.belleza.pos.dto.response.RubroSimpleResponse;
import com.belleza.pos.entity.Rubro;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades Rubro y DTOs
 */
@Component
public class RubroMapper {

    public Rubro toEntity(CreateRubroRequest request) {
        Rubro rubro = new Rubro();
        rubro.setNombre(request.nombre());
        rubro.setDescripcion(request.descripcion());
        rubro.setIvaPorcentaje(request.ivaPorcentaje());
        rubro.setActivo(request.activo() != null ? request.activo() : true);
        return rubro;
    }

    public void updateEntity(Rubro rubro, UpdateRubroRequest request) {
        if (request.nombre() != null) {
            rubro.setNombre(request.nombre());
        }
        if (request.descripcion() != null) {
            rubro.setDescripcion(request.descripcion());
        }
        if (request.ivaPorcentaje() != null) {
            rubro.setIvaPorcentaje(request.ivaPorcentaje());
        }
        if (request.activo() != null) {
            rubro.setActivo(request.activo());
        }
    }

    public RubroResponse toResponse(Rubro rubro, Long cantidadArticulos) {
        return RubroResponse.builder()
                .idRubro(rubro.getIdRubro())
                .nombre(rubro.getNombre())
                .descripcion(rubro.getDescripcion())
                .ivaPorcentaje(rubro.getIvaPorcentaje())
                .activo(rubro.getActivo())
                .cantidadArticulos(cantidadArticulos)
                .fechaCreacion(rubro.getFechaCreacion())
                .fechaModificacion(rubro.getFechaModificacion())
                .build();
    }

    public RubroSimpleResponse toSimpleResponse(Rubro rubro) {
        return RubroSimpleResponse.builder()
                .idRubro(rubro.getIdRubro())
                .nombre(rubro.getNombre())
                .activo(rubro.getActivo())
                .build();
    }
}
