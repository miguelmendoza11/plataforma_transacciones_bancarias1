package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.MontoDinero;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/** Pruebas de dominio puro de la entidad rica CuentaBancaria (sin Spring, en memoria). */
class CuentaBancariaTest {

    @Test
    @DisplayName("debitar() resta el monto del saldo")
    void debitarRestaDelSaldo() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("A", Saldo.de("100.00"));

        // Act
        cuenta.debitar(MontoDinero.de("30.00"));

        // Assert
        assertThat(cuenta.saldo()).isEqualTo(Saldo.de("70.00"));
    }

    @Test
    @DisplayName("debitar() todo el saldo deja la cuenta en cero")
    void debitarTodoElSaldoDejaCero() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("A", Saldo.de("50.00"));

        // Act
        cuenta.debitar(MontoDinero.de("50.00"));

        // Assert
        assertThat(cuenta.saldo()).isEqualTo(Saldo.cero());
    }

    @Test
    @DisplayName("debitar() sin fondos lanza FondosInsuficientesException y no modifica el saldo")
    void debitarSinFondosLanzaExcepcion() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("A", Saldo.de("30.00"));

        // Act
        Throwable error = catchThrowable(() -> cuenta.debitar(MontoDinero.de("50.00")));

        // Assert
        assertThat(error)
                .isInstanceOf(FondosInsuficientesException.class)
                .isInstanceOf(IllegalStateException.class);
        assertThat(cuenta.saldo()).isEqualTo(Saldo.de("30.00"));
    }

    @Test
    @DisplayName("acreditar() suma el monto al saldo")
    void acreditarSumaAlSaldo() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("B", Saldo.cero());

        // Act
        cuenta.acreditar(MontoDinero.de("50.00"));

        // Assert
        assertThat(cuenta.saldo()).isEqualTo(Saldo.de("50.00"));
    }

    @Test
    @DisplayName("Transferir entre dos cuentas conserva el dinero total")
    void debitarYAcreditarConservaElTotal() {
        // Arrange
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("20.00"));
        MontoDinero monto = MontoDinero.de("40.00");

        // Act
        origen.debitar(monto);
        destino.acreditar(monto);

        // Assert
        assertThat(origen.saldo()).isEqualTo(Saldo.de("60.00"));
        assertThat(destino.saldo()).isEqualTo(Saldo.de("60.00"));
    }

    @Test
    @DisplayName("tieneFondosPara() compara el saldo con el monto")
    void tieneFondosPara() {
        // Arrange
        CuentaBancaria cuenta = CuentaBancaria.abrir("A", Saldo.de("100.00"));

        // Act
        boolean alcanza = cuenta.tieneFondosPara(MontoDinero.de("100.00"));
        boolean noAlcanza = cuenta.tieneFondosPara(MontoDinero.de("100.01"));

        // Assert
        assertThat(alcanza).isTrue();
        assertThat(noAlcanza).isFalse();
    }

    @Test
    @DisplayName("Rechaza usuario vacio (Fail-Fast)")
    void rechazaUsuarioVacio() {
        // Arrange
        Saldo saldo = Saldo.cero();

        // Act
        Throwable error = catchThrowable(() -> CuentaBancaria.abrir("  ", saldo));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Rechaza saldo nulo (Fail-Fast)")
    void rechazaSaldoNulo() {
        // Arrange
        String usuario = "A";

        // Act
        Throwable error = catchThrowable(() -> CuentaBancaria.reconstituir(usuario, null));

        // Assert
        assertThat(error).isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Dos cuentas con el mismo usuario son la misma cuenta (identidad por usuario)")
    void igualdadPorUsuario() {
        // Arrange
        CuentaBancaria abierta = CuentaBancaria.abrir("A", Saldo.de("100.00"));
        CuentaBancaria reconstituida = CuentaBancaria.reconstituir("A", Saldo.de("5.00"));

        // Act
        boolean iguales = abierta.equals(reconstituida);

        // Assert
        assertThat(iguales).isTrue();
        assertThat(abierta.hashCode()).isEqualTo(reconstituida.hashCode());
    }
}
