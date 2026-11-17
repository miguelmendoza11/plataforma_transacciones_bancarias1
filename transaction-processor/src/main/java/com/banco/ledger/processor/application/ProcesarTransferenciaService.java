package com.banco.ledger.processor.application;

import com.banco.ledger.contracts.TransferCommandRecord;

/**
 * Caso de uso principal ("el cerebro").
 *
 * TODO (Miguel):
 *  - Anotar con @Service e inyectar POR CONSTRUCTOR: TransaccionRepository, CuentaRepository,
 *    LedgerEventPublisher, List<ReglaValidacion>.
 *  - @Transactional: cargar cuentas, crear Transaccion, evaluar reglas (Strategy),
 *    ejecutar o rechazar (State), persistir, publicar evento.
 *  - Idempotencia: si el tx_id ya existe (RabbitMQ puede reentregar), no reprocesar.
 *  - Prueba Mockito: verify(repo, times(1)).guardar(...) y verify(publisher, times(1)).publicar(...)
 */
public class ProcesarTransferenciaService {

    public void procesar(TransferCommandRecord comando) {
        throw new UnsupportedOperationException("TODO Miguel");
    }
}
