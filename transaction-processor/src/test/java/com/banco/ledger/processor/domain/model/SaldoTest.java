package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.MontoDinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas de dominio puro del Value Object Saldo (sin Spring, en memoria). */
class SaldoTest {

    @Test
    @DisplayName("Saldo.cero() vale 0.00 y es un saldo valido")
    void ceroEsValido() {
        // Arrange / Act
        Saldo cero = Saldo.cero();

        // Assert
        assertThat(cero.valor()).isEqualByComparingTo("0");
        assertThat(cero).isEqualTo(Saldo.de("0.00"));
    }

    @Test
    @DisplayName("Normaliza a escala 2 con redondeo bancario (HALF_EVEN)")
    void normalizaEscalaConHalfEven() {
        // Arrange / Act
        Saldo redondeaAbajo = Saldo.de("10.125");
        Saldo redondeaArriba = Saldo.de("10.135");

        // Assert
        assertThat(redondeaAbajo.valor()).isEqualTo(new BigDecimal("10.12"));
        assertThat(redondeaArriba.valor()).isEqualTo(new BigDecimal("10.14"));
    }

    @Test
    @DisplayName("Saldos con el mismo valor son iguales aunque se escriban distinto")
    void igualdadPorValor() {
        // Act / Assert
        assertThat(Saldo.de("100")).isEqualTo(Saldo.de(new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Rechaza saldo negativo (Fail-Fast)")
    void rechazaNegativo() {
        // Act / Assert
        assertThatThrownBy(() -> Saldo.de("-0.01"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    @DisplayName("Rechaza saldo nulo (Fail-Fast)")
    void rechazaNulo() {
        // Act / Assert
        assertThatThrownBy(() -> Saldo.de((BigDecimal) null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> Saldo.de((String) null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("mas() devuelve un saldo nuevo sin mutar el original")
    void masDevuelveNuevoSaldo() {
        // Arrange
        Saldo saldo = Saldo.de("100.00");

        // Act
        Saldo resultado = saldo.mas(MontoDinero.de("50.25"));

        // Assert
        assertThat(resultado).isEqualTo(Saldo.de("150.25"));
        assertThat(saldo).isEqualTo(Saldo.de("100.00"));
    }

    @Test
    @DisplayName("menos() resta y devuelve un saldo nuevo")
    void menosResta() {
        // Arrange
        Saldo saldo = Saldo.de("100.00");

        // Act
        Saldo resultado = saldo.menos(MontoDinero.de("30.50"));

        // Assert
        assertThat(resultado).isEqualTo(Saldo.de("69.50"));
        assertThat(saldo).isEqualTo(Saldo.de("100.00"));
    }

    @Test
    @DisplayName("menos() por el saldo exacto deja la cuenta en cero")
    void menosExactoDejaCero() {
        // Arrange
        Saldo saldo = Saldo.de("50.00");

        // Act
        Saldo resultado = saldo.menos(MontoDinero.de("50"));

        // Assert
        assertThat(resultado).isEqualTo(Saldo.cero());
    }

    @Test
    @DisplayName("menos() lanza IllegalStateException si quedaria negativo")
    void menosNoPermiteNegativo() {
        // Arrange
        Saldo saldo = Saldo.de("50.00");

        // Act / Assert
        assertThatThrownBy(() -> saldo.menos(MontoDinero.de("50.01")))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("alcanzaPara(): limites exactos")
    void alcanzaParaLimites() {
        // Arrange
        Saldo saldo = Saldo.de("100.00");

        // Act / Assert
        assertThat(saldo.alcanzaPara(MontoDinero.de("99.99"))).isTrue();
        assertThat(saldo.alcanzaPara(MontoDinero.de("100.00"))).isTrue();
        assertThat(saldo.alcanzaPara(MontoDinero.de("100.01"))).isFalse();
    }

    @Test
    @DisplayName("Un saldo en cero no alcanza para ningun monto")
    void ceroNoAlcanza() {
        // Act / Assert
        assertThat(Saldo.cero().alcanzaPara(MontoDinero.de("0.01"))).isFalse();
    }

    @Test
    @DisplayName("Las operaciones rechazan monto nulo")
    void operacionesRechazanMontoNulo() {
        // Arrange
        Saldo saldo = Saldo.de("10");

        // Act / Assert
        assertThatThrownBy(() -> saldo.mas(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> saldo.menos(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> saldo.alcanzaPara(null)).isInstanceOf(NullPointerException.class);
    }
}
