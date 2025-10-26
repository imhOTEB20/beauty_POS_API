package com.belleza.pos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI/Swagger para documentación del API
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "POS Beauty System API",
                version = "1.0.0",
                description = "API REST para Sistema de Punto de Venta - Productos de Belleza y Cuidado Personal",
                contact = @Contact(
                        name = "Equipo de Desarrollo",
                        email = "dev@belleza.com"
                ),
                license = @License(
                        name = "Propietario",
                        url = "https://belleza.com/license"
                )
        ),
        servers = {
                @Server(
                        description = "Servidor Local",
                        url = "http://localhost:8080/api"
                ),
                @Server(
                        description = "Servidor de Desarrollo",
                        url = "https://dev-api.belleza.com/api"
                ),
                @Server(
                        description = "Servidor de Producción",
                        url = "https://api.belleza.com/api"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}