package com.banco.ledger.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/** Beans generales de la API. El Clock se inyecta para poder fijar la hora en las pruebas. */
@Configuration
public class ApiConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
