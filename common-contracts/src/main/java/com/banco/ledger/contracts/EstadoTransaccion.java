package com.banco.ledger.contracts;

/**
 * Estado publicado en los contratos (eventos). OJO: esto es solo el "nombre" del estado
 * que viaja por la red. El comportamiento del ciclo de vida (patron State) vive en el
 * dominio de transaction-processor, NO aqui.
 */
public enum EstadoTransaccion {
    RECIBIDA,
    PROCESANDO,
    COMPLETED,
    REJECTED,
    SOSPECHOSA
}
