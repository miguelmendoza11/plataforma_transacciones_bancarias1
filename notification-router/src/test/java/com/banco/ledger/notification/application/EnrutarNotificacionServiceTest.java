package com.banco.ledger.notification.application;

import com.banco.ledger.contracts.EmailCommandRecord;
import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.notification.application.port.EmailCommandPublisher;
import com.banco.ledger.notification.domain.DirectorioUsuarios;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrutarNotificacionServiceTest {

    @Mock
    private DirectorioUsuarios directorio;

    @Mock
    private EmailCommandPublisher publisher;

    @InjectMocks
    private EnrutarNotificacionService service;

    private static TransactionEventRecord eventoCon(EstadoTransaccion estado) {
        return new TransactionEventRecord(TxId.de("tx-1"), "A", "B", MontoDinero.de("50"),
                estado, null, Instant.parse("2026-11-18T11:31:02Z"));
    }

    @Test
    void transaccionCompletadaPublicaUnEmailAlRemitente() {
        // Arrange
        when(directorio.emailDe("A")).thenReturn("user_a@email.com");
        var evento = eventoCon(EstadoTransaccion.COMPLETED);

        // Act
        service.enrutar(evento);

        // Assert
        verify(publisher, times(1)).publicar(new EmailCommandRecord("user_a@email.com", "Envió $50.00 a B"));
    }

    @Test
    void transaccionRechazadaNoPublicaNada() {
        // Arrange
        var evento = eventoCon(EstadoTransaccion.REJECTED);

        // Act
        service.enrutar(evento);

        // Assert
        verifyNoInteractions(publisher);
    }
}
