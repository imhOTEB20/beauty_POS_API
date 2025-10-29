package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateListaPrecioRequest;
import com.belleza.pos.dto.request.UpdateListaPrecioRequest;
import com.belleza.pos.dto.response.ListaPrecioResponse;
import com.belleza.pos.dto.response.ListaPrecioSimpleResponse;
import com.belleza.pos.entity.ListaPrecio;
import org.springframework.stereotype.Component;

@Component
public class ListaPrecioMapper {

    public ListaPrecio toEntity(CreateListaPrecioRequest request) {
        ListaPrecio lista = new ListaPrecio();
        lista.setNombre(request.nombre());
        lista.setDescripcion(request.descripcion());
        lista.setActivo(request.activo() != null ? request.activo() : true);
        lista.setEsPredeterminada(request.esPredeterminada() != null ? request.esPredeterminada() : false);
        return lista;
    }

    public void updateEntity(ListaPrecio lista, UpdateListaPrecioRequest request) {
        if (request.nombre() != null) {
            lista.setNombre(request.nombre());
        }
        if (request.descripcion() != null) {
            lista.setDescripcion(request.descripcion());
        }
        if (request.activo() != null) {
            lista.setActivo(request.activo());
        }
        if (request.esPredeterminada() != null) {
            lista.setEsPredeterminada(request.esPredeterminada());
        }
    }

    public ListaPrecioResponse toResponse(ListaPrecio lista, Long cantidadArticulos) {
        return ListaPrecioResponse.builder()
                .idLista(lista.getIdLista())
                .nombre(lista.getNombre())
                .descripcion(lista.getDescripcion())
                .activo(lista.getActivo())
                .esPredeterminada(lista.getEsPredeterminada())
                .cantidadArticulosConPrecio(cantidadArticulos)
                .fechaCreacion(lista.getFechaCreacion())
                .fechaModificacion(lista.getFechaModificacion())
                .build();
    }

    public ListaPrecioSimpleResponse toSimpleResponse(ListaPrecio lista) {
        return ListaPrecioSimpleResponse.builder()
                .idLista(lista.getIdLista())
                .nombre(lista.getNombre())
                .esPredeterminada(lista.getEsPredeterminada())
                .activo(lista.getActivo())
                .build();
    }
}