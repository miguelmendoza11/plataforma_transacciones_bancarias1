package com.banco.ledger.fraud.domain.rules;

import com.banco.ledger.contracts.TransactionEventRecord;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Strategy con estado: dispara HIGH_FREQUENCY si un mismo from_user hace mas de
 * maxTransacciones dentro de la ventana de tiempo (ventana deslizante en memoria).
 */
public final class FrecuenciaAnomalaRule implements ReglaFraude {

    private final int maxTransacciones;
    private final Duration ventana;
    private final Map<String, Deque<Instant>> historial = new ConcurrentHashMap<>();

    public FrecuenciaAnomalaRule(int maxTransacciones, Duration ventana) {
        if (maxTransacciones <= 0) {
            throw new IllegalArgumentException("maxTransacciones debe ser mayor que cero: " + maxTransacciones);
        }
        Objects.requireNonNull(ventana, "La ventana no puede ser nula");
        if (ventana.isZero() || ventana.isNegative()) {
            throw new IllegalArgumentException("La ventana debe ser positiva: " + ventana);
        }
        this.maxTransacciones = maxTransacciones;
        this.ventana = ventana;
    }

    @Override
    public Optional<String> evaluar(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "El evento no puede ser nulo");
        // se usa la hora del evento y no la del reloj, asi reprocesar da el mismo resultado
        Instant momento = evento.occurredAt();
        Instant limite = momento.minus(ventana);
        int[] enVentana = new int[1];

        // compute() bloquea la entrada del usuario: dos hilos no pueden perder conteos
        historial.compute(evento.fromUser(), (usuario, instantes) -> {
            Deque<Instant> deque = instantes == null ? new ArrayDeque<>() : instantes;
            deque.addLast(momento);
            deque.removeIf(instante -> !instante.isAfter(limite));
            enVentana[0] = deque.size();
            return deque;
        });

        return enVentana[0] > maxTransacciones
                ? Optional.of(MotivosFraude.HIGH_FREQUENCY)
                : Optional.empty();
    }
}
