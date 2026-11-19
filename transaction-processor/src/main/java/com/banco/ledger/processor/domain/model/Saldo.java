package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.MontoDinero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * VALUE OBJECT inmutable: saldo de una cuenta. Invariante: nunca es negativo.
 *
 * Existe porque {@link MontoDinero} representa el monto de una TRANSFERENCIA y por eso no
 * admite cero; un saldo, en cambio, si puede ser 0. Cada operacion devuelve un Saldo NUEVO
 * (Effective Java, Item 17: minimizar la mutabilidad), igual que BigDecimal o String.
 *
 * Misma escala (2) y redondeo (HALF_EVEN, "redondeo bancario") que MontoDinero, para que
 * las sumas y restas entre ambos sean exactas.
 */
public record Saldo(BigDecimal valor) {

    private static final int ESCALA = 2;
    private static final Saldo CERO = new Saldo(BigDecimal.ZERO);

    public Saldo {
        Objects.requireNonNull(valor, "El saldo no puede ser nulo");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo: " + valor);
        }
        valor = valor.setScale(ESCALA, RoundingMode.HALF_EVEN);
    }

    public static Saldo cero() {
        return CERO;
    }

    public static Saldo de(BigDecimal valor) {
        return new Saldo(valor);
    }

    public static Saldo de(String valor) {
        Objects.requireNonNull(valor, "El saldo no puede ser nulo");
        return new Saldo(new BigDecimal(valor));
    }

    /** Devuelve un saldo nuevo con el monto sumado. */
    public Saldo mas(MontoDinero monto) {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        return new Saldo(valor.add(monto.valor()));
    }

    /**
     * Devuelve un saldo nuevo con el monto restado.
     *
     * @throws IllegalStateException si el resultado quedaria negativo
     */
    public Saldo menos(MontoDinero monto) {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        if (!alcanzaPara(monto)) {
            throw new IllegalStateException(
                    "El saldo " + this + " no alcanza para restar " + monto);
        }
        return new Saldo(valor.subtract(monto.valor()));
    }

    /** true si el saldo es mayor o igual que el monto. */
    public boolean alcanzaPara(MontoDinero monto) {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        return valor.compareTo(monto.valor()) >= 0;
    }

    @Override
    public String toString() {
        return "$" + valor.toPlainString();
    }
}
