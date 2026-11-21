package com.banco.ledger.fraud.application.port;

import com.banco.ledger.contracts.FraudAlertRecord;

/** Puerto de mensajeria hacia el topic fraud_alerts. */
public interface FraudAlertPublisher {
    void publicar(FraudAlertRecord alerta);
}
