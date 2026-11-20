package com.banco.ledger.fraud.infrastructure.config;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.fraud.domain.rules.AltoValorRule;
import com.banco.ledger.fraud.domain.rules.FrecuenciaAnomalaRule;
import com.banco.ledger.fraud.domain.rules.ReglaFraude;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;

/** Registra el reloj y las reglas de fraude con los valores de fraud.* del application.yml. */
@Configuration
public class FraudConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ReglaFraude altoValorRule(@Value("${fraud.umbral-alto-valor}") BigDecimal umbral) {
        return new AltoValorRule(MontoDinero.de(umbral));
    }

    @Bean
    ReglaFraude frecuenciaAnomalaRule(@Value("${fraud.frecuencia.max-transacciones}") int maxTransacciones,
                                      @Value("${fraud.frecuencia.ventana-segundos}") long ventanaSegundos) {
        return new FrecuenciaAnomalaRule(maxTransacciones, Duration.ofSeconds(ventanaSegundos));
    }
}
