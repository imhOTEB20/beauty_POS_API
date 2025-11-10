package com.belleza.pos.entity.enums;

/**
 * Enum para formas de pago
 */
public enum FormaPago {
    EFECTIVO("Efectivo"),
    TARJETA_DEBITO("Tarjeta de Débito"),
    TARJETA_CREDITO("Tarjeta de Crédito"),
    CHEQUE("Cheque"),
    TRANSFERENCIA("Transferencia Bancaria"),
    CUENTA_CORRIENTE("Cuenta Corriente"),
    TICKET("Ticket/Vale");

    private final String descripcion;

    FormaPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}