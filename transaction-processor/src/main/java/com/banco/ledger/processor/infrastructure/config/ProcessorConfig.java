package com.banco.ledger.processor.infrastructure.config;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.processor.domain.validation.FondosSuficientesRule;
import com.banco.ledger.processor.domain.validation.MontoMaximoPorTransaccionRule;
import com.banco.ledger.processor.domain.validation.ReglaValidacion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Clock;

/**
 * Registra como beans las piezas de dominio (Java puro, sin anotaciones de Spring).
 * Spring junta todos los beans ReglaValidacion en la List que recibe el servicio:
 * agregar una regla es agregar un @Bean aqui, sin tocar el procesador (Open/Closed).
 */
@Configuration
public class ProcessorConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ReglaValidacion fondosSuficientesRule() {
        return new FondosSuficientesRule();
    }

    @Bean
    ReglaValidacion montoMaximoPorTransaccionRule(@Value("${ledger.monto-maximo:100000}") BigDecimal maximo) {
        return new MontoMaximoPorTransaccionRule(MontoDinero.de(maximo));
    }
}
