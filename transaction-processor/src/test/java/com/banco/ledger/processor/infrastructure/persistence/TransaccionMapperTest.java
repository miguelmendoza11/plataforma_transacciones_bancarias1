package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransferCommandRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.processor.domain.model.Transaccion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

/** Pruebas puras del mapper de transacciones: sin base de datos ni Spring. */
class TransaccionMapperTest {

    private static final Instant SOLICITADA_EN = Instant.parse("2026-11-18T11:30:00Z");
    private static final Instant PROCESADA_EN = Instant.parse("2026-11-18T11:30:01Z");

    private static Transaccion transaccionProcesando() {
        Transaccion tx = Transaccion.recibir(new TransferCommandRecord(
                TxId.de("tx-1"), "A", "B", MontoDinero.de("50"), SOLICITADA_EN));
        tx.iniciarProcesamiento();
        return tx;
    }

    @Test
    @DisplayName("toEntity() de una transaccion rechazada copia todos los campos y el motivo")
    void toEntityRechazada() {
        // Arrange
        Transaccion tx = transaccionProcesando();
        tx.rechazarPorFondosInsuficientes();

        // Act
        TransaccionJpaEntity entidad = TransaccionMapper.toEntity(tx, PROCESADA_EN);

        // Assert
        assertThat(entidad.txId()).isEqualTo("tx-1");
        assertThat(entidad.fromUser()).isEqualTo("A");
        assertThat(entidad.toUser()).isEqualTo("B");
        assertThat(entidad.amount()).isEqualByComparingTo("50.00");
        assertThat(entidad.status()).isEqualTo(EstadoTransaccion.REJECTED);
        assertThat(entidad.reason()).isEqualTo("INSUFFICIENT_FUNDS");
        assertThat(entidad.requestedAt()).isEqualTo(SOLICITADA_EN);
        assertThat(entidad.processedAt()).isEqualTo(PROCESADA_EN);
    }

    @Test
    @DisplayName("toEntity() sin motivo de rechazo guarda reason = null")
    void toEntitySinMotivoGuardaNull() {
        // Arrange
        Transaccion tx = transaccionProcesando();

        // Act
        TransaccionJpaEntity entidad = TransaccionMapper.toEntity(tx, PROCESADA_EN);

        // Assert
        assertThat(entidad.status()).isEqualTo(EstadoTransaccion.PROCESANDO);
        assertThat(entidad.reason()).isNull();
    }

    @Test
    @DisplayName("toDomain() rehidrata la transaccion con su estado y motivo")
    void toDomainRehidrata() {
        // Arrange
        TransaccionJpaEntity entidad = new TransaccionJpaEntity("tx-2", "A", "B", new BigDecimal("20000.00"),
                EstadoTransaccion.COMPLETED, null, SOLICITADA_EN, PROCESADA_EN);

        // Act
        Transaccion tx = TransaccionMapper.toDomain(entidad);

        // Assert
        assertThat(tx.txId()).isEqualTo(TxId.de("tx-2"));
        assertThat(tx.origen()).isEqualTo("A");
        assertThat(tx.destino()).isEqualTo("B");
        assertThat(tx.monto()).isEqualTo(MontoDinero.de("20000"));
        assertThat(tx.solicitadaEn()).isEqualTo(SOLICITADA_EN);
        assertThat(tx.estado()).isEqualTo(EstadoTransaccion.COMPLETED);
        assertThat(tx.motivoRechazo()).isEmpty();
    }
}
