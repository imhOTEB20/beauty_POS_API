package com.belleza.pos.entity.enums;

/**
 * Enum para estados de compra
 */
public enum EstadoCompra {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada"),
    ANULADA("Anulada");

    private final String descripcion;

    EstadoCompra(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}