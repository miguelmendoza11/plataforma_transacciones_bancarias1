package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.MontoDinero;

import java.util.Objects;

/**
 * Se lanza cuando se intenta debitar de una cuenta un monto mayor que su saldo.
 *
 * Extiende IllegalStateException: el monto es valido (MontoDinero ya garantiza que es
 * positivo), pero la operacion no esta permitida con el saldo ACTUAL de la cuenta.
 * Protege la invariante "el saldo nunca es negativo" de {@link CuentaBancaria}.
 */
public class FondosInsuficientesException extends IllegalStateException {

    private final String usuario;
    private final Saldo saldoDisponible;
    private final MontoDinero montoSolicitado;

    public FondosInsuficientesException(String usuario, Saldo saldoDisponible, MontoDinero montoSolicitado) {
        super("Fondos insuficientes en la cuenta " + Objects.requireNonNull(usuario, "usuario no puede ser nulo")
                + ": saldo " + Objects.requireNonNull(saldoDisponible, "saldoDisponible no puede ser nulo")
                + ", se intento debitar " + Objects.requireNonNull(montoSolicitado, "montoSolicitado no puede ser nulo"));
        this.usuario = usuario;
        this.saldoDisponible = saldoDisponible;
        this.montoSolicitado = montoSolicitado;
    }

    public String usuario() {
        return usuario;
    }

    public Saldo saldoDisponible() {
        return saldoDisponible;
    }

    public MontoDinero montoSolicitado() {
        return montoSolicitado;
    }
}
