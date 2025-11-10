package com.belleza.pos.entity.enums;

/**
 * Enum para estados de venta
 */
public enum EstadoVenta {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    ANULADA("Anulada"),
    PRESUPUESTO("Presupuesto");

    private final String descripcion;

    EstadoVenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
