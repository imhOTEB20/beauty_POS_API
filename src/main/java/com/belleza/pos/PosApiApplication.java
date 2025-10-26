package com.belleza.pos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Clase principal de la aplicación POS Beauty System API
 *
 * @author Sistema POS
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class PosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosApiApplication.class, args);

        System.out.println("""
            
            ╔═══════════════════════════════════════════════════════════╗
            ║                                                           ║
            ║     POS BEAUTY SYSTEM API - INICIADO CORRECTAMENTE       ║
            ║                                                           ║
            ║     Swagger UI: http://localhost:8080/api/swagger-ui.html║
            ║     API Docs:   http://localhost:8080/api/api-docs       ║
            ║                                                           ║
            ╚═══════════════════════════════════════════════════════════╝
            
            """);
    }
}