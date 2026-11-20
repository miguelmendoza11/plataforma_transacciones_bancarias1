package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Saldo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Crea las cuentas de la demo (A, B, C, D, E con $1.000.000,00) al arrancar, solo si la tabla
 * "cuentas" esta vacia. Asi se pueden lanzar los flujos del enunciado ($50 y $20.000) sin
 * cargar datos a mano, y reiniciar el servicio no duplica ni reinicia los saldos.
 */
@Component
public class CuentasSemillaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(CuentasSemillaInitializer.class);
    private static final List<String> USUARIOS = List.of("A", "B", "C", "D", "E");
    private static final Saldo SALDO_INICIAL = Saldo.de("1000000.00");

    private final SpringDataCuentaRepository repository;

    public CuentasSemillaInitializer(SpringDataCuentaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            log.info("La tabla cuentas ya tiene datos, no se crean cuentas semilla");
            return;
        }
        List<CuentaJpaEntity> cuentas = USUARIOS.stream()
                .map(usuario -> CuentaMapper.toEntity(CuentaBancaria.abrir(usuario, SALDO_INICIAL)))
                .toList();
        repository.saveAll(cuentas);
        log.info("Cuentas semilla creadas: {} con saldo {}", USUARIOS, SALDO_INICIAL);
    }
}
