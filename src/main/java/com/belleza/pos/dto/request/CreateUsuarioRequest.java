package com.belleza.pos.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear un nuevo usuario
 * @param username
 * @param password
 * @param nombre
 * @param apellido
 * @param email
 * @param rol
 * @param idSucursal
 * @param activo
 */
public record CreateUsuarioRequest(

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 50, message = "El username debe tener entre 3 y 50 caracteres")
    String username,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    String password,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    String apellido,

    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    String email,

    @NotBlank(message = "El rol es obligatorio")
    String rol,

    Integer idSucursal,

    Boolean activo
) {
    public CreateUsuarioRequest(String username, String password, String nombre, String apellido, String email, String rol, Integer idSucursal) {
        this(username, password, nombre, apellido, email, rol, idSucursal, true);
    }
}