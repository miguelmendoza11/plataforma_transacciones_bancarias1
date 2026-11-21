package com.banco.ledger.notification.infrastructure.messaging;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.notification.application.EnrutarNotificacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionsLogListenerTest {

    @Mock
    private EnrutarNotificacionService service;

    @InjectMocks
    private TransactionsLogListener listener;

    @Test
    void delegaElEventoAlServicioUnaVez() {
        // Arrange
        var evento = new TransactionEventRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de("50"),
                EstadoTransaccion.COMPLETED, null, Instant.now());

        // Act
        listener.onMessage(evento);

        // Assert
        verify(service, times(1)).enrutar(evento);
    }
}
