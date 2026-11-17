package com.banco.ledger.contracts;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferCommandRecordTest {

    private static final MontoDinero CINCUENTA = MontoDinero.de("50");

    @Test
    void creaComandoValido() {
        var cmd = new TransferCommandRecord(TxId.nuevo(), " A ", "B", CINCUENTA, Instant.now());
        assertThat(cmd.fromUser()).isEqualTo("A");
    }

    @Test
    void rechazaUsuarioVacio() {
        assertThatThrownBy(() -> new TransferCommandRecord(TxId.nuevo(), "  ", "B", CINCUENTA, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from_user");
    }

    @Test
    void rechazaTransferenciaALaMismaCuenta() {
        assertThatThrownBy(() -> new TransferCommandRecord(TxId.nuevo(), "A", "a", CINCUENTA, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaTxIdVacio() {
        assertThatThrownBy(() -> TxId.de(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
