package com.banco.ledger.processor.domain.model;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.domain.state.TransicionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

/** Pruebas de dominio puro del agregado Transaccion (sin Spring, en memoria). */
class TransaccionTest {

    private static final TxId TX_ID = TxId.de("tx-1");
    private static final Instant SOLICITADA_EN = Instant.parse("2026-11-18T11:30:00Z");
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-18T11:30:01Z");

    private static TransferCommandRecord comando(String monto) {
        return new TransferCommandRecord(TX_ID, "A", "B", MontoDinero.de(monto), SOLICITADA_EN);
    }

    @Test
    @DisplayName("recibir() crea la transaccion en estado RECIBIDA con los datos del comando")
    void recibirCreaEnEstadoRecibida() {
        // Arrange
        TransferCommandRecord cmd = comando("50");

        // Act
        Transaccion tx = Transaccion.recibir(cmd);

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.RECIBIDA);
        assertThat(tx.txId()).isEqualTo(TX_ID);
        assertThat(tx.origen()).isEqualTo("A");
        assertThat(tx.destino()).isEqualTo("B");
        assertThat(tx.monto()).isEqualTo(MontoDinero.de("50"));
        assertThat(tx.solicitadaEn()).isEqualTo(SOLICITADA_EN);
        assertThat(tx.motivoRechazo()).isEmpty();
    }

    @Test
    @DisplayName("iniciarProcesamiento() pasa a PROCESANDO")
    void iniciarProcesamientoPasaAProcesando() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));

        // Act
        tx.iniciarProcesamiento();

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.PROCESANDO);
    }

    @Test
    @DisplayName("ejecutarTransferencia() mueve el dinero y completa la transaccion")
    void ejecutarTransferenciaMueveDineroYCompleta() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("10.00"));

        // Act
        tx.ejecutarTransferencia(origen, destino);

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.COMPLETED);
        assertThat(origen.saldo()).isEqualTo(Saldo.de("50.00"));
        assertThat(destino.saldo()).isEqualTo(Saldo.de("60.00"));
    }

    @Test
    @DisplayName("ejecutarTransferencia() sin iniciar procesamiento falla SIN mover dinero")
    void ejecutarSinProcesarNoMueveDinero() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("10.00"));

        // Act
        Throwable error = catchThrowable(() -> tx.ejecutarTransferencia(origen, destino));

        // Assert
        assertThat(error).isInstanceOf(TransicionInvalidaException.class);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.RECIBIDA);
        assertThat(origen.saldo()).isEqualTo(Saldo.de("100.00"));
        assertThat(destino.saldo()).isEqualTo(Saldo.de("10.00"));
    }

    @Test
    @DisplayName("ejecutarTransferencia() con cuentas que no corresponden lanza IllegalArgumentException")
    void ejecutarConCuentasEquivocadasLanzaExcepcion() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        CuentaBancaria otra = CuentaBancaria.abrir("C", Saldo.de("100.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("10.00"));

        // Act
        Throwable error = catchThrowable(() -> tx.ejecutarTransferencia(otra, destino));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.PROCESANDO);
        assertThat(otra.saldo()).isEqualTo(Saldo.de("100.00"));
    }

    @Test
    @DisplayName("ejecutarTransferencia() sin fondos lanza FondosInsuficientesException y no cambia nada")
    void ejecutarSinFondosNoCambiaNada() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("500"));
        tx.iniciarProcesamiento();
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("100.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("10.00"));

        // Act
        Throwable error = catchThrowable(() -> tx.ejecutarTransferencia(origen, destino));

        // Assert
        assertThat(error).isInstanceOf(FondosInsuficientesException.class);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.PROCESANDO);
        assertThat(origen.saldo()).isEqualTo(Saldo.de("100.00"));
        assertThat(destino.saldo()).isEqualTo(Saldo.de("10.00"));
    }

    @Test
    @DisplayName("rechazar() pasa a REJECTED y guarda el motivo")
    void rechazarGuardaMotivo() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();

        // Act
        tx.rechazar("ACCOUNT_NOT_FOUND");

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(tx.motivoRechazo()).contains("ACCOUNT_NOT_FOUND");
    }

    @Test
    @DisplayName("rechazarPorFondosInsuficientes() usa el motivo INSUFFICIENT_FUNDS")
    void rechazarPorFondosInsuficientes() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();

        // Act
        tx.rechazarPorFondosInsuficientes();

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(tx.motivoRechazo()).contains("INSUFFICIENT_FUNDS");
    }

    @Test
    @DisplayName("rechazar() con motivo vacio lanza IllegalArgumentException y no cambia el estado")
    void rechazarConMotivoVacioFalla() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();

        // Act
        Throwable error = catchThrowable(() -> tx.rechazar(" "));

        // Assert
        assertThat(error).isInstanceOf(IllegalArgumentException.class);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.PROCESANDO);
    }

    @Test
    @DisplayName("marcarComoSospechosa() sobre una transaccion completada pasa a SOSPECHOSA")
    void marcarComoSospechosaDespuesDeCompletar() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        tx.ejecutarTransferencia(CuentaBancaria.abrir("A", Saldo.de("100.00")),
                CuentaBancaria.abrir("B", Saldo.cero()));

        // Act
        tx.marcarComoSospechosa();

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.SOSPECHOSA);
    }

    @Test
    @DisplayName("marcarComoSospechosa() sobre una transaccion rechazada lanza IllegalStateException")
    void marcarComoSospechosaRechazadaFalla() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        tx.rechazarPorFondosInsuficientes();

        // Act
        Throwable error = catchThrowable(tx::marcarComoSospechosa);

        // Assert
        assertThat(error).isInstanceOf(IllegalStateException.class);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.REJECTED);
    }

    @Test
    @DisplayName("toEvento() de una transaccion completada no lleva motivo")
    void toEventoCompletada() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        tx.ejecutarTransferencia(CuentaBancaria.abrir("A", Saldo.de("100.00")),
                CuentaBancaria.abrir("B", Saldo.cero()));

        // Act
        TransactionEventRecord evento = tx.toEvento(OCURRIDO_EN);

        // Assert
        assertThat(evento).isEqualTo(new TransactionEventRecord(
                TX_ID, "A", "B", MontoDinero.de("50"), EstadoTransaccion.COMPLETED, null, OCURRIDO_EN));
    }

    @Test
    @DisplayName("toEvento() de una transaccion rechazada lleva el motivo como reason")
    void toEventoRechazada() {
        // Arrange
        Transaccion tx = Transaccion.recibir(comando("50"));
        tx.iniciarProcesamiento();
        tx.rechazarPorFondosInsuficientes();

        // Act
        TransactionEventRecord evento = tx.toEvento(OCURRIDO_EN);

        // Assert
        assertThat(evento.status()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(evento.reason()).isEqualTo("INSUFFICIENT_FUNDS");
    }

    @Test
    @DisplayName("reconstituir() recupera el estado y el motivo guardados")
    void reconstituirRecuperaEstado() {
        // Arrange
        MontoDinero monto = MontoDinero.de("50");

        // Act
        Transaccion tx = Transaccion.reconstituir(TX_ID, "A", "B", monto, SOLICITADA_EN,
                EstadoTransaccion.REJECTED, "INSUFFICIENT_FUNDS");

        // Assert
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(tx.motivoRechazo()).contains("INSUFFICIENT_FUNDS");
    }
}
