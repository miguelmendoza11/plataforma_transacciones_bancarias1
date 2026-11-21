package com.banco.ledger.dashboard.infrastructure.websocket;

import com.banco.ledger.contracts.EstadoTransaccion;
import com.banco.ledger.contracts.MontoDinero;
import com.banco.ledger.contracts.TransactionEventRecord;
import com.banco.ledger.contracts.TxId;
import com.banco.ledger.dashboard.domain.TransaccionVista;
import com.banco.ledger.dashboard.domain.VistaMaterializada;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebSocketDashboardNotifierTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private WebSocketSession session;

    private VistaMaterializada vista;
    private TransaccionesWebSocketHandler handler;
    private WebSocketDashboardNotifier notifier;

    @BeforeEach
    void setUp() {
        vista = new VistaMaterializada();
        handler = new TransaccionesWebSocketHandler(vista, objectMapper);
        notifier = new WebSocketDashboardNotifier(handler);
    }

    private static TransactionEventRecord evento(String txId) {
        return new TransactionEventRecord(TxId.de(txId), "A", "B", MontoDinero.de("20000"),
                EstadoTransaccion.COMPLETED, null, Instant.parse("2026-01-01T10:00:00Z"));
    }

    @Test
    void difundeElJsonEsperadoALaSesionAbierta() throws Exception {
        // Arrange
        when(session.isOpen()).thenReturn(true);
        handler.afterConnectionEstablished(session);
        TransaccionVista sospechosa = TransaccionVista.desde(evento("tx-1")).marcarSospechosa("HIGH_VALUE");

        // Act
        notifier.difundir(sospechosa);

        // Assert
        var captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session, times(1)).sendMessage(captor.capture());
        String esperado = """
                {"tx_id":"tx-1","from_user":"A","to_user":"B","amount":20000.00,
                 "status":"SOSPECHOSA","sospechosa":true,"motivo_fraude":"HIGH_VALUE"}
                """;
        assertThat(objectMapper.readTree(captor.getValue().getPayload()))
                .isEqualTo(objectMapper.readTree(esperado));
    }

    @Test
    void alConectarEnviaElSnapshot() throws Exception {
        // Arrange
        vista.registrar(evento("tx-1"));
        vista.registrar(evento("tx-2"));
        when(session.isOpen()).thenReturn(true);

        // Act
        handler.afterConnectionEstablished(session);

        // Assert
        verify(session, times(2)).sendMessage(any(TextMessage.class));
    }

    @Test
    void noEnviaASesionesCerradas() throws Exception {
        // Arrange
        handler.afterConnectionEstablished(session);
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Act
        notifier.difundir(TransaccionVista.desde(evento("tx-1")));

        // Assert
        verify(session, never()).sendMessage(any());
    }
}
