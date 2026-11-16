package com.banco.ledger.fraud.application;

import com.banco.ledger.contracts.TransactionEventRecord;

/**
 * TODO (Miguel):
 *  - @Service, constructor con List<ReglaFraude>, AlertaFraudeRepository, FraudAlertPublisher.
 *  - Para cada regla disparada: persistir alerta (PostgreSQL) y publicar FraudAlertRecord.
 *  - Solo evaluar eventos COMPLETED.
 */
public class DetectarFraudeService {

    public void analizar(TransactionEventRecord evento) {
        throw new UnsupportedOperationException("TODO Miguel");
    }
}
