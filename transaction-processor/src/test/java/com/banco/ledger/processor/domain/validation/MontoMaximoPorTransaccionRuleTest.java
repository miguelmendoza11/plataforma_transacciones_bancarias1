package com.banco.ledger.processor.domain.validation;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Saldo;
import com.banco.ledger.processor.domain.model.Transaccion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/** Pruebas de dominio puro de la estrategia MontoMaximoPorTransaccionRule (maximo 100000). */
class MontoMaximoPorTransaccionRuleTest {

    private final ReglaValidacion regla = new MontoMaximoPorTransaccionRule(MontoDinero.de("100000"));
    private final CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("1000000.00"));

    private static Transaccion transaccionDe(String monto) {
        return Transaccion.recibir(new TransferCommandRecord(
                TxId.de("tx-1"), "A", "B", MontoDinero.de(monto), Instant.parse("2026-11-18T11:31:00Z")));
    }

    @Test
    @DisplayName("Una transferencia de $20.000 se aprueba (el fraude la marca despues, no el processor)")
    void apruebaVeinteMil() {
        // Arrange
        Transaccion tx = transaccionDe("20000");

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado).isEqualTo(ResultadoValidacion.aprobada());
    }

    @Test
    @DisplayName("Un monto exactamente igual al maximo se aprueba")
    void apruebaMontoIgualAlMaximo() {
        // Arrange
        Transaccion tx = transaccionDe("100000.00");

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado.esAprobada()).isTrue();
    }

    @Test
    @DisplayName("Un monto mayor que el maximo se rechaza con AMOUNT_LIMIT_EXCEEDED")
    void rechazaMontoMayorAlMaximo() {
        // Arrange
        Transaccion tx = transaccionDe("100000.01");

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado.esAprobada()).isFalse();
        assertThat(resultado.motivo()).isEqualTo("AMOUNT_LIMIT_EXCEEDED");
    }

    @Test
    @DisplayName("Rechaza un maximo nulo (Fail-Fast)")
    void rechazaMaximoNulo() {
        // Arrange
        MontoDinero maximo = null;

        // Act
        Throwable error = catchThrowable(() -> new MontoMaximoPorTransaccionRule(maximo));

        // Assert
        assertThat(error).isInstanceOf(NullPointerException.class);
    }
}
