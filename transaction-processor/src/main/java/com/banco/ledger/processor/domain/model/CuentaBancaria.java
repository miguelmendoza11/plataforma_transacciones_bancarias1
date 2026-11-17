package com.banco.ledger.processor.domain.model;

/**
 * ENTIDAD RICA - protege la invariante "el saldo nunca es negativo".
 *
 * TODO (Miguel):
 *  - debitar(MontoDinero): lanza IllegalStateException si no hay fondos.
 *  - acreditar(MontoDinero).
 *  - tieneFondosPara(MontoDinero): boolean.
 *  - OJO: MontoDinero no admite cero; el saldo puede ser 0, asi que modela el saldo
 *    con BigDecimal interno o con otro Value Object (ej. Saldo).
 */
public final class CuentaBancaria {
}
