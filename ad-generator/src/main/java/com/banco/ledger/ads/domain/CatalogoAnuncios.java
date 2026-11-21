package com.banco.ledger.ads.domain;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Catalogo inmutable de anuncios con rotacion round-robin.
 * El AtomicInteger hace que siguiente() sea seguro si lo llaman varios hilos.
 */
public final class CatalogoAnuncios {

    private final List<String> mensajes;
    private final AtomicInteger posicion = new AtomicInteger();

    public CatalogoAnuncios(List<String> mensajes) {
        Objects.requireNonNull(mensajes, "mensajes no puede ser nulo");
        if (mensajes.isEmpty()) {
            throw new IllegalArgumentException("El catalogo debe tener al menos un anuncio");
        }
        this.mensajes = List.copyOf(mensajes);
    }

    public String siguiente() {
        // floorMod evita indices negativos si el contador llega a desbordarse
        int indice = Math.floorMod(posicion.getAndIncrement(), mensajes.size());
        return mensajes.get(indice);
    }
}
