package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.contracts.Validaciones;
import com.banco.ledger.processor.domain.state.EstadoTransaccionState;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * ENTIDAD RICA (agregado raiz) - NO es una entidad JPA.
 *
 * Sin setters: el ciclo de vida solo avanza con metodos semanticos (iniciarProcesamiento(),
 * ejecutarTransferencia(), rechazar(), marcarComoSospechosa()), que DELEGAN en el objeto
 * estado (patron State). Si la transicion no esta permitida, el estado lanza
 * TransicionInvalidaException y la Transaccion no cambia.
 */
public final class Transaccion {

    private static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";

    private final TxId txId;
    private final String origen;
    private final String destino;
    private final MontoDinero monto;
    private final Instant solicitadaEn;
    private EstadoTransaccionState estado;
    private String motivoRechazo;

    private Transaccion(TxId txId, String origen, String destino, MontoDinero monto,
                        Instant solicitadaEn, EstadoTransaccionState estado, String motivoRechazo) {
        this.txId = Objects.requireNonNull(txId, "txId no puede ser nulo");
        this.origen = Validaciones.noVacio(origen, "origen");
        this.destino = Validaciones.noVacio(destino, "destino");
        this.monto = Objects.requireNonNull(monto, "monto no puede ser nulo");
        this.solicitadaEn = Objects.requireNonNull(solicitadaEn, "solicitadaEn no puede ser nulo");
        this.estado = Objects.requireNonNull(estado, "estado no puede ser nulo");
        this.motivoRechazo = motivoRechazo;
    }

    /** Crea la transaccion a partir del comando recibido por RabbitMQ, en estado inicial. */
    public static Transaccion recibir(TransferCommandRecord cmd) {
        Objects.requireNonNull(cmd, "El comando no puede ser nulo");
        return new Transaccion(cmd.txId(), cmd.fromUser(), cmd.toUser(), cmd.amount(),
                cmd.requestedAt(), EstadoTransaccionState.inicial(), null);
    }

    /** Reconstruye una transaccion existente (la usa el adaptador de persistencia). */
    public static Transaccion reconstituir(TxId txId, String origen, String destino, MontoDinero monto,
                                           Instant solicitadaEn, EstadoTransaccion estado, String motivoRechazo) {
        return new Transaccion(txId, origen, destino, monto, solicitadaEn,
                EstadoTransaccionState.desde(estado), motivoRechazo);
    }

    public void iniciarProcesamiento() {
        estado = estado.iniciarProcesamiento();
    }

    /**
     * Mueve el dinero de la cuenta origen a la cuenta destino y completa la transaccion.
     * Primero calcula el siguiente estado: si el estado actual no permite completar,
     * falla ANTES de mover dinero.
     *
     * @throws IllegalArgumentException si las cuentas no corresponden a origen/destino
     * @throws FondosInsuficientesException si la cuenta origen no tiene saldo suficiente
     */
    public void ejecutarTransferencia(CuentaBancaria cuentaOrigen, CuentaBancaria cuentaDestino) {
        Objects.requireNonNull(cuentaOrigen, "La cuenta origen no puede ser nula");
        Objects.requireNonNull(cuentaDestino, "La cuenta destino no puede ser nula");
        if (!cuentaOrigen.usuario().equals(origen) || !cuentaDestino.usuario().equals(destino)) {
            throw new IllegalArgumentException(
                    "Las cuentas no corresponden a la transaccion " + txId + " (" + origen + " -> " + destino + ")");
        }
        var siguiente = estado.completar();
        cuentaOrigen.debitar(monto);
        cuentaDestino.acreditar(monto);
        estado = siguiente;
    }

    public void rechazar(String motivo) {
        String motivoValidado = Validaciones.noVacio(motivo, "motivo");
        estado = estado.rechazar();
        motivoRechazo = motivoValidado;
    }

    public void rechazarPorFondosInsuficientes() {
        rechazar(INSUFFICIENT_FUNDS);
    }

    public void marcarComoSospechosa() {
        estado = estado.marcarComoSospechosa();
    }

    /** Evento inmutable que se publica en Kafka (transactions_log). */
    public TransactionEventRecord toEvento(Instant ocurridoEn) {
        return new TransactionEventRecord(txId, origen, destino, monto, estado.nombre(), motivoRechazo, ocurridoEn);
    }

    public TxId txId() {
        return txId;
    }

    public String origen() {
        return origen;
    }

    public String destino() {
        return destino;
    }

    public MontoDinero monto() {
        return monto;
    }

    public Instant solicitadaEn() {
        return solicitadaEn;
    }

    public EstadoTransaccion estado() {
        return estado.nombre();
    }

    public Optional<String> motivoRechazo() {
        return Optional.ofNullable(motivoRechazo);
    }
}
