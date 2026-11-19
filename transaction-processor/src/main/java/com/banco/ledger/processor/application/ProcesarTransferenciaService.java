package com.banco.ledger.processor.application;

import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.processor.application.port.CuentaRepository;
import com.banco.ledger.processor.application.port.LedgerEventPublisher;
import com.banco.ledger.processor.application.port.TransaccionRepository;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Transaccion;
import com.banco.ledger.processor.domain.validation.ReglaValidacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Caso de uso principal ("el cerebro").
 *
 * DIP: depende solo de puertos (TransaccionRepository, CuentaRepository, LedgerEventPublisher),
 * inyectados por constructor; no conoce PostgreSQL, RabbitMQ ni Kafka.
 * STRATEGY: recibe List&lt;ReglaValidacion&gt; y aplica la primera que rechace, sin saber cuales son.
 * STATE: las transiciones las gobierna la propia Transaccion.
 */
@Service
public class ProcesarTransferenciaService {

    private static final Logger log = LoggerFactory.getLogger(ProcesarTransferenciaService.class);
    private static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";

    private final TransaccionRepository transaccionRepository;
    private final CuentaRepository cuentaRepository;
    private final LedgerEventPublisher publisher;
    private final List<ReglaValidacion> reglas;
    private final Clock clock;

    public ProcesarTransferenciaService(TransaccionRepository transaccionRepository,
                                        CuentaRepository cuentaRepository,
                                        LedgerEventPublisher publisher,
                                        List<ReglaValidacion> reglas,
                                        Clock clock) {
        this.transaccionRepository = Objects.requireNonNull(transaccionRepository);
        this.cuentaRepository = Objects.requireNonNull(cuentaRepository);
        this.publisher = Objects.requireNonNull(publisher);
        this.reglas = List.copyOf(reglas);
        this.clock = Objects.requireNonNull(clock);
    }

    @Transactional
    public void procesar(TransferCommandRecord cmd) {
        Objects.requireNonNull(cmd, "El comando no puede ser nulo");

        // 1. Idempotencia: RabbitMQ entrega "at-least-once", el mismo comando puede llegar dos veces
        if (transaccionRepository.existe(cmd.txId())) {
            log.info("Transaccion {} ya procesada, se ignora la reentrega", cmd.txId());
            return;
        }

        // 2. Crear la transaccion y pasarla a PROCESANDO
        Transaccion tx = Transaccion.recibir(cmd);
        tx.iniciarProcesamiento();

        // 3. Cargar cuentas; si falta alguna, se rechaza
        Optional<CuentaBancaria> origen = cuentaRepository.buscarPorUsuario(tx.origen());
        Optional<CuentaBancaria> destino = cuentaRepository.buscarPorUsuario(tx.destino());
        if (origen.isEmpty() || destino.isEmpty()) {
            tx.rechazar(ACCOUNT_NOT_FOUND);
        } else {
            // 4. Reglas (Strategy): la primera que no aprueba rechaza la transaccion
            aplicarReglas(tx, origen.get(), destino.get());
        }

        // 5. Persistir y publicar el resultado en el ledger
        transaccionRepository.guardar(tx);
        publisher.publicar(tx.toEvento(clock.instant()));
        log.info("Transaccion {} procesada con estado {}", tx.txId(), tx.estado());
    }

    private void aplicarReglas(Transaccion tx, CuentaBancaria origen, CuentaBancaria destino) {
        reglas.stream()
                .map(regla -> regla.evaluar(tx, origen))
                .filter(resultado -> !resultado.esAprobada())
                .findFirst()
                .ifPresentOrElse(
                        rechazo -> tx.rechazar(rechazo.motivo()),
                        () -> {
                            tx.ejecutarTransferencia(origen, destino);
                            cuentaRepository.guardar(origen);
                            cuentaRepository.guardar(destino);
                        });
    }
}
