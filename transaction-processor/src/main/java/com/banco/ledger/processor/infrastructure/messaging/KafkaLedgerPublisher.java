package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.processor.application.port.LedgerEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;

/** Adaptador del puerto LedgerEventPublisher: publica los eventos en transactions_log con tx_id como key. */
@Component
public class KafkaLedgerPublisher implements LedgerEventPublisher {

    private final KafkaTemplate<String, TransactionEventRecord> kafkaTemplate;

    public KafkaLedgerPublisher(KafkaTemplate<String, TransactionEventRecord> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public void publicar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "El evento no puede ser nulo");
        // Se publica despues del commit para no mandar a Kafka algo que la BD revirtio (dual write).
        // Mejora posible: patron Outbox.
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(evento);
                }
            });
        } else {
            enviar(evento);
        }
    }

    private void enviar(TransactionEventRecord evento) {
        kafkaTemplate.send(Topologia.TOPIC_TRANSACTIONS_LOG, evento.txId().valor(), evento);
    }
}
