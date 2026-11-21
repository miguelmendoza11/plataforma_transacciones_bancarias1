package com.banco.ledger.ads.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CatalogoAnunciosTest {

    @Test
    void rotaEnOrdenYVuelveAlInicio() {
        // Arrange
        var catalogo = new CatalogoAnuncios(List.of("uno", "dos", "tres"));

        // Act
        var entregados = List.of(catalogo.siguiente(), catalogo.siguiente(),
                catalogo.siguiente(), catalogo.siguiente());

        // Assert
        assertThat(entregados).containsExactly("uno", "dos", "tres", "uno");
    }

    @Test
    void catalogoVacioFallaRapido() {
        // Arrange
        List<String> mensajes = List.of();

        // Act
        Throwable error = catchThrowable(() -> new CatalogoAnuncios(mensajes));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
    }
}
