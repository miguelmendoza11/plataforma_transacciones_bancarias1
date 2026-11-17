package com.banco.ledger.contracts;

import java.util.Objects;

/** Utilidades Fail-Fast para constructores compactos. Clase no instanciable (Effective Java, Item 4). */
public final class Validaciones {

    private Validaciones() {
        throw new AssertionError("No instanciable");
    }

    public static String noVacio(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser nulo");
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " no puede estar vacio");
        }
        return valor.strip();
    }
}
