package com.belleza.pos.entity.enums;

/**
 * Enum para unidades de venta
 */
public enum UnidadVenta {
    UNIDAD("Unidad"),
    KG("Kilogramo");

    private final String descripcion;

    UnidadVenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}