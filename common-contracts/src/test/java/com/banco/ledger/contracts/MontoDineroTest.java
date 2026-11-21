package com.banco.ledger.contracts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Ejemplo de prueba de dominio puro bajo el patron AAA (sin Spring, sin red). */
class MontoDineroTest {

    @Test
    void creaMontoValidoNormalizandoEscala() {
        // Arrange
        var bruto = new BigDecimal("50");
        // Act
        var monto = MontoDinero.de(bruto);
        // Assert
        assertThat(monto.valor()).isEqualByComparingTo("50.00");
        assertThat(monto.valor().scale()).isEqualTo(2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-0.01"})
    void rechazaMontosNoPositivos(String valor) {
        // Act + Assert
        assertThatThrownBy(() -> MontoDinero.de(valor))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaNulo() {
        // Act + Assert
        assertThatThrownBy(() -> new MontoDinero(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void comparaMontos() {
        // Arrange
        var veinteMil = MontoDinero.de("20000");
        var diezMil = MontoDinero.de("10000");
        // Act
        boolean mayor = veinteMil.esMayorQue(diezMil);
        // Assert
        assertThat(mayor).isTrue();
    }
}
