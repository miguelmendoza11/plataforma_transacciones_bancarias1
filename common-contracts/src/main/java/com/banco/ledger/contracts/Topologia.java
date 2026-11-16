package com.banco.ledger.contracts;

/** Nombres de colas y topics compartidos, para no repetir "magic strings" en cada servicio. */
public final class Topologia {

    private Topologia() {
        throw new AssertionError("No instanciable");
    }

    // RabbitMQ
    public static final String COLA_TRANSFER_COMMANDS = "transfer_commands";
    public static final String COLA_EMAIL = "email_queue";

    // Kafka
    public static final String TOPIC_TRANSACTIONS_LOG = "transactions_log";
    public static final String TOPIC_FRAUD_ALERTS = "fraud_alerts";
}
