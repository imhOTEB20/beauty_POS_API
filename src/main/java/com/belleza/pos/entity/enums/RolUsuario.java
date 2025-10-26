package com.belleza.pos.entity.enums;

/**
 * Enum para roles de usuario
 */
public enum RolUsuario {
    ADMIN("Administrador"),
    VENDEDOR("Vendedor"),
    CAJERO("Cajero"),
    GERENTE("Gerente");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}