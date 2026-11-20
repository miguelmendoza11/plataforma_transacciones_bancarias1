package com.banco.ledger.fraud.infrastructure.persistence;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.fraud.application.port.AlertaFraudeRepository;
import com.banco.ledger.fraud.domain.model.AlertaFraude;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador JPA del puerto AlertaFraudeRepository. */
@Component
public class JpaAlertaFraudeRepositoryAdapter implements AlertaFraudeRepository {

    private final SpringDataAlertaFraudeRepository repository;

    public JpaAlertaFraudeRepositoryAdapter(SpringDataAlertaFraudeRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public void registrar(AlertaFraude alerta) {
        FraudAlertRecord datos = alerta.toRecord();
        repository.save(new AlertaFraudeJpaEntity(datos.txId().valor(), datos.reason(), datos.detectedAt()));
    }

    @Override
    public boolean existe(TxId txId, String motivo) {
        return repository.existsByTxIdAndReason(txId.valor(), motivo);
    }
}
