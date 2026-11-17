/**
 * TODO (Juanse):
 *  - SolicitarTransferenciaService (@Service, constructor con TransferCommandPublisher):
 *    TransferRequest -> TransferCommandRecord (TxId.nuevo(), Instant.now()) -> publicar -> devolver tx_id.
 *  - Prueba Mockito: verify(publisher, times(1)).publicar(argThat(...)).
 */
package com.banco.ledger.api.application;
