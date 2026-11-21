package com.banco.ledger.fraud.application;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.fraud.application.port.AlertaFraudeRepository;
import com.banco.ledger.fraud.application.port.FraudAlertPublisher;
import com.banco.ledger.fraud.domain.model.AlertaFraude;
import com.banco.ledger.fraud.domain.rules.AltoValorRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetectarFraudeServiceTest {

    private static final Instant AHORA = Instant.parse("2026-11-18T11:31:04Z");
    private static final TxId TX_ID = TxId.de("tx-1");

    @Mock
    private AlertaFraudeRepository alertaRepository;

    @Mock
    private FraudAlertPublisher publisher;

    private DetectarFraudeService service;

    @BeforeEach
    void setUp() {
        // se construye a mano porque recibe una lista de reglas y un Clock
        service = new DetectarFraudeService(
                List.of(new AltoValorRule(MontoDinero.de("10000"))),
                alertaRepository,
                publisher,
                Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    private static TransactionEventRecord evento(String monto, EstadoTransaccion estado) {
        return new TransactionEventRecord(TX_ID, "A", "B", MontoDinero.de(monto), estado, null,
                Instant.parse("2026-11-18T11:31:02Z"));
    }

    @Test
    void veinteMilRegistraYPublicaUnaAlertaHighValue() {
        // Arrange
        when(alertaRepository.existe(TX_ID, "HIGH_VALUE")).thenReturn(false);
        ArgumentCaptor<FraudAlertRecord> alerta = ArgumentCaptor.forClass(FraudAlertRecord.class);

        // Act
        service.analizar(evento("20000", EstadoTransaccion.COMPLETED));

        // Assert
        verify(alertaRepository, times(1)).registrar(any(AlertaFraude.class));
        verify(publisher, times(1)).publicar(alerta.capture());
        assertThat(alerta.getValue()).isEqualTo(new FraudAlertRecord(TX_ID, "HIGH_VALUE", AHORA));
    }

    @Test
    void cincuentaPesosNoGeneraAlerta() {
        // Arrange
        var evento = evento("50", EstadoTransaccion.COMPLETED);

        // Act
        service.analizar(evento);

        // Assert
        verifyNoInteractions(alertaRepository);
        verifyNoInteractions(publisher);
    }

    @Test
    void transaccionRechazadaSeIgnora() {
        // Arrange
        var evento = evento("20000", EstadoTransaccion.REJECTED);

        // Act
        service.analizar(evento);

        // Assert
        verifyNoInteractions(alertaRepository);
        verifyNoInteractions(publisher);
    }

    @Test
    void alertaRepetidaNoSeDuplica() {
        // Arrange
        when(alertaRepository.existe(TX_ID, "HIGH_VALUE")).thenReturn(true);

        // Act
        service.analizar(evento("20000", EstadoTransaccion.COMPLETED));

        // Assert
        verify(alertaRepository, never()).registrar(any(AlertaFraude.class));
        verifyNoInteractions(publisher);
    }
}
