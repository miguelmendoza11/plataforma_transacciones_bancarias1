package com.banco.ledger.notification.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class DirectorioEnMemoriaTest {

    private final DirectorioUsuarios directorio = new DirectorioEnMemoria();

    @Test
    void usuarioAResuelveAUserAEnMinusculas() {
        // Arrange
        var usuario = "A";

        // Act
        String email = directorio.emailDe(usuario);

        // Assert
        assertThat(email).isEqualTo("user_a@email.com");
    }

    @Test
    void usuarioNuloFallaRapido() {
        // Arrange
        String usuario = null;

        // Act
        Throwable error = catchThrowable(() -> directorio.emailDe(usuario));

        // Assert
        assertThat(error).isInstanceOf(NullPointerException.class);
    }

    @Test
    void usuarioVacioFallaRapido() {
        // Arrange
        var usuario = "   ";

        // Act
        Throwable error = catchThrowable(() -> directorio.emailDe(usuario));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
    }
}
