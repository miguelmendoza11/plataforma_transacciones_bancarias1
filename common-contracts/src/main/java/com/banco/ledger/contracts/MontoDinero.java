package com.banco.ledger.contracts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object inmutable que representa una cantidad de dinero positiva.
 * Fail-Fast: rechaza nulos, ceros y negativos en el constructor compacto.
 * (Effective Java, Item 17: minimizar la mutabilidad).
 */
public record MontoDinero(BigDecimal valor) implements Comparable<MontoDinero> {

    private static final int ESCALA = 2;

    public MontoDinero {
        Objects.requireNonNull(valor, "El monto no puede ser nulo");
        if (valor.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero: " + valor);
        }
        valor = valor.setScale(ESCALA, RoundingMode.HALF_EVEN);
    }

    /** Factoria estatica (Effective Java, Item 1). */
    @JsonCreator
    public static MontoDinero de(BigDecimal valor) {
        return new MontoDinero(valor);
    }

    public static MontoDinero de(String valor) {
        Objects.requireNonNull(valor, "El monto no puede ser nulo");
        return new MontoDinero(new BigDecimal(valor));
    }

    @JsonValue
    public BigDecimal valor() {
        return valor;
    }

    public MontoDinero sumar(MontoDinero otro) {
        return new MontoDinero(valor.add(otro.valor));
    }

    public boolean esMayorQue(MontoDinero otro) {
        return compareTo(otro) > 0;
    }

    @Override
    public int compareTo(MontoDinero otro) {
        return valor.compareTo(otro.valor);
    }

    @Override
    public String toString() {
        return "$" + valor.toPlainString();
    }
}
