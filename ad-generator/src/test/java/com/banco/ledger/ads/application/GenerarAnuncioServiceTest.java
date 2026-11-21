package com.banco.ledger.ads.application;

import com.banco.ledger.ads.application.port.AnuncioPublisher;
import com.banco.ledger.ads.domain.CatalogoAnuncios;
import com.banco.ledger.contracts.EmailCommandRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerarAnuncioServiceTest {

    @Mock
    private AnuncioPublisher publisher;

    @Test
    void publicaElSiguienteAnuncioParaTodosLosUsuarios() {
        // Arrange (construccion manual: el catalogo recibe una List)
        var catalogo = new CatalogoAnuncios(List.of("¡Obtenga nuestra nueva Tarjeta de Crédito!"));
        var service = new GenerarAnuncioService(catalogo, publisher);

        // Act
        service.generar();

        // Assert
        verify(publisher, times(1)).publicar(
                new EmailCommandRecord("all_users@banco.com", "¡Obtenga nuestra nueva Tarjeta de Crédito!"));
    }
}
