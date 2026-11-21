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

/** Pruebas de dominio puro de la estrategia FondosSuficientesRule. */
class FondosSuficientesRuleTest {

    private final ReglaValidacion regla = new FondosSuficientesRule();

    private static Transaccion transaccionDe(String monto) {
        return Transaccion.recibir(new TransferCommandRecord(
                TxId.de("tx-1"), "A", "B", MontoDinero.de(monto), Instant.parse("2026-11-18T11:30:00Z")));
    }

    @Test
    @DisplayName("Aprueba si el saldo es mayor que el monto")
    void apruebaConSaldoSuficiente() {
        // Arrange
        Transaccion tx = transaccionDe("50");
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado).isEqualTo(ResultadoValidacion.aprobada());
    }

    @Test
    @DisplayName("Aprueba si el saldo es exactamente igual al monto")
    void apruebaConSaldoExacto() {
        // Arrange
        Transaccion tx = transaccionDe("100.00");
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado.esAprobada()).isTrue();
    }

    @Test
    @DisplayName("Rechaza con INSUFFICIENT_FUNDS si el saldo no alcanza")
    void rechazaSinFondos() {
        // Arrange
        Transaccion tx = transaccionDe("100.01");
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));

        // Act
        ResultadoValidacion resultado = regla.evaluar(tx, origen);

        // Assert
        assertThat(resultado.esAprobada()).isFalse();
        assertThat(resultado.motivo()).isEqualTo("INSUFFICIENT_FUNDS");
    }
}
