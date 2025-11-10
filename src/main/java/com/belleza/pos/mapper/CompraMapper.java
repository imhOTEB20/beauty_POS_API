package com.belleza.pos.mapper;

import com.belleza.pos.dto.response.CompraDetalleResponse;
import com.belleza.pos.dto.response.CompraResponse;
import com.belleza.pos.dto.response.CompraSimpleResponse;
import com.belleza.pos.entity.Compra;
import com.belleza.pos.entity.CompraDetalle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Compra y DTOs
 */
@Component
public class CompraMapper {

    /**
     * Convierte Compra a CompraResponse
     */
    public CompraResponse toResponse(Compra compra) {
        CompraResponse response = CompraResponse.builder()
                .idCompra(compra.getIdCompra())
                .idProveedor(compra.getProveedor().getIdProveedor())
                .razonSocialProveedor(compra.getProveedor().getRazonSocial())
                .nombreComercialProveedor(compra.getProveedor().getNombreComercial())
                .cuitProveedor(compra.getProveedor().getCuit())
                .idSucursal(compra.getSucursal().getIdSucursal())
                .nombreSucursal(compra.getSucursal().getNombre())
                .idUsuario(compra.getUsuario().getIdUsuario())
                .nombreUsuario(compra.getUsuario().getNombre())
                .apellidoUsuario(compra.getUsuario().getApellido())
                .tipoComprobante(compra.getTipoComprobante().name())
                .tipoComprobanteDescripcion(compra.getTipoComprobante().getDescripcion())
                .nroComprobante(compra.getNroComprobante())
                .fechaCompra(compra.getFechaCompra())
                .subtotal(compra.getSubtotal())
                .impuestosInternos(compra.getImpuestosInternos())
                .iva21(compra.getIva21())
                .iva105(compra.getIva105())
                .total(compra.getTotal())
                .actualizarPrecios(compra.getActualizarPrecios())
                .actualizarStock(compra.getActualizarStock())
                .formaPago(compra.getFormaPago().name())
                .formaPagoDescripcion(compra.getFormaPago().getDescripcion())
                .estado(compra.getEstado().name())
                .estadoDescripcion(compra.getEstado().getDescripcion())
                .fechaCreacion(compra.getFechaCreacion())
                .observaciones(compra.getObservaciones())
                .build();

        // Mapear detalles
        if (compra.getDetalles() != null && !compra.getDetalles().isEmpty()) {
            response.setDetalles(compra.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
            response.setCantidadArticulos(compra.getDetalles().size());
        } else {
            response.setDetalles(new ArrayList<>());
            response.setCantidadArticulos(0);
        }

        return response;
    }

    /**
     * Convierte CompraDetalle a CompraDetalleResponse
     */
    public CompraDetalleResponse toDetalleResponse(CompraDetalle detalle) {
        return CompraDetalleResponse.builder()
                .idDetalle(detalle.getIdDetalle())
                .numeroLinea(detalle.getNumeroLinea())
                .idArticulo(detalle.getArticulo().getIdArticulo())
                .codigoBarras(detalle.getCodigoBarras())
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .precioSinIva(detalle.getPrecioSinIva())
                .porcentajeIva(detalle.getPorcentajeIva())
                .precioUnitarioConIva(detalle.getPrecioUnitarioConIva())
                .totalSinImpuestos(detalle.getTotalSinImpuestos())
                .build();
    }

    /**
     * Convierte Compra a CompraSimpleResponse
     */
    public CompraSimpleResponse toSimpleResponse(Compra compra) {
        return CompraSimpleResponse.builder()
                .idCompra(compra.getIdCompra())
                .nroComprobante(compra.getNroComprobante())
                .tipoComprobante(compra.getTipoComprobante().getDescripcion())
                .razonSocialProveedor(compra.getProveedor().getRazonSocial())
                .nombreSucursal(compra.getSucursal().getNombre())
                .fechaCompra(compra.getFechaCompra())
                .total(compra.getTotal())
                .estado(compra.getEstado().getDescripcion())
                .build();
    }
}
