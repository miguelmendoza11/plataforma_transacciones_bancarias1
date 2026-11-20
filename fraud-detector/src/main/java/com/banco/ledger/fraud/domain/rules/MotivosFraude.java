package com.banco.ledger.fraud.domain.rules;

/** Motivos de alerta que se publican en fraud_alerts. */
public final class MotivosFraude {

    public static final String HIGH_VALUE = "HIGH_VALUE";
    public static final String HIGH_FREQUENCY = "HIGH_FREQUENCY";

    private MotivosFraude() {
        throw new AssertionError("No instanciable");
    }
}
