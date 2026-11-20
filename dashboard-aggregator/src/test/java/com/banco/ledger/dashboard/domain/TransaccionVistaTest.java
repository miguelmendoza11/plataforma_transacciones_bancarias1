package com.banco.ledger.dashboard.domain;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransaccionVistaTest {

    private static final TxId TX_ID = TxId.de("tx-1");
    private static final MontoDinero CINCUENTA = MontoDinero.de("50");

    private static TransactionEventRecord evento(EstadoTransaccion estado, String motivo) {
        return new TransactionEventRecord(TX_ID, "A", "B", CINCUENTA, estado, motivo,
                Instant.parse("2026-01-01T10:00:00Z"));
    }

    @Test
    void creaVistaNoSospechosaDesdeEventoCompletado() {
        // Act
        var vista = TransaccionVista.desde(evento(EstadoTransaccion.COMPLETED, null));

        // Assert
        assertThat(vista.txId()).isEqualTo(TX_ID);
        assertThat(vista.fromUser()).isEqualTo("A");
        assertThat(vista.toUser()).isEqualTo("B");
        assertThat(vista.amount()).isEqualTo(CINCUENTA);
        assertThat(vista.status()).isEqualTo(EstadoTransaccion.COMPLETED);
        assertThat(vista.sospechosa()).isFalse();
        assertThat(vista.motivoFraude()).isNull();
    }

    @Test
    void creaVistaDesdeEventoRechazado() {
        // Act
        var vista = TransaccionVista.desde(evento(EstadoTransaccion.REJECTED, "INSUFFICIENT_FUNDS"));

        // Assert
        assertThat(vista.status()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(vista.sospechosa()).isFalse();
    }

    @Test
    void marcarSospechosaDevuelveVistaNuevaSinCambiarLaOriginal() {
        // Arrange
        var original = TransaccionVista.desde(evento(EstadoTransaccion.COMPLETED, null));

        // Act
        var marcada = original.marcarSospechosa("HIGH_VALUE");

        // Assert
        assertThat(marcada.sospechosa()).isTrue();
        assertThat(marcada.motivoFraude()).isEqualTo("HIGH_VALUE");
        assertThat(marcada.txId()).isEqualTo(original.txId());
        assertThat(marcada.status()).isEqualTo(EstadoTransaccion.COMPLETED);
        assertThat(original.sospechosa()).isFalse();
        assertThat(original.motivoFraude()).isNull();
    }

    @Test
    void rechazaMarcarSinMotivo() {
        // Arrange
        var vista = TransaccionVista.desde(evento(EstadoTransaccion.COMPLETED, null));

        // Act + Assert
        assertThatThrownBy(() -> vista.marcarSospechosa(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaMotivoEnVistaNoSospechosa() {
        // Act + Assert
        assertThatThrownBy(() -> new TransaccionVista(TX_ID, "A", "B", CINCUENTA,
                EstadoTransaccion.COMPLETED, false, "HIGH_VALUE"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaEventoNulo() {
        // Act + Assert
        assertThatThrownBy(() -> TransaccionVista.desde(null))
                .isInstanceOf(NullPointerException.class);
    }
}
