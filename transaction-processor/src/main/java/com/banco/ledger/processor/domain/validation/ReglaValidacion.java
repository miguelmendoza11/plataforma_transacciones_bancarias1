package com.banco.ledger.processor.domain.validation;

import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Transaccion;

/**
 * PATRON STRATEGY - reglas de validacion de riesgo intercambiables.
 *
 * Cada regla es un algoritmo independiente que decide si una transaccion puede ejecutarse.
 * El servicio recibe List&lt;ReglaValidacion&gt; por constructor y las evalua todas sin saber
 * cuales son: agregar una regla nueva no obliga a tocar el procesador (Open/Closed).
 */
public interface ReglaValidacion {

    ResultadoValidacion evaluar(Transaccion tx, CuentaBancaria origen);
}
