package com.banco.ledger.processor.infrastructure.messaging;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
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
class KafkaLedgerPublisherTest {

    @Mock
    private KafkaTemplate<String, TransactionEventRecord> kafkaTemplate;

    @InjectMocks
    private KafkaLedgerPublisher publisher;

    @Test
    void publicaEnTransactionsLogConTxIdComoKey() {
        // Arrange
        var evento = new TransactionEventRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de("50"),
                EstadoTransaccion.COMPLETED, null, Instant.now());

        // Act
        publisher.publicar(evento);

        // Assert
        verify(kafkaTemplate, times(1)).send("transactions_log", "tx-1", evento);
    }
}
