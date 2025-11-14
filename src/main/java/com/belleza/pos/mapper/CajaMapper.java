package com.belleza.pos.mapper;

import com.belleza.pos.dto.request.CreateCajaRequest;
import com.belleza.pos.dto.request.UpdateCajaRequest;
import com.belleza.pos.dto.response.CajaResponse;
import com.belleza.pos.dto.response.CajaSimpleResponse;
import com.belleza.pos.dto.response.EstadoCajaResponse;
import com.belleza.pos.entity.Caja;
import com.belleza.pos.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Mapper para convertir entre entidades Caja y DTOs
 */
@Component
public class CajaMapper {

    /**
     * Convierte CreateCajaRequest a Caja
     */
    public Caja toEntity(CreateCajaRequest request, Sucursal sucursal) {
        Caja caja = new Caja();
        caja.setNumeroCaja(request.numeroCaja());
        caja.setSucursal(sucursal);
        caja.setDescripcion(request.descripcion());
        caja.setActivo(request.activo() != null ? request.activo() : true);
        return caja;
    }

    /**
     * Actualiza una Caja existente con UpdateCajaRequest
     */
    public void updateEntity(Caja caja, UpdateCajaRequest request, Sucursal sucursal) {
        if (request.numeroCaja() != null) {
            caja.setNumeroCaja(request.numeroCaja());
        }
        if (request.idSucursal() != null && sucursal != null) {
            caja.setSucursal(sucursal);
        }
        if (request.descripcion() != null) {
            caja.setDescripcion(request.descripcion());
        }
        if (request.activo() != null) {
            caja.setActivo(request.activo());
        }
    }

    /**
     * Convierte Caja a CajaResponse
     */
    public CajaResponse toResponse(Caja caja, BigDecimal saldoActual) {
        return CajaResponse.builder()
                .idCaja(caja.getIdCaja())
                .numeroCaja(caja.getNumeroCaja())
                .idSucursal(caja.getSucursal().getIdSucursal())
                .nombreSucursal(caja.getSucursal().getNombre())
                .descripcion(caja.getDescripcion())
                .activo(caja.getActivo())
                .saldoActual(saldoActual != null ? saldoActual : BigDecimal.ZERO)
                .fechaCreacion(caja.getFechaCreacion())
                .build();
    }

    /**
     * Convierte Caja a CajaSimpleResponse
     */
    public CajaSimpleResponse toSimpleResponse(Caja caja, BigDecimal saldoActual) {
        return CajaSimpleResponse.builder()
                .idCaja(caja.getIdCaja())
                .numeroCaja(caja.getNumeroCaja())
                .nombreSucursal(caja.getSucursal().getNombre())
                .descripcion(caja.getDescripcion())
                .activo(caja.getActivo())
                .saldoActual(saldoActual != null ? saldoActual : BigDecimal.ZERO)
                .build();
    }

    /**
     * Convierte Caja a EstadoCajaResponse
     */
    public EstadoCajaResponse toEstadoResponse(
            Caja caja,
            Boolean abierta,
            BigDecimal saldoActual,
            LocalDateTime fechaUltimoMovimiento,
            String tipoUltimoMovimiento,
            String nombreUsuario) {

        return EstadoCajaResponse.builder()
                .idCaja(caja.getIdCaja())
                .numeroCaja(caja.getNumeroCaja())
                .nombreSucursal(caja.getSucursal().getNombre())
                .activo(caja.getActivo())
                .abierta(abierta)
                .saldoActual(saldoActual != null ? saldoActual : BigDecimal.ZERO)
                .fechaUltimoMovimiento(fechaUltimoMovimiento)
                .tipoUltimoMovimiento(tipoUltimoMovimiento)
                .nombreUsuarioActual(nombreUsuario)
                .build();
    }
}