package com.banco.ledger.dashboard.domain;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.contracts.Validaciones;

import java.util.Objects;

/**
 * Modelo de lectura (lado Query de CQRS): una fila del dashboard.
 * Inmutable: marcarSospechosa() devuelve una vista nueva en estado SOSPECHOSA.
 */
public record TransaccionVista(
        TxId txId,
        String fromUser,
        String toUser,
        MontoDinero amount,
        EstadoTransaccion status,
        boolean sospechosa,
        String motivoFraude) {

    public TransaccionVista {
        Objects.requireNonNull(txId, "tx_id es obligatorio");
        fromUser = Validaciones.noVacio(fromUser, "from_user");
        toUser = Validaciones.noVacio(toUser, "to_user");
        Objects.requireNonNull(amount, "amount es obligatorio");
        Objects.requireNonNull(status, "status es obligatorio");
        if (sospechosa) {
            motivoFraude = Validaciones.noVacio(motivoFraude, "motivo_fraude");
        } else if (motivoFraude != null) {
            throw new IllegalArgumentException("Una transaccion no sospechosa no lleva motivo de fraude");
        }
    }

    public static TransaccionVista desde(TransactionEventRecord evento) {
        Objects.requireNonNull(evento, "evento no puede ser nulo");
        return new TransaccionVista(evento.txId(), evento.fromUser(), evento.toUser(),
                evento.amount(), evento.status(), false, null);
    }

    public TransaccionVista marcarSospechosa(String motivo) {
        return new TransaccionVista(txId, fromUser, toUser, amount, EstadoTransaccion.SOSPECHOSA, true, motivo);
    }
}
