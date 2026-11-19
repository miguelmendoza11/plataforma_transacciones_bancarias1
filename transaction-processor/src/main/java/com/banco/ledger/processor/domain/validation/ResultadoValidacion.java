package com.banco.ledger.processor.domain.validation;

import com.banco.ledger.contracts.Validaciones;

/**
 * VALUE OBJECT inmutable: resultado de evaluar una {@link ReglaValidacion}.
 * Si la regla rechaza, el motivo es obligatorio (ej. "INSUFFICIENT_FUNDS"); viaja como
 * "reason" en el evento de Kafka.
 */
public record ResultadoValidacion(boolean esAprobada, String motivo) {

    public ResultadoValidacion {
        if (!esAprobada) {
            motivo = Validaciones.noVacio(motivo, "motivo");
        }
    }

    public static ResultadoValidacion aprobada() {
        return new ResultadoValidacion(true, null);
    }

    public static ResultadoValidacion rechazada(String motivo) {
        return new ResultadoValidacion(false, motivo);
    }
}
