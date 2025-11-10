package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateProveedorRequest;
import com.belleza.pos.dto.request.UpdateProveedorRequest;
import com.belleza.pos.dto.response.ProveedorResponse;
import com.belleza.pos.dto.response.ProveedorSimpleResponse;
import com.belleza.pos.entity.Proveedor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper para convertir entre entidades Proveedor y DTOs
 */
@Component
public class ProveedorMapper {

    public Proveedor toEntity(CreateProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setNroProveedor(request.nroProveedor());
        proveedor.setRazonSocial(request.razonSocial());
        proveedor.setNombreComercial(request.nombreComercial());
        proveedor.setCuit(request.cuit());
        proveedor.setTelefono(request.telefono());
        proveedor.setCelular(request.celular());
        proveedor.setEmail(request.email());
        proveedor.setPaginaWeb(request.paginaWeb());
        proveedor.setPersonaContacto(request.personaContacto());
        proveedor.setCalle(request.calle());
        proveedor.setNumero(request.numero());
        proveedor.setLocalidad(request.localidad());
        proveedor.setProvincia(request.provincia());
        proveedor.setCodigoPostal(request.codigoPostal());
        proveedor.setInformacionDomicilioAdicional(request.informacionDomicilioAdicional());
        proveedor.setCuentaCorrienteHabilitada(request.cuentaCorrienteHabilitada());
        proveedor.setLimiteCredito(request.limiteCredito());
        proveedor.setDiasPlazoPago(request.diasPlazoPago());
        proveedor.setCondicionIva(request.condicionIva());
        proveedor.setIngresosBrutos(request.ingresosBrutos());
        proveedor.setObservaciones(request.observaciones());
        proveedor.setActivo(request.activo() != null ? request.activo() : true);
        return proveedor;
    }

    public void updateEntity(Proveedor proveedor, UpdateProveedorRequest request) {
        if (request.razonSocial() != null) {
            proveedor.setRazonSocial(request.razonSocial());
        }
        if (request.nombreComercial() != null) {
            proveedor.setNombreComercial(request.nombreComercial());
        }
        if (request.telefono() != null) {
            proveedor.setTelefono(request.telefono());
        }
        if (request.celular() != null) {
            proveedor.setCelular(request.celular());
        }
        if (request.email() != null) {
            proveedor.setEmail(request.email());
        }
        if (request.paginaWeb() != null) {
            proveedor.setPaginaWeb(request.paginaWeb());
        }
        if (request.personaContacto() != null) {
            proveedor.setPersonaContacto(request.personaContacto());
        }
        if (request.calle() != null) {
            proveedor.setCalle(request.calle());
        }
        if (request.numero() != null) {
            proveedor.setNumero(request.numero());
        }
        if (request.localidad() != null) {
            proveedor.setLocalidad(request.localidad());
        }
        if (request.provincia() != null) {
            proveedor.setProvincia(request.provincia());
        }
        if (request.codigoPostal() != null) {
            proveedor.setCodigoPostal(request.codigoPostal());
        }
        if (request.informacionDomicilioAdicional() != null) {
            proveedor.setInformacionDomicilioAdicional(request.informacionDomicilioAdicional());
        }
        if (request.cuentaCorrienteHabilitada() != null) {
            proveedor.setCuentaCorrienteHabilitada(request.cuentaCorrienteHabilitada());
        }
        if (request.limiteCredito() != null) {
            proveedor.setLimiteCredito(request.limiteCredito());
        }
        if (request.diasPlazoPago() != null) {
            proveedor.setDiasPlazoPago(request.diasPlazoPago());
        }
        if (request.condicionIva() != null) {
            proveedor.setCondicionIva(request.condicionIva());
        }
        if (request.ingresosBrutos() != null) {
            proveedor.setIngresosBrutos(request.ingresosBrutos());
        }
        if (request.observaciones() != null) {
            proveedor.setObservaciones(request.observaciones());
        }
        if (request.activo() != null) {
            proveedor.setActivo(request.activo());
        }
    }

    public ProveedorResponse toResponse(Proveedor proveedor, Long cantidadArticulos) {
        String direccionCompleta = "";
        if (proveedor.getCalle() != null && !proveedor.getCalle().isEmpty()) {
            direccionCompleta = proveedor.getCalle();
            if (proveedor.getNumero() != null && !proveedor.getNumero().isEmpty()) {
                direccionCompleta += " " + proveedor.getNumero();
            }
            if (proveedor.getLocalidad() != null && !proveedor.getLocalidad().isEmpty()) {
                direccionCompleta += ", " + proveedor.getLocalidad();
            }
            if (proveedor.getProvincia() != null && !proveedor.getProvincia().isEmpty()) {
                direccionCompleta += ", " + proveedor.getProvincia();
            }
        }

        BigDecimal creditoDisponible = proveedor.getLimiteCredito().subtract(proveedor.getSaldoActual());
        String estadoCredito = "SIN_CUENTA_CORRIENTE";

        if (proveedor.getCuentaCorrienteHabilitada()) {
            if (proveedor.getSaldoActual().compareTo(proveedor.getLimiteCredito()) > 0) {
                estadoCredito = "EXCEDIDO";
            } else if (proveedor.getSaldoActual().compareTo(proveedor.getLimiteCredito().multiply(BigDecimal.valueOf(0.8))) > 0) {
                estadoCredito = "ALERTA";
            } else {
                estadoCredito = "NORMAL";
            }
        }

        return ProveedorResponse.builder()
                .idProveedor(proveedor.getIdProveedor())
                .nroProveedor(proveedor.getNroProveedor())
                .razonSocial(proveedor.getRazonSocial())
                .nombreComercial(proveedor.getNombreComercial())
                .cuit(proveedor.getCuit())
                .telefono(proveedor.getTelefono())
                .celular(proveedor.getCelular())
                .email(proveedor.getEmail())
                .paginaWeb(proveedor.getPaginaWeb())
                .personaContacto(proveedor.getPersonaContacto())
                .calle(proveedor.getCalle())
                .numero(proveedor.getNumero())
                .localidad(proveedor.getLocalidad())
                .provincia(proveedor.getProvincia())
                .codigoPostal(proveedor.getCodigoPostal())
                .direccionCompleta(direccionCompleta)
                .informacionDomicilioAdicional(proveedor.getInformacionDomicilioAdicional())
                .cuentaCorrienteHabilitada(proveedor.getCuentaCorrienteHabilitada())
                .limiteCredito(proveedor.getLimiteCredito())
                .saldoActual(proveedor.getSaldoActual())
                .creditoDisponible(creditoDisponible)
                .diasPlazoPago(proveedor.getDiasPlazoPago())
                .condicionIva(proveedor.getCondicionIva())
                .ingresosBrutos(proveedor.getIngresosBrutos())
                .estadoCredito(estadoCredito)
                .observaciones(proveedor.getObservaciones())
                .activo(proveedor.getActivo())
                .cantidadArticulos(cantidadArticulos)
                .fechaCreacion(proveedor.getFechaCreacion())
                .fechaModificacion(proveedor.getFechaModificacion())
                .build();
    }

    public ProveedorSimpleResponse toSimpleResponse(Proveedor proveedor) {
        return ProveedorSimpleResponse.builder()
                .idProveedor(proveedor.getIdProveedor())
                .nroProveedor(proveedor.getNroProveedor())
                .razonSocial(proveedor.getRazonSocial())
                .nombreComercial(proveedor.getNombreComercial())
                .cuit(proveedor.getCuit())
                .telefono(proveedor.getTelefono())
                .saldoActual(proveedor.getSaldoActual())
                .activo(proveedor.getActivo())
                .build();
    }
}