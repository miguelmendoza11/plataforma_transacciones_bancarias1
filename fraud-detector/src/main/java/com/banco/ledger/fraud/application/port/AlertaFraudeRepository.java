package com.banco.ledger.fraud.application.port;

import com.banco.ledger.contracts.TxId;
import com.banco.ledger.fraud.domain.model.AlertaFraude;

/** Puerto de persistencia de alertas para auditoria. Lo implementa el adaptador JPA. */
public interface AlertaFraudeRepository {

    void registrar(AlertaFraude alerta);

    boolean existe(TxId txId, String motivo);
}
