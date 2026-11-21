package com.banco.ledger.fraud.application;

import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.fraud.application.port.AlertaFraudeRepository;
import com.banco.ledger.fraud.application.port.FraudAlertPublisher;
import com.banco.ledger.fraud.domain.model.AlertaFraude;
import com.banco.ledger.fraud.domain.rules.ReglaFraude;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Caso de uso: evalua cada evento COMPLETED con todas las reglas de fraude (Strategy). */
@Service
public class DetectarFraudeService {

    private final List<ReglaFraude> reglas;
    private final AlertaFraudeRepository alertaRepository;
    private final FraudAlertPublisher publisher;
    private final Clock clock;

    public DetectarFraudeService(List<ReglaFraude> reglas,
                                 AlertaFraudeRepository alertaRepository,
                                 FraudAlertPublisher publisher,
                                 Clock clock) {
        this.reglas = List.copyOf(reglas);
        this.alertaRepository = Objects.requireNonNull(alertaRepository);
        this.publisher = Objects.requireNonNull(publisher);
        this.clock = Objects.requireNonNull(clock);
    }

    @Transactional
    public void analizar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "El evento no puede ser nulo");
        if (!evento.estaCompletada()) {
            return;
        }
        reglas.stream()
                .map(regla -> regla.evaluar(evento))
                .flatMap(Optional::stream)
                // Kafka puede reentregar el evento: si la alerta ya existe no se duplica
                .filter(motivo -> !alertaRepository.existe(evento.txId(), motivo))
                .map(motivo -> AlertaFraude.detectar(evento.txId(), motivo, clock.instant()))
                .forEach(alerta -> {
                    alertaRepository.registrar(alerta);
                    publisher.publicar(alerta.toRecord());
                });
    }
}
