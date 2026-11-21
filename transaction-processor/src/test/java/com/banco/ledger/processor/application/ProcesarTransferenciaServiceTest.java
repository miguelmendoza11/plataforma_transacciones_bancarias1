package com.banco.ledger.processor.application;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.application.port.CuentaRepository;
import com.banco.ledger.processor.application.port.LedgerEventPublisher;
import com.banco.ledger.processor.application.port.TransaccionRepository;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import com.banco.ledger.processor.domain.model.Saldo;
import com.banco.ledger.processor.domain.model.Transaccion;
import com.banco.ledger.processor.domain.validation.FondosSuficientesRule;
import com.banco.ledger.processor.domain.validation.MontoMaximoPorTransaccionRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas de orquestacion del caso de uso con Mockito (sin Spring, sin BD, sin brokers).
 * El servicio se construye a mano porque recibe una List de reglas y un Clock.
 */
@ExtendWith(MockitoExtension.class)
class ProcesarTransferenciaServiceTest {

    private static final Instant AHORA = Instant.parse("2026-11-18T11:30:01Z");
    private static final TxId TX_ID = TxId.de("tx-1");

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private LedgerEventPublisher publisher;

    private ProcesarTransferenciaService service;

    @BeforeEach
    void setUp() {
        service = new ProcesarTransferenciaService(
                transaccionRepository,
                cuentaRepository,
                publisher,
                List.of(new FondosSuficientesRule(), new MontoMaximoPorTransaccionRule(MontoDinero.de("100000"))),
                Clock.fixed(AHORA, ZoneOffset.UTC));
    }

    private static TransferCommandRecord comando(String monto) {
        return new TransferCommandRecord(TX_ID, "A", "B", MontoDinero.de(monto), Instant.parse("2026-11-18T11:30:00Z"));
    }

    @Test
    @DisplayName("Transferencia valida: guarda la transaccion 1 vez y publica 1 evento COMPLETED")
    void transferenciaValidaGuardaYPublicaUnaVez() {
        // Arrange
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("1000.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("0.00"));
        when(transaccionRepository.existe(TX_ID)).thenReturn(false);
        when(cuentaRepository.buscarPorUsuario("A")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorUsuario("B")).thenReturn(Optional.of(destino));
        ArgumentCaptor<TransactionEventRecord> evento = ArgumentCaptor.forClass(TransactionEventRecord.class);

        // Act
        service.procesar(comando("50"));

        // Assert
        verify(transaccionRepository, times(1)).guardar(any(Transaccion.class));
        verify(publisher, times(1)).publicar(evento.capture());
        assertThat(evento.getValue().txId()).isEqualTo(TX_ID);
        assertThat(evento.getValue().status()).isEqualTo(EstadoTransaccion.COMPLETED);
        assertThat(evento.getValue().reason()).isNull();
        assertThat(evento.getValue().occurredAt()).isEqualTo(AHORA);
    }

    @Test
    @DisplayName("Transferencia valida: guarda ambas cuentas con los saldos actualizados")
    void transferenciaValidaGuardaAmbasCuentas() {
        // Arrange
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("1000.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("0.00"));
        when(transaccionRepository.existe(TX_ID)).thenReturn(false);
        when(cuentaRepository.buscarPorUsuario("A")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorUsuario("B")).thenReturn(Optional.of(destino));

        // Act
        service.procesar(comando("50"));

        // Assert
        verify(cuentaRepository, times(1)).guardar(origen);
        verify(cuentaRepository, times(1)).guardar(destino);
        assertThat(origen.saldo()).isEqualTo(Saldo.de("950.00"));
        assertThat(destino.saldo()).isEqualTo(Saldo.de("50.00"));
    }

    @Test
    @DisplayName("Transferencia de $20.000 se completa (el fraude la marca despues)")
    void transferenciaDeVeinteMilSeCompleta() {
        // Arrange
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("1000000.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("0.00"));
        when(transaccionRepository.existe(TX_ID)).thenReturn(false);
        when(cuentaRepository.buscarPorUsuario("A")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorUsuario("B")).thenReturn(Optional.of(destino));
        ArgumentCaptor<TransactionEventRecord> evento = ArgumentCaptor.forClass(TransactionEventRecord.class);

        // Act
        service.procesar(comando("20000"));

        // Assert
        verify(transaccionRepository, times(1)).guardar(any(Transaccion.class));
        verify(publisher, times(1)).publicar(evento.capture());
        assertThat(evento.getValue().status()).isEqualTo(EstadoTransaccion.COMPLETED);
    }

    @Test
    @DisplayName("Fondos insuficientes: publica REJECTED con INSUFFICIENT_FUNDS y no guarda ninguna cuenta")
    void fondosInsuficientesPublicaRejected() {
        // Arrange
        CuentaBancaria origen = CuentaBancaria.abrir("A", Saldo.de("10.00"));
        CuentaBancaria destino = CuentaBancaria.abrir("B", Saldo.de("0.00"));
        when(transaccionRepository.existe(TX_ID)).thenReturn(false);
        when(cuentaRepository.buscarPorUsuario("A")).thenReturn(Optional.of(origen));
        when(cuentaRepository.buscarPorUsuario("B")).thenReturn(Optional.of(destino));
        ArgumentCaptor<TransactionEventRecord> evento = ArgumentCaptor.forClass(TransactionEventRecord.class);

        // Act
        service.procesar(comando("50"));

        // Assert
        verify(transaccionRepository, times(1)).guardar(any(Transaccion.class));
        verify(publisher, times(1)).publicar(evento.capture());
        assertThat(evento.getValue().status()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(evento.getValue().reason()).isEqualTo("INSUFFICIENT_FUNDS");
        verify(cuentaRepository, never()).guardar(any(CuentaBancaria.class));
        assertThat(origen.saldo()).isEqualTo(Saldo.de("10.00"));
    }

    @Test
    @DisplayName("Cuenta inexistente: publica REJECTED con ACCOUNT_NOT_FOUND")
    void cuentaInexistentePublicaRejected() {
        // Arrange
        when(transaccionRepository.existe(TX_ID)).thenReturn(false);
        when(cuentaRepository.buscarPorUsuario("A")).thenReturn(Optional.of(CuentaBancaria.abrir("A", Saldo.de("100.00"))));
        when(cuentaRepository.buscarPorUsuario("B")).thenReturn(Optional.empty());
        ArgumentCaptor<TransactionEventRecord> evento = ArgumentCaptor.forClass(TransactionEventRecord.class);

        // Act
        service.procesar(comando("50"));

        // Assert
        verify(transaccionRepository, times(1)).guardar(any(Transaccion.class));
        verify(publisher, times(1)).publicar(evento.capture());
        assertThat(evento.getValue().status()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(evento.getValue().reason()).isEqualTo("ACCOUNT_NOT_FOUND");
        verify(cuentaRepository, never()).guardar(any(CuentaBancaria.class));
    }

    @Test
    @DisplayName("tx_id repetido (reentrega de RabbitMQ): no se reprocesa ni se publica")
    void txIdRepetidoNoSeReprocesa() {
        // Arrange
        when(transaccionRepository.existe(TX_ID)).thenReturn(true);

        // Act
        service.procesar(comando("50"));

        // Assert
        verifyNoInteractions(publisher);
        verifyNoInteractions(cuentaRepository);
        verify(transaccionRepository, never()).guardar(any(Transaccion.class));
    }
}
