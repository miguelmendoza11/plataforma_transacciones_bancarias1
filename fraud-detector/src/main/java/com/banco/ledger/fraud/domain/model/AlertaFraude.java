package com.banco.ledger.fraud.domain.model;

import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.contracts.Validaciones;

import java.time.Instant;
import java.util.Objects;

/** Entidad de dominio: alerta de fraude detectada sobre una transaccion. Inmutable y sin setters. */
public final class AlertaFraude {

    private final TxId txId;
    private final String motivo;
    private final Instant detectadaEn;

    private AlertaFraude(TxId txId, String motivo, Instant detectadaEn) {
        this.txId = Objects.requireNonNull(txId, "txId no puede ser nulo");
        this.motivo = Validaciones.noVacio(motivo, "motivo");
        this.detectadaEn = Objects.requireNonNull(detectadaEn, "detectadaEn no puede ser nulo");
    }

    public static AlertaFraude detectar(TxId txId, String motivo, Instant detectadaEn) {
        return new AlertaFraude(txId, motivo, detectadaEn);
    }

    /** Contrato que se publica en fraud_alerts. */
    public FraudAlertRecord toRecord() {
        return new FraudAlertRecord(txId, motivo, detectadaEn);
    }
}
