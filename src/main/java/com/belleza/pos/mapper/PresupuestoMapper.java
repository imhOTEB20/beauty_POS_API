package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreatePresupuestoRequest;
import com.belleza.pos.dto.request.PresupuestoDetalleRequest;
import com.belleza.pos.dto.request.UpdatePresupuestoRequest;
import com.belleza.pos.dto.response.PresupuestoDetalleResponse;
import com.belleza.pos.dto.response.PresupuestoResponse;
import com.belleza.pos.dto.response.PresupuestoSimpleResponse;
import com.belleza.pos.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Presupuesto y DTOs
 */
@Component
public class PresupuestoMapper {

    private static final int DIAS_VALIDEZ_DEFAULT = 30;

    /**
     * Convierte CreatePresupuestoRequest a Presupuesto
     */
    public Presupuesto toEntity(
            CreatePresupuestoRequest request,
            Cliente cliente,
            Usuario usuario,
            Sucursal sucursal,
            ListaPrecio listaPrecio) {

        Presupuesto presupuesto = new Presupuesto();
        presupuesto.setNroPresupuesto(request.nroPresupuesto());
        presupuesto.setCliente(cliente);
        presupuesto.setUsuario(usuario);
        presupuesto.setSucursal(sucursal);
        presupuesto.setFechaPresupuesto(request.fechaPresupuesto());
        presupuesto.setListaPrecio(listaPrecio);
        presupuesto.setObservaciones(request.observaciones());
        presupuesto.setEstado("PENDIENTE");

        return presupuesto;
    }

    /**
     * Actualiza un Presupuesto existente con UpdatePresupuestoRequest
     */
    public void updateEntity(
            Presupuesto presupuesto,
            UpdatePresupuestoRequest request,
            Cliente cliente,
            ListaPrecio listaPrecio) {

        if (request.nroPresupuesto() != null) {
            presupuesto.setNroPresupuesto(request.nroPresupuesto());
        }
        if (request.idCliente() != null && cliente != null) {
            presupuesto.setCliente(cliente);
        }
        if (request.fechaPresupuesto() != null) {
            presupuesto.setFechaPresupuesto(request.fechaPresupuesto());
        }
        if (request.idListaPrecio() != null && listaPrecio != null) {
            presupuesto.setListaPrecio(listaPrecio);
        }
        if (request.observaciones() != null) {
            presupuesto.setObservaciones(request.observaciones());
        }
        if (request.estado() != null) {
            presupuesto.setEstado(request.estado());
        }
    }

    /**
     * Convierte Presupuesto a PresupuestoResponse
     */
    public PresupuestoResponse toResponse(Presupuesto presupuesto) {
        String nombreCliente = presupuesto.getCliente().getNombre() + " " +
                (presupuesto.getCliente().getApellido() != null ?
                        presupuesto.getCliente().getApellido() : "");

        String documentoCliente = presupuesto.getCliente().getNroDocumento();

        // Calcular validez
        long diasDesdeCreacion = ChronoUnit.DAYS.between(
                presupuesto.getFechaPresupuesto(), LocalDate.now());
        boolean vigente = diasDesdeCreacion <= DIAS_VALIDEZ_DEFAULT &&
                "PENDIENTE".equals(presupuesto.getEstado());

        PresupuestoResponse response = PresupuestoResponse.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .nroPresupuesto(presupuesto.getNroPresupuesto())
                .idCliente(presupuesto.getCliente().getIdCliente())
                .nombreCliente(nombreCliente.trim())
                .documentoCliente(documentoCliente)
                .idUsuario(presupuesto.getUsuario().getIdUsuario())
                .nombreUsuario(presupuesto.getUsuario().getNombre() + " " +
                        presupuesto.getUsuario().getApellido())
                .idSucursal(presupuesto.getSucursal().getIdSucursal())
                .nombreSucursal(presupuesto.getSucursal().getNombre())
                .idListaPrecio(presupuesto.getListaPrecio().getIdLista())
                .nombreListaPrecio(presupuesto.getListaPrecio().getNombre())
                .fechaPresupuesto(presupuesto.getFechaPresupuesto())
                .subtotal(presupuesto.getSubtotal())
                .iva21(presupuesto.getIva21())
                .iva105(presupuesto.getIva105())
                .total(presupuesto.getTotal())
                .estado(presupuesto.getEstado())
                .idVentaGenerada(presupuesto.getIdVentaGenerada())
                .observaciones(presupuesto.getObservaciones())
                .fechaCreacion(presupuesto.getFechaCreacion())
                .diasValidez(DIAS_VALIDEZ_DEFAULT)
                .vigente(vigente)
                .build();

        // Mapear detalles
        if (presupuesto.getDetalles() != null && !presupuesto.getDetalles().isEmpty()) {
            response.setDetalles(presupuesto.getDetalles().stream()
                    .map(this::toDetalleResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setDetalles(new ArrayList<>());
        }

        return response;
    }

    /**
     * Convierte Presupuesto a PresupuestoSimpleResponse
     */
    public PresupuestoSimpleResponse toSimpleResponse(Presupuesto presupuesto) {
        String nombreCliente = presupuesto.getCliente().getNombre() + " " +
                (presupuesto.getCliente().getApellido() != null ?
                        presupuesto.getCliente().getApellido() : "");

        // Calcular validez
        long diasDesdeCreacion = ChronoUnit.DAYS.between(
                presupuesto.getFechaPresupuesto(), LocalDate.now());
        boolean vigente = diasDesdeCreacion <= DIAS_VALIDEZ_DEFAULT &&
                "PENDIENTE".equals(presupuesto.getEstado());

        return PresupuestoSimpleResponse.builder()
                .idPresupuesto(presupuesto.getIdPresupuesto())
                .nroPresupuesto(presupuesto.getNroPresupuesto())
                .fechaPresupuesto(presupuesto.getFechaPresupuesto())
                .nombreCliente(nombreCliente.trim())
                .total(presupuesto.getTotal())
                .estado(presupuesto.getEstado())
                .vigente(vigente)
                .build();
    }

    /**
     * Convierte PresupuestoDetalle a PresupuestoDetalleResponse
     */
    public PresupuestoDetalleResponse toDetalleResponse(PresupuestoDetalle detalle) {
        BigDecimal totalConIva = detalle.getPrecioUnitarioConIva().multiply(detalle.getCantidad());

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
                .totalConIva(totalConIva)
                .build();
    }

    /**
     * Convierte PresupuestoDetalleRequest a PresupuestoDetalle
     */
    public PresupuestoDetalle toDetalleEntity(
            PresupuestoDetalleRequest request,
            Presupuesto presupuesto,
            Articulo articulo) {

        PresupuestoDetalle detalle = new PresupuestoDetalle();
        detalle.setPresupuesto(presupuesto);
        detalle.setNumeroLinea(request.numeroLinea());
        detalle.setArticulo(articulo);
        detalle.setCodigoBarras(request.codigoBarras());
        detalle.setDescripcion(request.descripcion());
        detalle.setCantidad(request.cantidad());
        detalle.setPrecioSinIva(request.precioSinIva());
        detalle.setPorcentajeIva(request.porcentajeIva());
        detalle.setPrecioUnitarioConIva(request.calcularPrecioConIva());
        detalle.setTotalSinImpuestos(request.calcularTotalSinImpuestos());

        return detalle;
    }

    /**
     * Calcula los totales del presupuesto
     */
    public void calcularTotales(Presupuesto presupuesto) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal iva21 = BigDecimal.ZERO;
        BigDecimal iva105 = BigDecimal.ZERO;

        for (PresupuestoDetalle detalle : presupuesto.getDetalles()) {
            subtotal = subtotal.add(detalle.getTotalSinImpuestos());

            // Calcular IVA según porcentaje
            BigDecimal montoIva = detalle.getTotalSinImpuestos()
                    .multiply(detalle.getPorcentajeIva())
                    .divide(BigDecimal.valueOf(100));

            if (detalle.getPorcentajeIva().compareTo(BigDecimal.valueOf(21)) == 0) {
                iva21 = iva21.add(montoIva);
            } else if (detalle.getPorcentajeIva().compareTo(BigDecimal.valueOf(10.5)) == 0) {
                iva105 = iva105.add(montoIva);
            }
        }

        BigDecimal total = subtotal.add(iva21).add(iva105);

        presupuesto.setSubtotal(subtotal);
        presupuesto.setIva21(iva21);
        presupuesto.setIva105(iva105);
        presupuesto.setTotal(total);
    }
}