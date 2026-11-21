package com.banco.ledger.contracts;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferCommandRecordTest {

    private static final MontoDinero CINCUENTA = MontoDinero.de("50");

    @Test
    void creaComandoValido() {
        // Arrange
        var txId = TxId.nuevo();
        // Act
        var cmd = new TransferCommandRecord(txId, " A ", "B", CINCUENTA, Instant.now());
        // Assert
        assertThat(cmd.fromUser()).isEqualTo("A");
    }

    @Test
    void rechazaUsuarioVacio() {
        // Act + Assert
        assertThatThrownBy(() -> new TransferCommandRecord(TxId.nuevo(), "  ", "B", CINCUENTA, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from_user");
    }

    @Test
    void rechazaTransferenciaALaMismaCuenta() {
        // Act + Assert
        assertThatThrownBy(() -> new TransferCommandRecord(TxId.nuevo(), "A", "a", CINCUENTA, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaTxIdVacio() {
        // Act + Assert
        assertThatThrownBy(() -> TxId.de(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
