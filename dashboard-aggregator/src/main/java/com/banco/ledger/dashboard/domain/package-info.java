/**
 * TODO (Juanse):
 *  - TransaccionVista: modelo de lectura (CQRS - lado Query) con metodo marcarSospechosa(motivo).
 *  - VistaMaterializada: ConcurrentHashMap<TxId, TransaccionVista> (stateful, en memoria).
 *    Metodos: registrar(TransactionEventRecord), marcarSospechosa(FraudAlertRecord), snapshot().
 *    Caso borde: puede llegar la alerta ANTES que el evento (topics distintos, sin orden global)
 *    -> guardar alertas pendientes y aplicarlas cuando llegue la transaccion.
 */
package com.banco.ledger.dashboard.domain;
