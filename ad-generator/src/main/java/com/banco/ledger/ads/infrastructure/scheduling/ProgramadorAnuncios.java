package com.banco.ledger.ads.infrastructure.scheduling;

import com.banco.ledger.ads.application.GenerarAnuncioService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Adaptador de entrada por tiempo: cada ads.intervalo-ms dispara el caso de uso. */
@Component
public class ProgramadorAnuncios {

    private final GenerarAnuncioService servicio;

    public ProgramadorAnuncios(GenerarAnuncioService servicio) {
        this.servicio = Objects.requireNonNull(servicio);
    }

    @Scheduled(fixedRateString = "${ads.intervalo-ms}")
    public void publicarAnuncio() {
        servicio.generar();
    }
}
