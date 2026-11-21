package com.banco.ledger.dashboard.domain;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.FraudAlertRecord;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class VistaMaterializadaTest {

    private static final Instant AHORA = Instant.parse("2026-01-01T10:00:00Z");

    private final VistaMaterializada vista = new VistaMaterializada();

    private static TransactionEventRecord evento(String txId) {
        return new TransactionEventRecord(TxId.de(txId), "A", "B", MontoDinero.de("20000"),
                EstadoTransaccion.COMPLETED, null, AHORA);
    }

    private static FraudAlertRecord alerta(String txId, String motivo) {
        return new FraudAlertRecord(TxId.de(txId), motivo, AHORA);
    }

    @Test
    void registraUnaTransaccionNoSospechosa() {
        // Act
        var registrada = vista.registrar(evento("tx-1"));

        // Assert
        assertThat(registrada.txId()).isEqualTo(TxId.de("tx-1"));
        assertThat(registrada.sospechosa()).isFalse();
        assertThat(vista.snapshot()).containsExactly(registrada);
    }

    @Test
    void marcaComoSospechosaUnaTransaccionExistente() {
        // Arrange
        vista.registrar(evento("tx-1"));

        // Act
        var marcada = vista.marcarSospechosa(alerta("tx-1", "HIGH_VALUE"));

        // Assert
        assertThat(marcada).isPresent();
        assertThat(marcada.get().sospechosa()).isTrue();
        assertThat(marcada.get().motivoFraude()).isEqualTo("HIGH_VALUE");
        assertThat(vista.snapshot()).containsExactly(marcada.get());
    }

    @Test
    void alertaQueLlegaAntesQuedaPendienteYSeAplicaAlRegistrar() {
        // Act
        var resultadoAlerta = vista.marcarSospechosa(alerta("tx-1", "HIGH_FREQUENCY"));
        var snapshotIntermedio = vista.snapshot();
        var registrada = vista.registrar(evento("tx-1"));

        // Assert
        assertThat(resultadoAlerta).isEmpty();
        assertThat(snapshotIntermedio).isEmpty();
        assertThat(registrada.sospechosa()).isTrue();
        assertThat(registrada.motivoFraude()).isEqualTo("HIGH_FREQUENCY");
    }

    @Test
    void laAlertaPendienteSoloSeAplicaUnaVez() {
        // Arrange
        vista.marcarSospechosa(alerta("tx-1", "HIGH_VALUE"));
        vista.registrar(evento("tx-1"));

        // Act
        var otra = vista.registrar(evento("tx-2"));

        // Assert
        assertThat(otra.sospechosa()).isFalse();
    }

    @Test
    void reentregaDeLaTransaccionConservaLaMarca() {
        // Arrange
        vista.registrar(evento("tx-1"));
        vista.marcarSospechosa(alerta("tx-1", "HIGH_VALUE"));

        // Act
        var reentregada = vista.registrar(evento("tx-1"));

        // Assert
        assertThat(reentregada.sospechosa()).isTrue();
        assertThat(reentregada.motivoFraude()).isEqualTo("HIGH_VALUE");
        assertThat(vista.snapshot()).hasSize(1);
    }

    @Test
    void snapshotDevuelveTodasLasTransacciones() {
        // Arrange
        vista.registrar(evento("tx-1"));
        vista.registrar(evento("tx-2"));
        vista.registrar(evento("tx-3"));

        // Act
        var snapshot = vista.snapshot();

        // Assert
        assertThat(snapshot).extracting(TransaccionVista::txId)
                .containsExactlyInAnyOrder(TxId.de("tx-1"), TxId.de("tx-2"), TxId.de("tx-3"));
    }

    @Test
    void ningunaAlertaSePierdeConHilosConcurrentes() throws Exception {
        // Arrange: por cada tx una tarea que registra y otra que alerta, en orden aleatorio
        int total = 500;
        List<Callable<Object>> tareas = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            String txId = "tx-" + i;
            tareas.add(() -> vista.registrar(evento(txId)));
            tareas.add(() -> vista.marcarSospechosa(alerta(txId, "HIGH_VALUE")));
        }
        Collections.shuffle(tareas);
        ExecutorService executor = Executors.newFixedThreadPool(8);

        // Act
        try {
            for (Future<Object> futuro : executor.invokeAll(tareas)) {
                futuro.get();
            }
        } finally {
            executor.shutdown();
        }

        // Assert
        assertThat(vista.snapshot()).hasSize(total)
                .allSatisfy(v -> assertThat(v.sospechosa()).isTrue());
    }
}
