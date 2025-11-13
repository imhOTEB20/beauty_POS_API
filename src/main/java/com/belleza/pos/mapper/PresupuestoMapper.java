package com.belleza.pos.mapper;

import com.belleza.pos.dto.response.PresupuestoDetalleResponse;
import com.belleza.pos.dto.response.PresupuestoResponse;
import com.belleza.pos.dto.response.PresupuestoSimpleResponse;
import com.belleza.pos.entity.Presupuesto;
import com.belleza.pos.entity.PresupuestoDetalle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Presupuesto y DTOs
 */
@Component
public class PresupuestoMapper {

    /**
     * Convierte Presupuesto a PresupuestoResponse
     */
    public PresupuestoResponse toResponse(Presupuesto presupuesto) {
        PresupuestoResponse response = PresupuestoResponse.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .nroPresupuesto(presupuesto.getNroPresupuesto())
                .idCliente(presupuesto.getCliente().getIdCliente())
                .nombreCliente(presupuesto.getCliente().getNombre())
                .apellidoCliente(presupuesto.getCliente().getApellido())
                .nroDocumentoCliente(presupuesto.getCliente().getNroDocumento())
                .idUsuario(presupuesto.getUsuario().getIdUsuario())
                .nombreUsuario(presupuesto.getUsuario().getNombre())
                .apellidoUsuario(presupuesto.getUsuario().getApellido())
                .idSucursal(presupuesto.getSucursal().getIdSucursal())
                .nombreSucursal(presupuesto.getSucursal().getNombre())
                .fechaPresupuesto(presupuesto.getFechaPresupuesto())
                .idListaPrecio(presupuesto.getListaPrecio().getIdLista())
                .nombreListaPrecio(presupuesto.getListaPrecio().getNombre())
                .subtotal(presupuesto.getSubtotal())
                .iva21(presupuesto.getIva21())
                .iva105(presupuesto.getIva105())
                .total(presupuesto.getTotal())
                .estado(presupuesto.getEstado().name())
                .estadoDescripcion(presupuesto.getEstado().getDescripcion())
                .fechaCreacion(presupuesto.getFechaCreacion())
                .observaciones(presupuesto.getObservaciones())
                .build();

        // Venta generada (si existe)
        if (presupuesto.getVentaGenerada() != null) {
            response.setIdVentaGenerada(presupuesto.getVentaGenerada().getIdVenta());
            response.setNroTransaccionVenta(presupuesto.getVentaGenerada().getNroTransaccion());
        }

        // Mapear detalles
        if (presupuesto.getDetalles() != null && !presupuesto.getDetalles().isEmpty()) {
            response.setDetalles(presupuesto.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
            response.setCantidadArticulos(presupuesto.getDetalles().size());
        } else {
            response.setDetalles(new ArrayList<>());
            response.setCantidadArticulos(0);
        }

        return response;
    }

    /**
     * Convierte PresupuestoDetalle a PresupuestoDetalleResponse
     */
    public PresupuestoDetalleResponse toDetalleResponse(PresupuestoDetalle detalle) {
        return PresupuestoDetalleResponse.builder()
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
     * Convierte Presupuesto a PresupuestoSimpleResponse
     */
    public PresupuestoSimpleResponse toSimpleResponse(Presupuesto presupuesto) {
        String nombreCliente = presupuesto.getCliente().getNombre() +
                (presupuesto.getCliente().getApellido() != null ?
                        " " + presupuesto.getCliente().getApellido() : "");

        return PresupuestoSimpleResponse.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .nroPresupuesto(presupuesto.getNroPresupuesto())
                .nombreCliente(nombreCliente)
                .nombreSucursal(presupuesto.getSucursal().getNombre())
                .fechaPresupuesto(presupuesto.getFechaPresupuesto())
                .total(presupuesto.getTotal())
                .estado(presupuesto.getEstado().getDescripcion())
                .build();
    }
}