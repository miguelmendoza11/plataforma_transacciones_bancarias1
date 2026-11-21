package com.banco.ledger.ads.infrastructure.scheduling;

import com.banco.ledger.ads.application.GenerarAnuncioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProgramadorAnunciosTest {

    @Mock
    private GenerarAnuncioService servicio;

    @InjectMocks
    private ProgramadorAnuncios programador;

    @Test
    void cadaTickGeneraUnAnuncio() {
        // Arrange (el servicio es un mock, no hace falta esperar al reloj)

        // Act
        programador.publicarAnuncio();

        // Assert
        verify(servicio, times(1)).generar();
    }
}
