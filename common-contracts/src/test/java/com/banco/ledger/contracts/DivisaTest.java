package com.banco.ledger.contracts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DivisaTest {

    @Test
    void porDefectoEsCop() {
        // Arrange / Act
        Divisa divisa = Divisa.porDefecto();

        // Assert
        assertThat(divisa).isEqualTo(Divisa.COP);
    }

    @ParameterizedTest
    @ValueSource(strings = {"COP", "cop", " Cop "})
    void aceptaCopSinImportarMayusculasNiEspacios(String codigo) {
        // Act
        Divisa divisa = Divisa.de(codigo);

        // Assert
        assertThat(divisa).isEqualTo(Divisa.COP);
    }

    @ParameterizedTest
    @ValueSource(strings = {"USD", "EUR", "XYZ"})
    void rechazaDivisasNoAdmitidas(String codigo) {
        // Act
        Throwable error = catchThrowable(() -> Divisa.de(codigo));

        // Assert
        assertThat(error)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Divisa no admitida");
    }

    @Test
    void rechazaCodigoVacio() {
        // Act
        Throwable error = catchThrowable(() -> Divisa.de("  "));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaCodigoNulo() {
        // Act
        Throwable error = catchThrowable(() -> Divisa.de(null));

        // Assert
        assertThat(error).isInstanceOf(NullPointerException.class);
    }
}
