package com.banco.ledger.processor.domain.model;

/**
 * ENTIDAD RICA (agregado raiz) - NO es una entidad JPA.
 *
 * TODO (Miguel):
 *  - Campos finales: TxId, origen, destino, MontoDinero; campo privado EstadoTransaccionState.
 *  - Factoria estatica: Transaccion.recibir(TransferCommandRecord cmd).
 *  - Metodos semanticos que delegan en el estado: iniciarProcesamiento(),
 *    ejecutarTransferencia(CuentaBancaria origen, CuentaBancaria destino),
 *    rechazarPorFondosInsuficientes(), marcarComoSospechosa().
 *  - toEvento(): TransactionEventRecord para publicar en Kafka.
 *  - Sin setters. Getters solo los estrictamente necesarios.
 */
public final class Transaccion {
}
