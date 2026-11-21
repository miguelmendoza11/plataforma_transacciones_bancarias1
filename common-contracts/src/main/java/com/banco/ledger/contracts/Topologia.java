package com.banco.ledger.contracts;

/** Nombres de colas y topics compartidos, para no repetir "magic strings" en cada servicio. */
public final class Topologia {

    private Topologia() {
        throw new AssertionError("No instanciable");
    }

    // RabbitMQ
    public static final String COLA_TRANSFER_COMMANDS = "transfer_commands";
    /**
     * Dead Letter Queue de transfer_commands: los comandos que fallan 3 veces van aqui en vez de perderse.
     * OJO: transfer_commands se declara con x-dead-letter-exchange="" y
     * x-dead-letter-routing-key=transfer_commands.dlq. Cada servicio que declare esa cola
     * (transaction-api y transaction-processor) debe usar los MISMOS argumentos; si no,
     * RabbitMQ rechaza la declaracion con PRECONDITION_FAILED.
     */
    public static final String COLA_TRANSFER_COMMANDS_DLQ = "transfer_commands.dlq";
    public static final String COLA_EMAIL = "email_queue";

    // Kafka
    public static final String TOPIC_TRANSACTIONS_LOG = "transactions_log";
    public static final String TOPIC_FRAUD_ALERTS = "fraud_alerts";
}
