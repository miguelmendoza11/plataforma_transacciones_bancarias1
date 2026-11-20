package com.banco.ledger.dashboard.infrastructure.config;

import com.banco.ledger.dashboard.domain.VistaMaterializada;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra como bean la vista materializada (el dominio no lleva anotaciones de Spring). */
@Configuration
public class DashboardConfig {

    // un solo bean: los dos listeners comparten la misma vista en memoria
    @Bean
    VistaMaterializada vistaMaterializada() {
        return new VistaMaterializada();
    }
}
