package com.banco.ledger.dashboard.application;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.dashboard.application.port.DashboardNotifier;
import com.banco.ledger.dashboard.domain.VistaMaterializada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ProyectarLedgerServiceTest {

    private static final Instant AHORA = Instant.parse("2026-01-01T10:00:00Z");
    private static final TxId TX_ID = TxId.de("tx-1");

    @Mock
    private DashboardNotifier notifier;

    private ProyectarLedgerService service;

    @BeforeEach
    void setUp() {
        // la vista es dominio puro, se usa la real
        service = new ProyectarLedgerService(new VistaMaterializada(), notifier);
    }

    private static TransactionEventRecord evento() {
        return new TransactionEventRecord(TX_ID, "A", "B", MontoDinero.de("20000"),
                EstadoTransaccion.COMPLETED, null, AHORA);
    }

    private static FraudAlertRecord alerta() {
        return new FraudAlertRecord(TX_ID, "HIGH_VALUE", AHORA);
    }

    @Test
    void transaccionSeDifundeUnaVez() {
        // Act
        service.alRecibirTransaccion(evento());

        // Assert
        verify(notifier, times(1)).difundir(argThat(v ->
                v.txId().equals(TX_ID) && !v.sospechosa()));
    }

    @Test
    void alertaDeTransaccionExistenteDifundeLaVistaMarcada() {
        // Arrange
        service.alRecibirTransaccion(evento());
        clearInvocations(notifier);

        // Act
        service.alRecibirAlerta(alerta());

        // Assert
        verify(notifier, times(1)).difundir(argThat(v ->
                v.txId().equals(TX_ID) && v.sospechosa() && v.motivoFraude().equals("HIGH_VALUE")));
    }

    @Test
    void alertaPendienteNoDifundeNada() {
        // Act
        service.alRecibirAlerta(alerta());

        // Assert
        verifyNoInteractions(notifier);
    }

    @Test
    void transaccionQueLlegaDespuesDeSuAlertaSeDifundeMarcada() {
        // Arrange
        service.alRecibirAlerta(alerta());

        // Act
        service.alRecibirTransaccion(evento());

        // Assert
        verify(notifier, times(1)).difundir(argThat(v -> v.sospechosa()));
    }
}
