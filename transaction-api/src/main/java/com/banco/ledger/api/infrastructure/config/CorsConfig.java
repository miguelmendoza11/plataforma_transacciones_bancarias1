package com.banco.ledger.api.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** CORS: deja que dashboard.html (servido en :8081) haga POST /transfer desde el navegador. */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/transfer")
                .allowedOrigins("http://localhost:8081")
                .allowedMethods("POST");
    }
}
