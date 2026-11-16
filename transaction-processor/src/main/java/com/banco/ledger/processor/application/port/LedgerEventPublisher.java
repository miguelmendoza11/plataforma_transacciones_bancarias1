package com.banco.ledger.processor.application.port;

import com.banco.ledger.contracts.TransactionEventRecord;

/** Puerto de mensajeria: publicar el resultado en el ledger. La logica NO conoce Kafka. */
public interface LedgerEventPublisher {
    void publicar(TransactionEventRecord evento);
}
