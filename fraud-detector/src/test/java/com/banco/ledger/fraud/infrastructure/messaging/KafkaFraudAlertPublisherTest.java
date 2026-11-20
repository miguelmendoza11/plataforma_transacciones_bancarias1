package com.banco.ledger.fraud.infrastructure.messaging;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaFraudAlertPublisherTest {

    @Mock
    private KafkaTemplate<String, FraudAlertRecord> kafkaTemplate;

    @InjectMocks
    private KafkaFraudAlertPublisher publisher;

    @Test
    void publicaEnFraudAlertsConTxIdComoKey() {
        // Arrange
        var alerta = new FraudAlertRecord(TxId.de("tx-1"), "HIGH_VALUE", Instant.now());

        // Act
        publisher.publicar(alerta);

        // Assert
        verify(kafkaTemplate, times(1)).send("fraud_alerts", "tx-1", alerta);
    }
}
