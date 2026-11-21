package com.banco.ledger.fraud.infrastructure.messaging;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.Topologia;
import com.banco.ledger.fraud.application.port.FraudAlertPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador del puerto FraudAlertPublisher: publica en fraud_alerts con tx_id como key. */
@Component
public class KafkaFraudAlertPublisher implements FraudAlertPublisher {

    private final KafkaTemplate<String, FraudAlertRecord> kafkaTemplate;

    public KafkaFraudAlertPublisher(KafkaTemplate<String, FraudAlertRecord> kafkaTemplate) {
        this.kafkaTemplate = Objects.requireNonNull(kafkaTemplate);
    }

    @Override
    public void publicar(FraudAlertRecord alerta) {
        Objects.requireNonNull(alerta, "La alerta no puede ser nula");
        kafkaTemplate.send(Topologia.TOPIC_FRAUD_ALERTS, alerta.txId().valor(), alerta);
    }
}
