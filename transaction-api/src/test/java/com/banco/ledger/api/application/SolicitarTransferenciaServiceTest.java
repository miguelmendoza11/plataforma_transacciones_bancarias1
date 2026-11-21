package com.banco.ledger.api.application;

import com.banco.ledger.api.application.port.TransferCommandPublisher;
import com.banco.ledger.api.web.TransferRequest;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SolicitarTransferenciaServiceTest {

    private static final Instant AHORA = Instant.parse("2026-01-01T10:00:00Z");

    @Mock
    private TransferCommandPublisher publisher;

    private SolicitarTransferenciaService service;

    @BeforeEach
    void setUp() {
        // construccion manual porque el Clock no es un mock
        service = new SolicitarTransferenciaService(publisher, Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    @Test
    void publicaElComandoUnaVezYDevuelveElTxId() {
        // Arrange
        var request = new TransferRequest("A", "B", new BigDecimal("50"));

        // Act
        TxId txId = service.solicitar(request);

        // Assert
        assertThat(txId).isNotNull();
        verify(publisher, times(1)).publicar(argThat(cmd ->
                cmd.txId().equals(txId)
                        && cmd.fromUser().equals("A")
                        && cmd.toUser().equals("B")
                        && cmd.amount().equals(MontoDinero.de("50"))
                        && cmd.requestedAt().equals(AHORA)));
    }

    @Test
    void noPublicaSiElMontoEsInvalido() {
        // Act + Assert: el request invalido ni siquiera se puede crear
        assertThatThrownBy(() -> service.solicitar(new TransferRequest("A", "B", BigDecimal.ZERO)))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(publisher);
    }

    @Test
    void noPublicaSiEsLaMismaCuenta() {
        // Arrange
        var request = new TransferRequest("A", "A", BigDecimal.TEN);

        // Act + Assert
        assertThatThrownBy(() -> service.solicitar(request))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(publisher);
    }

    @Test
    void rechazaRequestNulo() {
        // Act + Assert
        assertThatThrownBy(() -> service.solicitar(null))
                .isInstanceOf(NullPointerException.class);
        verifyNoInteractions(publisher);
    }
}
