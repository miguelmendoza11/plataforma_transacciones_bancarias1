package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class AltoValorRuleTest {

    private final AltoValorRule regla = new AltoValorRule(MontoDinero.de("10000"));

    private static TransactionEventRecord eventoDe(String monto) {
        return new TransactionEventRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de(monto),
                EstadoTransaccion.COMPLETED, null, Instant.parse("2026-11-18T11:31:02Z"));
    }

    @Test
    void cincuentaPesosNoDisparaAlerta() {
        // Arrange
        var evento = eventoDe("50");

        // Act
        Optional<String> motivo = regla.evaluar(evento);

        // Assert
        assertThat(motivo).isEmpty();
    }

    @Test
    void diezMilExactosNoDisparaAlerta() {
        // Arrange
        var evento = eventoDe("10000.00");

        // Act
        Optional<String> motivo = regla.evaluar(evento);

        // Assert
        assertThat(motivo).isEmpty();
    }

    @Test
    void diezMilConUnCentavoDisparaHighValue() {
        // Arrange
        var evento = eventoDe("10000.01");

        // Act
        Optional<String> motivo = regla.evaluar(evento);

        // Assert
        assertThat(motivo).contains(MotivosFraude.HIGH_VALUE);
    }

    @Test
    void veinteMilDisparaHighValue() {
        // Arrange
        var evento = eventoDe("20000");

        // Act
        Optional<String> motivo = regla.evaluar(evento);

        // Assert
        assertThat(motivo).contains("HIGH_VALUE");
    }

    @Test
    void umbralNuloFallaRapido() {
        // Arrange
        MontoDinero umbral = null;

        // Act
        Throwable error = catchThrowable(() -> new AltoValorRule(umbral));

        // Assert
        assertThat(error).isInstanceOf(NullPointerException.class);
    }
}
