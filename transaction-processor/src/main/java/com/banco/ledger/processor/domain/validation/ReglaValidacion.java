package com.banco.ledger.processor.domain.validation;

/**
 * PATRON STRATEGY - reglas de validacion de riesgo intercambiables.
 *
 * TODO (Miguel):
 *  - Definir que recibe la regla (ej. Transaccion + CuentaBancaria origen) y que devuelve
 *    (ej. un record ResultadoValidacion(boolean aprobada, String motivo)).
 *  - Implementaciones sugeridas: FondosSuficientesRule, MontoMaximoPorTransaccionRule,
 *    CuentaActivaRule.
 *  - El servicio recibe List<ReglaValidacion> por constructor (Spring inyecta todas las
 *    implementaciones) -> agregar reglas nuevas sin tocar el procesador (Open/Closed).
 */
public interface ReglaValidacion {
    // TODO: ResultadoValidacion evaluar(Transaccion tx, CuentaBancaria origen);
}
