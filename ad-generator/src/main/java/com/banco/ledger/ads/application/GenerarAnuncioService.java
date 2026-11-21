package com.banco.ledger.ads.application;

import com.banco.ledger.ads.application.port.AnuncioPublisher;
import com.banco.ledger.ads.domain.CatalogoAnuncios;
import com.banco.ledger.contracts.EmailCommandRecord;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Caso de uso: toma el siguiente anuncio del catalogo y lo publica para todos los usuarios.
 * DIP: depende del puerto AnuncioPublisher; no sabe nada del reloj ni de RabbitMQ.
 */
@Service
public class GenerarAnuncioService {

    private static final String DESTINATARIO = "all_users@banco.com";

    private final CatalogoAnuncios catalogo;
    private final AnuncioPublisher publisher;

    public GenerarAnuncioService(CatalogoAnuncios catalogo, AnuncioPublisher publisher) {
        this.catalogo = Objects.requireNonNull(catalogo);
        this.publisher = Objects.requireNonNull(publisher);
    }

    public void generar() {
        publisher.publicar(new EmailCommandRecord(DESTINATARIO, catalogo.siguiente()));
    }
}
