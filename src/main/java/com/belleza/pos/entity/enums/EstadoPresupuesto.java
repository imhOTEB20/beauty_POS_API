package com.belleza.pos.entity.enums;

/**
 * Enum para estados de presupuesto
 */
public enum EstadoPresupuesto {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    RECHAZADO("Rechazado"),
    CONVERTIDO_VENTA("Convertido a Venta");

    private final String descripcion;

    EstadoPresupuesto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}