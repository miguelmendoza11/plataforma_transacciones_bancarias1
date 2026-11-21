package com.banco.ledger.notification.infrastructure.config;

import com.banco.ledger.notification.domain.DirectorioEnMemoria;
import com.banco.ledger.notification.domain.DirectorioUsuarios;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elige la Strategy de directorio de usuarios (el dominio no conoce Spring). */
@Configuration
public class RouterConfig {

    @Bean
    DirectorioUsuarios directorioUsuarios() {
        return new DirectorioEnMemoria();
    }
}
