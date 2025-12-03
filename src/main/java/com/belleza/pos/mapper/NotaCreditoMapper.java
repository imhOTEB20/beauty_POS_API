package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateNotaCreditoRequest;
import com.belleza.pos.dto.request.NotaCreditoDetalleRequest;
import com.belleza.pos.dto.request.UpdateNotaCreditoRequest;
import com.belleza.pos.dto.response.NotaCreditoDetalleResponse;
import com.belleza.pos.dto.response.NotaCreditoResponse;
import com.belleza.pos.dto.response.NotaCreditoSimpleResponse;
import com.belleza.pos.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades NotaCredito y DTOs
 */
@Component
public class NotaCreditoMapper {

    /**
     * Convierte CreateNotaCreditoRequest a NotaCredito
     */
    public NotaCredito toEntity(
            CreateNotaCreditoRequest request,
            Cliente cliente,
            Usuario usuario,
            Sucursal sucursal) {

        NotaCredito notaCredito = new NotaCredito();
        notaCredito.setTipoComprobante(request.tipoComprobante());
        notaCredito.setNroComprobante(request.nroComprobante());
        notaCredito.setFecha(request.fecha());
        notaCredito.setCliente(cliente);
        notaCredito.setUsuario(usuario);
        notaCredito.setSucursal(sucursal);
        notaCredito.setTipoComprobanteAsociado(request.tipoComprobanteAsociado());
        notaCredito.setPuntoVentaAsociado(request.puntoVentaAsociado());
        notaCredito.setNroComprobanteAsociado(request.nroComprobanteAsociado());
        notaCredito.setObservaciones(request.observaciones());
        notaCredito.setEstado("ACTIVA");

        // Calcular total
        BigDecimal total = request.detalles().stream()
                .map(NotaCreditoDetalleRequest::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        notaCredito.setTotal(total);

        return notaCredito;
    }

    /**
     * Actualiza una NotaCredito existente con UpdateNotaCreditoRequest
     */
    public void updateEntity(NotaCredito notaCredito, UpdateNotaCreditoRequest request, Cliente cliente) {
        if (request.tipoComprobante() != null) {
            notaCredito.setTipoComprobante(request.tipoComprobante());
        }
        if (request.nroComprobante() != null) {
            notaCredito.setNroComprobante(request.nroComprobante());
        }
        if (request.fecha() != null) {
            notaCredito.setFecha(request.fecha());
        }
        if (request.idCliente() != null && cliente != null) {
            notaCredito.setCliente(cliente);
        }
        if (request.tipoComprobanteAsociado() != null) {
            notaCredito.setTipoComprobanteAsociado(request.tipoComprobanteAsociado());
        }
        if (request.puntoVentaAsociado() != null) {
            notaCredito.setPuntoVentaAsociado(request.puntoVentaAsociado());
        }
        if (request.nroComprobanteAsociado() != null) {
            notaCredito.setNroComprobanteAsociado(request.nroComprobanteAsociado());
        }
        if (request.observaciones() != null) {
            notaCredito.setObservaciones(request.observaciones());
        }
        if (request.estado() != null) {
            notaCredito.setEstado(request.estado());
        }
    }

    /**
     * Convierte NotaCredito a NotaCreditoResponse
     */
    public NotaCreditoResponse toResponse(NotaCredito notaCredito) {
        String nombreCliente = notaCredito.getCliente() != null ?
                notaCredito.getCliente().getNombre() + " " +
                        (notaCredito.getCliente().getApellido() != null ? notaCredito.getCliente().getApellido() : "") :
                "Consumidor Final";

        String documentoCliente = notaCredito.getCliente() != null ?
                notaCredito.getCliente().getNroDocumento() : null;

        String comprobanteAsociado = null;
        if (notaCredito.getTipoComprobanteAsociado() != null) {
            comprobanteAsociado = String.format("%s %s-%s",
                    notaCredito.getTipoComprobanteAsociado(),
                    notaCredito.getPuntoVentaAsociado() != null ? notaCredito.getPuntoVentaAsociado() : "",
                    notaCredito.getNroComprobanteAsociado() != null ? notaCredito.getNroComprobanteAsociado() : ""
            ).trim();
        }

        NotaCreditoResponse response = NotaCreditoResponse.builder()
                .idNotaCredito(notaCredito.getIdNotaCredito())
                .tipoComprobante(notaCredito.getTipoComprobante())
                .nroComprobante(notaCredito.getNroComprobante())
                .fecha(notaCredito.getFecha())
                .idCliente(notaCredito.getCliente() != null ? notaCredito.getCliente().getIdCliente() : null)
                .nombreCliente(nombreCliente)
                .documentoCliente(documentoCliente)
                .idUsuario(notaCredito.getUsuario().getIdUsuario())
                .nombreUsuario(notaCredito.getUsuario().getNombre() + " " +
                        notaCredito.getUsuario().getApellido())
                .idSucursal(notaCredito.getSucursal().getIdSucursal())
                .nombreSucursal(notaCredito.getSucursal().getNombre())
                .tipoComprobanteAsociado(notaCredito.getTipoComprobanteAsociado())
                .puntoVentaAsociado(notaCredito.getPuntoVentaAsociado())
                .nroComprobanteAsociado(notaCredito.getNroComprobanteAsociado())
                .comprobanteAsociadoCompleto(comprobanteAsociado)
                .total(notaCredito.getTotal())
                .estado(notaCredito.getEstado())
                .observaciones(notaCredito.getObservaciones())
                .fechaCreacion(notaCredito.getFechaCreacion())
                .build();

        // Mapear detalles
        if (notaCredito.getDetalles() != null && !notaCredito.getDetalles().isEmpty()) {
            response.setDetalles(notaCredito.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setDetalles(new ArrayList<>());
        }

        return response;
    }

    /**
     * Convierte NotaCredito a NotaCreditoSimpleResponse
     */
    public NotaCreditoSimpleResponse toSimpleResponse(NotaCredito notaCredito) {
        String nombreCliente = notaCredito.getCliente() != null ?
                notaCredito.getCliente().getNombre() + " " +
                        (notaCredito.getCliente().getApellido() != null ? notaCredito.getCliente().getApellido() : "") :
                "Consumidor Final";

        return NotaCreditoSimpleResponse.builder()
                .idNotaCredito(notaCredito.getIdNotaCredito())
                .tipoComprobante(notaCredito.getTipoComprobante())
                .nroComprobante(notaCredito.getNroComprobante())
                .fecha(notaCredito.getFecha())
                .nombreCliente(nombreCliente.trim())
                .total(notaCredito.getTotal())
                .estado(notaCredito.getEstado())
                .build();
    }

    /**
     * Convierte NotaCreditoDetalle a NotaCreditoDetalleResponse
     */
    public NotaCreditoDetalleResponse toDetalleResponse(NotaCreditoDetalle detalle) {
        return NotaCreditoDetalleResponse.builder()
                .idDetalle(detalle.getIdDetalle())
                .idArticulo(detalle.getArticulo() != null ? detalle.getArticulo().getIdArticulo() : null)
                .codigoBarras(detalle.getCodigoBarras())
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .total(detalle.getTotal())
                .build();
    }

    /**
     * Convierte NotaCreditoDetalleRequest a NotaCreditoDetalle
     */
    public NotaCreditoDetalle toDetalleEntity(
            NotaCreditoDetalleRequest request,
            NotaCredito notaCredito,
            Articulo articulo) {

        NotaCreditoDetalle detalle = new NotaCreditoDetalle();
        detalle.setNotaCredito(notaCredito);
        detalle.setArticulo(articulo);
        detalle.setCodigoBarras(request.codigoBarras());
        detalle.setDescripcion(request.descripcion());
        detalle.setCantidad(request.cantidad());
        detalle.setPrecioUnitario(request.precioUnitario());
        detalle.setTotal(request.calcularTotal());

        return detalle;
    }
}