package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.Validaciones;

import java.util.Objects;

/**
 * ENTIDAD RICA - protege la invariante "el saldo nunca es negativo".
 *
 * Modelo rico vs. anemico: no hay setSaldo(). El saldo solo cambia mediante los metodos
 * semanticos debitar() y acreditar(), que validan la regla de negocio antes de modificarlo.
 * La identidad es el usuario (equals/hashCode por usuario), no el saldo.
 */
public final class CuentaBancaria {

    private final String usuario;
    private Saldo saldo;

    private CuentaBancaria(String usuario, Saldo saldo) {
        this.usuario = Validaciones.noVacio(usuario, "usuario");
        this.saldo = Objects.requireNonNull(saldo, "El saldo no puede ser nulo");
    }

    /** Abre una cuenta nueva con un saldo inicial. */
    public static CuentaBancaria abrir(String usuario, Saldo inicial) {
        return new CuentaBancaria(usuario, inicial);
    }

    /** Reconstruye una cuenta existente (la usa el adaptador de persistencia). */
    public static CuentaBancaria reconstituir(String usuario, Saldo saldo) {
        return new CuentaBancaria(usuario, saldo);
    }

    /**
     * Retira dinero de la cuenta.
     *
     * @throws FondosInsuficientesException si el saldo no alcanza
     */
    public void debitar(MontoDinero monto) {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        if (!tieneFondosPara(monto)) {
            throw new FondosInsuficientesException(usuario, saldo, monto);
        }
        saldo = saldo.menos(monto);
    }

    /** Deposita dinero en la cuenta. */
    public void acreditar(MontoDinero monto) {
        Objects.requireNonNull(monto, "El monto no puede ser nulo");
        saldo = saldo.mas(monto);
    }

    public boolean tieneFondosPara(MontoDinero monto) {
        return saldo.alcanzaPara(monto);
    }

    public String usuario() {
        return usuario;
    }

    public Saldo saldo() {
        return saldo;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof CuentaBancaria otra && usuario.equals(otra.usuario));
    }

    @Override
    public int hashCode() {
        return usuario.hashCode();
    }
}
