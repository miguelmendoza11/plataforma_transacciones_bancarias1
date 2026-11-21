package com.banco.ledger.contracts;

import java.util.Arrays;
import java.util.Locale;

/** Divisas admitidas por el banco. Por ahora solo pesos colombianos (COP). */
public enum Divisa {
    COP;

    public static Divisa porDefecto() {
        return COP;
    }

    /** Fail-Fast: rechaza codigos vacios o divisas que el banco no admite. */
    public static Divisa de(String codigo) {
        String valor = Validaciones.noVacio(codigo, "currency").toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(divisa -> divisa.name().equals(valor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Divisa no admitida: " + codigo));
    }
}
