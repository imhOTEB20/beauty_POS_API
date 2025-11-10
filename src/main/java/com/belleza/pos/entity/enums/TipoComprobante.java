package com.belleza.pos.entity.enums;

public enum TipoComprobante {
    FACTURA_A("Factura_A"),
    FACTURA_B("Factura_B"),
    FACTURA_C("Factura_C"),
    NOTA_DEBITO("Nota_Debito"),
    NOTA_CREDITO("Nota_Credito");

    private final String descripcion;

    TipoComprobante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
