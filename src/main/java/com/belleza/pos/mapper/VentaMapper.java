package com.belleza.pos.mapper;

import com.belleza.pos.dto.response.VentaDetalleResponse;
import com.belleza.pos.dto.response.VentaFormaPagoResponse;
import com.belleza.pos.dto.response.VentaResponse;
import com.belleza.pos.dto.response.VentaSimpleResponse;
import com.belleza.pos.entity.Venta;
import com.belleza.pos.entity.VentaDetalle;
import com.belleza.pos.entity.VentaFormaPago;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Venta y DTOs
 */
@Component
public class VentaMapper {

    /**
     * Convierte Venta a VentaResponse
     */
    public VentaResponse toResponse(Venta venta) {
        VentaResponse response = VentaResponse.builder()
                .idVenta(venta.getIdVenta())
                .nroTransaccion(venta.getNroTransaccion())
                .nroCaja(venta.getNroCaja())
                .idSucursal(venta.getSucursal().getIdSucursal())
                .nombreSucursal(venta.getSucursal().getNombre())
                .idUsuario(venta.getUsuario().getIdUsuario())
                .nombreUsuario(venta.getUsuario().getNombre())
                .apellidoUsuario(venta.getUsuario().getApellido())
                .tipoComprobante(venta.getTipoComprobante().name())
                .tipoComprobanteDescripcion(venta.getTipoComprobante().getDescripcion())
                .nroComprobante(venta.getNroComprobante())
                .cae(venta.getCae())
                .fechaVencimientoCae(venta.getFechaVencimientoCae())
                .idListaPrecio(venta.getListaPrecio().getIdLista())
                .nombreListaPrecio(venta.getListaPrecio().getNombre())
                .subtotal(venta.getSubtotal())
                .descuentoPorcentaje(venta.getDescuentoPorcentaje())
                .descuentoMonto(venta.getDescuentoMonto())
                .recargoPorcentaje(venta.getRecargoPorcentaje())
                .recargoMonto(venta.getRecargoMonto())
                .total(venta.getTotal())
                .estado(venta.getEstado().name())
                .estadoDescripcion(venta.getEstado().getDescripcion())
                .fechaVenta(venta.getFechaVenta())
                .observaciones(venta.getObservaciones())
                .build();

        // Cliente (opcional)
        if (venta.getCliente() != null) {
            response.setIdCliente(venta.getCliente().getIdCliente());
            response.setNombreCliente(venta.getCliente().getNombre());
            response.setApellidoCliente(venta.getCliente().getApellido());
            response.setNroDocumentoCliente(venta.getCliente().getNroDocumento());
        }

        // Mapear detalles
        if (venta.getDetalles() != null && !venta.getDetalles().isEmpty()) {
            response.setDetalles(venta.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
            response.setCantidadArticulos(venta.getDetalles().size());
        } else {
            response.setDetalles(new ArrayList<>());
            response.setCantidadArticulos(0);
        }

        // Mapear formas de pago
        if (venta.getFormasPago() != null && !venta.getFormasPago().isEmpty()) {
            response.setFormasPago(venta.getFormasPago().stream()
                    .map(this::toFormaPagoResponse)
                    .collect(Collectors.toList()));

            // Calcular total pagado
            BigDecimal totalPagado = venta.getFormasPago().stream()
                    .map(VentaFormaPago::getMonto)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            response.setTotalPagado(totalPagado);

            // Calcular vuelto si corresponde
            BigDecimal vuelto = totalPagado.subtract(venta.getTotal());
            if (vuelto.compareTo(BigDecimal.ZERO) > 0) {
                response.setVuelto(vuelto);
            } else {
                response.setVuelto(BigDecimal.ZERO);
            }
        } else {
            response.setFormasPago(new ArrayList<>());
            response.setTotalPagado(BigDecimal.ZERO);
            response.setVuelto(BigDecimal.ZERO);
        }

        return response;
    }

    /**
     * Convierte VentaDetalle a VentaDetalleResponse
     */
    public VentaDetalleResponse toDetalleResponse(VentaDetalle detalle) {
        return VentaDetalleResponse.builder()
                .idDetalle(detalle.getIdDetalle())
                .numeroLinea(detalle.getNumeroLinea())
                .idArticulo(detalle.getArticulo().getIdArticulo())
                .codigoBarras(detalle.getCodigoBarras())
                .descripcion(detalle.getDescripcion())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .descuentoPorcentaje(detalle.getDescuentoPorcentaje())
                .descuentoMonto(detalle.getDescuentoMonto())
                .subtotal(detalle.getSubtotal())
                .build();
    }

    /**
     * Convierte VentaFormaPago a VentaFormaPagoResponse
     */
    public VentaFormaPagoResponse toFormaPagoResponse(VentaFormaPago formaPago) {
        return VentaFormaPagoResponse.builder()
                .id(formaPago.getId())
                .formaPago(formaPago.getFormaPago().name())
                .formaPagoDescripcion(formaPago.getFormaPago().getDescripcion())
                .monto(formaPago.getMonto())
                .detalle(formaPago.getDetalle())
                .build();
    }

    /**
     * Convierte Venta a VentaSimpleResponse
     */
    public VentaSimpleResponse toSimpleResponse(Venta venta) {
        String nombreCliente = null;
        if (venta.getCliente() != null) {
            nombreCliente = venta.getCliente().getNombre() +
                    (venta.getCliente().getApellido() != null ?
                            " " + venta.getCliente().getApellido() : "");
        }

        return VentaSimpleResponse.builder()
                .idVenta(venta.getIdVenta())
                .nroTransaccion(venta.getNroTransaccion())
                .nroComprobante(venta.getNroComprobante())
                .tipoComprobante(venta.getTipoComprobante().getDescripcion())
                .nombreCliente(nombreCliente)
                .nombreSucursal(venta.getSucursal().getNombre())
                .total(venta.getTotal())
                .estado(venta.getEstado().getDescripcion())
                .fechaVenta(venta.getFechaVenta())
                .build();
    }
}