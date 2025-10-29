package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateClienteRequest;
import com.belleza.pos.dto.request.UpdateClienteRequest;
import com.belleza.pos.dto.response.ClienteResponse;
import com.belleza.pos.dto.response.ClienteSimpleResponse;
import com.belleza.pos.entity.Cliente;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mapper para convertir entre entidades Cliente y DTOs
 */
@Component
public class ClienteMapper {

    public Cliente toEntity(CreateClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNroCliente(request.nroCliente());
        cliente.setTipoCuenta(request.tipoCuenta());
        cliente.setNombre(request.nombre());
        cliente.setApellido(request.apellido());
        cliente.setTipoDocumento(request.tipoDocumento());
        cliente.setNroDocumento(request.nroDocumento());
        cliente.setTelefono(request.telefono());
        cliente.setCelular(request.celular());
        cliente.setEmail(request.email());
        cliente.setCalle(request.calle());
        cliente.setNumero(request.numero());
        cliente.setLocalidad(request.localidad());
        cliente.setProvincia(request.provincia());
        cliente.setCodigoPostal(request.codigoPostal());
        cliente.setCuentaCorrienteHabilitada(request.cuentaCorrienteHabilitada());
        cliente.setTipoLimite(request.tipoLimite());
        cliente.setLimiteCredito(request.limiteCredito());
        cliente.setDiasPlazoPago(request.diasPlazoPago());
        cliente.setCondicionIva(request.condicionIva());
        cliente.setObservaciones(request.observaciones());
        cliente.setActivo(request.activo() != null ? request.activo() : true);
        return cliente;
    }

    public void updateEntity(Cliente cliente, UpdateClienteRequest request) {
        if (request.nombre() != null) {
            cliente.setNombre(request.nombre());
        }
        if (request.apellido() != null) {
            cliente.setApellido(request.apellido());
        }
        if (request.telefono() != null) {
            cliente.setTelefono(request.telefono());
        }
        if (request.celular() != null) {
            cliente.setCelular(request.celular());
        }
        if (request.email() != null) {
            cliente.setEmail(request.email());
        }
        if (request.calle() != null) {
            cliente.setCalle(request.calle());
        }
        if (request.numero() != null) {
            cliente.setNumero(request.numero());
        }
        if (request.localidad() != null) {
            cliente.setLocalidad(request.localidad());
        }
        if (request.provincia() != null) {
            cliente.setProvincia(request.provincia());
        }
        if (request.codigoPostal() != null) {
            cliente.setCodigoPostal(request.codigoPostal());
        }
        if (request.cuentaCorrienteHabilitada() != null) {
            cliente.setCuentaCorrienteHabilitada(request.cuentaCorrienteHabilitada());
        }
        if (request.tipoLimite() != null) {
            cliente.setTipoLimite(request.tipoLimite());
        }
        if (request.limiteCredito() != null) {
            cliente.setLimiteCredito(request.limiteCredito());
        }
        if (request.diasPlazoPago() != null) {
            cliente.setDiasPlazoPago(request.diasPlazoPago());
        }
        if (request.condicionIva() != null) {
            cliente.setCondicionIva(request.condicionIva());
        }
        if (request.observaciones() != null) {
            cliente.setObservaciones(request.observaciones());
        }
        if (request.activo() != null) {
            cliente.setActivo(request.activo());
        }
    }

    public ClienteResponse toResponse(Cliente cliente) {
        String nombreCompleto = cliente.getNombre();
        if (cliente.getApellido() != null && !cliente.getApellido().isEmpty()) {
            nombreCompleto += " " + cliente.getApellido();
        }

        String direccionCompleta = "";
        if (cliente.getCalle() != null && !cliente.getCalle().isEmpty()) {
            direccionCompleta = cliente.getCalle();
            if (cliente.getNumero() != null && !cliente.getNumero().isEmpty()) {
                direccionCompleta += " " + cliente.getNumero();
            }
            if (cliente.getLocalidad() != null && !cliente.getLocalidad().isEmpty()) {
                direccionCompleta += ", " + cliente.getLocalidad();
            }
        }

        BigDecimal creditoDisponible = BigDecimal.ZERO;
        String estadoCredito = "SIN_CUENTA_CORRIENTE";

        if (cliente.getCuentaCorrienteHabilitada()) {
            if ("ILIMITADA".equals(cliente.getTipoLimite())) {
                estadoCredito = "ILIMITADO";
                creditoDisponible = BigDecimal.valueOf(Double.MAX_VALUE);
            } else {
                creditoDisponible = cliente.getLimiteCredito().subtract(cliente.getSaldoActual());

                if (cliente.getSaldoActual().compareTo(cliente.getLimiteCredito()) > 0) {
                    estadoCredito = "EXCEDIDO";
                } else if (cliente.getSaldoActual().compareTo(cliente.getLimiteCredito().multiply(BigDecimal.valueOf(0.8))) > 0) {
                    estadoCredito = "ALERTA";
                } else {
                    estadoCredito = "NORMAL";
                }
            }
        }

        return ClienteResponse.builder()
                .idCliente(cliente.getIdCliente())
                .nroCliente(cliente.getNroCliente())
                .tipoCuenta(cliente.getTipoCuenta())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .nombreCompleto(nombreCompleto)
                .tipoDocumento(cliente.getTipoDocumento())
                .nroDocumento(cliente.getNroDocumento())
                .telefono(cliente.getTelefono())
                .celular(cliente.getCelular())
                .email(cliente.getEmail())
                .calle(cliente.getCalle())
                .numero(cliente.getNumero())
                .localidad(cliente.getLocalidad())
                .provincia(cliente.getProvincia())
                .codigoPostal(cliente.getCodigoPostal())
                .direccionCompleta(direccionCompleta)
                .cuentaCorrienteHabilitada(cliente.getCuentaCorrienteHabilitada())
                .tipoLimite(cliente.getTipoLimite())
                .limiteCredito(cliente.getLimiteCredito())
                .saldoActual(cliente.getSaldoActual())
                .creditoDisponible(creditoDisponible)
                .diasPlazoPago(cliente.getDiasPlazoPago())
                .condicionIva(cliente.getCondicionIva())
                .estadoCredito(estadoCredito)
                .observaciones(cliente.getObservaciones())
                .activo(cliente.getActivo())
                .fechaCreacion(cliente.getFechaCreacion())
                .fechaModificacion(cliente.getFechaModificacion())
                .build();
    }

    public ClienteSimpleResponse toSimpleResponse(Cliente cliente) {
        String nombreCompleto = cliente.getNombre();
        if (cliente.getApellido() != null && !cliente.getApellido().isEmpty()) {
            nombreCompleto += " " + cliente.getApellido();
        }

        return ClienteSimpleResponse.builder()
                .idCliente(cliente.getIdCliente())
                .nroCliente(cliente.getNroCliente())
                .nombreCompleto(nombreCompleto)
                .nroDocumento(cliente.getNroDocumento())
                .telefono(cliente.getTelefono())
                .saldoActual(cliente.getSaldoActual())
                .activo(cliente.getActivo())
                .build();
    }
}
