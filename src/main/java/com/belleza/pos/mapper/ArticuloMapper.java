package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateArticuloRequest;
import com.belleza.pos.dto.request.UpdateArticuloRequest;
import com.belleza.pos.dto.response.*;
import com.belleza.pos.entity.*;
import com.belleza.pos.entity.enums.UnidadVenta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Articulo y DTOs
 */
@Component
public class ArticuloMapper {

    /**
     * Convierte CreateArticuloRequest a Articulo
     */
    public Articulo toEntity(CreateArticuloRequest request, Rubro rubro) {
        Articulo articulo = new Articulo();
        articulo.setCodigoBarras(request.codigoBarras());
        articulo.setDescripcion(request.descripcion());
        articulo.setRubro(rubro);
        articulo.setUnidadVenta(UnidadVenta.valueOf(request.unidadVenta()));
        articulo.setUsaControlStock(request.usaControlStock());
        articulo.setStockActual(request.stockActual());
        articulo.setStockMinimo(request.stockMinimo());
        articulo.setStockMaximo(request.stockMaximo());
        articulo.setFechaVencimiento(request.fechaVencimiento());
        articulo.setImagenUrl(request.imagenUrl());
        articulo.setPublicarEnWeb(request.publicarEnWeb());
        articulo.setEnOferta(request.enOferta());
        articulo.setActivo(request.activo() != null ? request.activo() : true);
        return articulo;
    }

    /**
     * Actualiza un Articulo existente con UpdateArticuloRequest
     */
    public void updateEntity(Articulo articulo, UpdateArticuloRequest request, Rubro rubro) {
        if (request.codigoBarras() != null) {
            articulo.setCodigoBarras(request.codigoBarras());
        }
        if (request.descripcion() != null) {
            articulo.setDescripcion(request.descripcion());
        }
        if (request.idRubro() != null) {
            articulo.setRubro(rubro);
        }
        if (request.unidadVenta() != null) {
            articulo.setUnidadVenta(UnidadVenta.valueOf(request.unidadVenta()));
        }
        if (request.usaControlStock() != null) {
            articulo.setUsaControlStock(request.usaControlStock());
        }
        if (request.stockActual() != null) {
            articulo.setStockActual(request.stockActual());
        }
        if (request.stockMinimo() != null) {
            articulo.setStockMinimo(request.stockMinimo());
        }
        if (request.stockMaximo() != null) {
            articulo.setStockMaximo(request.stockMaximo());
        }
        if (request.fechaVencimiento() != null) {
            articulo.setFechaVencimiento(request.fechaVencimiento());
        }
        if (request.imagenUrl() != null) {
            articulo.setImagenUrl(request.imagenUrl());
        }
        if (request.publicarEnWeb() != null) {
            articulo.setPublicarEnWeb(request.publicarEnWeb());
        }
        if (request.enOferta() != null) {
            articulo.setEnOferta(request.enOferta());
        }
        if (request.activo() != null) {
            articulo.setActivo(request.activo());
        }
    }

    /**
     * Convierte Articulo a ArticuloResponse
     */
    public ArticuloResponse toResponse(Articulo articulo) {
        ArticuloResponse response = ArticuloResponse.builder()
                .idArticulo(articulo.getIdArticulo())
                .codigoBarras(articulo.getCodigoBarras())
                .descripcion(articulo.getDescripcion())
                .idRubro(articulo.getRubro() != null ? articulo.getRubro().getIdRubro() : null)
                .nombreRubro(articulo.getRubro() != null ? articulo.getRubro().getNombre() : null)
                .unidadVenta(articulo.getUnidadVenta().name())
                .usaControlStock(articulo.getUsaControlStock())
                .stockActual(articulo.getStockActual())
                .stockMinimo(articulo.getStockMinimo())
                .stockMaximo(articulo.getStockMaximo())
                .estadoStock(calcularEstadoStock(articulo))
                .fechaVencimiento(articulo.getFechaVencimiento())
                .imagenUrl(articulo.getImagenUrl())
                .publicarEnWeb(articulo.getPublicarEnWeb())
                .enOferta(articulo.getEnOferta())
                .activo(articulo.getActivo())
                .fechaCreacion(articulo.getFechaCreacion())
                .fechaModificacion(articulo.getFechaModificacion())
                .build();

        // Calcular días para vencer
        if (articulo.getFechaVencimiento() != null) {
            long dias = ChronoUnit.DAYS.between(LocalDate.now(), articulo.getFechaVencimiento());
            response.setDiasParaVencer((int) dias);
            response.setProximoAVencer(dias >= 0 && dias <= 30);
        }

        // Mapear precios
        if (articulo.getPrecios() != null && !articulo.getPrecios().isEmpty()) {
            response.setPrecios(articulo.getPrecios().stream()
                    .map(this::toPrecioResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setPrecios(new ArrayList<>());
        }

        // Mapear proveedores
        if (articulo.getProveedores() != null && !articulo.getProveedores().isEmpty()) {
            response.setProveedores(articulo.getProveedores().stream()
                    .map(this::toProveedorArticuloResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setProveedores(new ArrayList<>());
        }

        return response;
    }

    /**
     * Convierte Articulo a ArticuloSimpleResponse
     */
    public ArticuloSimpleResponse toSimpleResponse(Articulo articulo) {
        BigDecimal precioVenta = null;

        // Obtener precio de lista predeterminada
        if (articulo.getPrecios() != null) {
            precioVenta = articulo.getPrecios().stream()
                    .filter(p -> p.getListaPrecio().getEsPredeterminada())
                    .findFirst()
                    .map(ArticuloPrecio::getPrecioVenta)
                    .orElse(null);
        }

        return ArticuloSimpleResponse.builder()
                .idArticulo(articulo.getIdArticulo())
                .codigoBarras(articulo.getCodigoBarras())
                .descripcion(articulo.getDescripcion())
                .nombreRubro(articulo.getRubro() != null ? articulo.getRubro().getNombre() : null)
                .precioVenta(precioVenta)
                .stockActual(articulo.getStockActual())
                .activo(articulo.getActivo())
                .build();
    }

    /**
     * Convierte ArticuloPrecio a PrecioResponse
     */
    public PrecioResponse toPrecioResponse(ArticuloPrecio precio) {
        BigDecimal precioConIva = precio.getPrecioVenta().multiply(BigDecimal.valueOf(1.21));

        return PrecioResponse.builder()
                .idPrecio(precio.getIdPrecio())
                .idLista(precio.getListaPrecio().getIdLista())
                .nombreLista(precio.getListaPrecio().getNombre())
                .precioCosto(precio.getPrecioCosto())
                .precioVenta(precio.getPrecioVenta())
                .precioConIva(precioConIva)
                .porcentajeUtilidad(precio.getPorcentajeUtilidad())
                .fechaUltimaActualizacion(precio.getFechaUltimaActualizacion())
                .build();
    }

    /**
     * Convierte ArticuloProveedor a ProveedorArticuloResponse
     */
    public ProveedorArticuloResponse toProveedorArticuloResponse(ArticuloProveedor artProv) {
        return ProveedorArticuloResponse.builder()
                .id(artProv.getId())
                .idProveedor(artProv.getProveedor().getIdProveedor())
                .razonSocial(artProv.getProveedor().getRazonSocial())
                .nombreComercial(artProv.getProveedor().getNombreComercial())
                .costo(artProv.getCosto())
                .esPredeterminado(artProv.getEsPredeterminado())
                .ultimaActualizacion(artProv.getUltimaActualizacion())
                .build();
    }

    /**
     * Convierte Articulo a ArticuloStockBajoResponse
     */
    public ArticuloStockBajoResponse toStockBajoResponse(Articulo articulo) {
        BigDecimal diferencia = articulo.getStockMinimo().subtract(articulo.getStockActual());

        return ArticuloStockBajoResponse.builder()
                .idArticulo(articulo.getIdArticulo())
                .codigoBarras(articulo.getCodigoBarras())
                .descripcion(articulo.getDescripcion())
                .stockActual(articulo.getStockActual())
                .stockMinimo(articulo.getStockMinimo())
                .diferencia(diferencia)
                .estadoStock(calcularEstadoStock(articulo))
                .build();
    }

    /**
     * Convierte Articulo a ArticuloVencimientoResponse
     */
    public ArticuloVencimientoResponse toVencimientoResponse(Articulo articulo) {
        long diasRestantes = articulo.getFechaVencimiento() != null ?
                ChronoUnit.DAYS.between(LocalDate.now(), articulo.getFechaVencimiento()) : 0;

        String estadoVencimiento;
        if (diasRestantes < 0) {
            estadoVencimiento = "VENCIDO";
        } else if (diasRestantes <= 7) {
            estadoVencimiento = "CRITICO";
        } else if (diasRestantes <= 30) {
            estadoVencimiento = "PROXIMO";
        } else {
            estadoVencimiento = "OK";
        }

        return ArticuloVencimientoResponse.builder()
                .idArticulo(articulo.getIdArticulo())
                .codigoBarras(articulo.getCodigoBarras())
                .descripcion(articulo.getDescripcion())
                .fechaVencimiento(articulo.getFechaVencimiento())
                .diasRestantes((int) diasRestantes)
                .stockActual(articulo.getStockActual())
                .estadoVencimiento(estadoVencimiento)
                .build();
    }

    /**
     * Calcula el estado del stock
     */
    private String calcularEstadoStock(Articulo articulo) {
        if (!articulo.getUsaControlStock()) {
            return "SIN_CONTROL";
        }

        BigDecimal stock = articulo.getStockActual();
        BigDecimal minimo = articulo.getStockMinimo();

        if (stock.compareTo(BigDecimal.ZERO) == 0) {
            return "SIN_STOCK";
        } else if (stock.compareTo(minimo) < 0) {
            return "CRITICO";
        } else if (stock.compareTo(minimo) == 0) {
            return "BAJO";
        } else {
            return "OK";
        }
    }
}
