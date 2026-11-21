package com.banco.ledger.dashboard.infrastructure.messaging;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.dashboard.application.ProyectarLedgerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class LedgerListenersTest {

    private static final Instant AHORA = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private ProyectarLedgerService service;

    @InjectMocks
    private LedgerListeners listeners;

    @Test
    void transaccionSeDelegaAlServicioUnaVez() {
        // Arrange
        var evento = new TransactionEventRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de("50"),
                EstadoTransaccion.COMPLETED, null, AHORA);

        // Act
        listeners.onTransaccion(evento);

        // Assert
        verify(service, times(1)).alRecibirTransaccion(evento);
        verifyNoMoreInteractions(service);
    }

    @Test
    void alertaSeDelegaAlServicioUnaVez() {
        // Arrange
        var alerta = new FraudAlertRecord(TxId.de("tx-1"), "HIGH_VALUE", AHORA);

        // Act
        listeners.onAlerta(alerta);

        // Assert
        verify(service, times(1)).alRecibirAlerta(alerta);
        verifyNoMoreInteractions(service);
    }
}
