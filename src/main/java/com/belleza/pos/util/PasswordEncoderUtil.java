package com.belleza.pos.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad para generar hashes de contraseñas BCrypt
 *
 * Ejecutar esta clase para generar el hash de una contraseña
 */
public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Contraseñas a encodear
        String[] passwords = {
                "admin123",
                "vendedor123",
                "cajero123",
                "gerente123"
        };

        System.out.println("=".repeat(80));
        System.out.println("GENERADOR DE HASHES BCrypt PARA CONTRASEÑAS");
        System.out.println("=".repeat(80));
        System.out.println();

        for (String password : passwords) {
            String hash = encoder.encode(password);
            System.out.println("Password: " + password);
            System.out.println("Hash:     " + hash);
            System.out.println();
            System.out.println("SQL UPDATE:");
            System.out.println("UPDATE usuarios SET password_hash = '" + hash + "' WHERE username = 'admin';");
            System.out.println("-".repeat(80));
            System.out.println();
        }

        System.out.println("=".repeat(80));
        System.out.println("NOTAS:");
        System.out.println("- Copia el hash generado y actualiza la base de datos");
        System.out.println("- BCrypt genera un hash diferente cada vez (es normal)");
        System.out.println("- Todos los hashes son válidos para la misma contraseña");
        System.out.println("=".repeat(80));
    }
}