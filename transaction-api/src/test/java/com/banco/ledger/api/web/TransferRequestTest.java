package com.banco.ledger.api.web;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferRequestTest {

    private static final TxId TX_ID = TxId.de("tx-1");
    private static final Instant AHORA = Instant.parse("2026-01-01T10:00:00Z");

    @Test
    void convierteRequestValidoEnComando() {
        // Arrange
        var request = new TransferRequest(" A ", "B", new BigDecimal("50"));

        // Act
        TransferCommandRecord comando = request.toComando(TX_ID, AHORA);

        // Assert
        assertThat(comando.txId()).isEqualTo(TX_ID);
        assertThat(comando.fromUser()).isEqualTo("A");
        assertThat(comando.toUser()).isEqualTo("B");
        assertThat(comando.amount()).isEqualTo(MontoDinero.de("50"));
        assertThat(comando.requestedAt()).isEqualTo(AHORA);
    }

    @Test
    void rechazaOrigenVacio() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new TransferRequest("  ", "B", BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from_user");
    }

    @Test
    void rechazaDestinoVacio() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new TransferRequest("A", "", BigDecimal.TEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("to_user");
    }

    @Test
    void rechazaOrigenNulo() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new TransferRequest(null, "B", BigDecimal.TEN))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("from_user");
    }

    @Test
    void rechazaDestinoNulo() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new TransferRequest("A", null, BigDecimal.TEN))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("to_user");
    }

    @Test
    void rechazaMontoNulo() {
        // Arrange + Act + Assert
        assertThatThrownBy(() -> new TransferRequest("A", "B", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("amount");
    }

    @Test
    void rechazaMontoCero() {
        // Arrange + Act + Assert: lo rechaza el propio constructor compacto
        assertThatThrownBy(() -> new TransferRequest("A", "B", BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaMontoNegativo() {
        // Arrange + Act + Assert: lo rechaza el propio constructor compacto
        assertThatThrownBy(() -> new TransferRequest("A", "B", new BigDecimal("-5")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaTransferenciaALaMismaCuenta() {
        // Arrange
        var request = new TransferRequest("A", "a", BigDecimal.TEN);

        // Act + Assert
        assertThatThrownBy(() -> request.toComando(TX_ID, AHORA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("misma cuenta");
    }
}
