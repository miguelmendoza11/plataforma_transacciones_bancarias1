package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de dominio puro del patron State: transiciones PERMITIDAS.
 * Sin Spring, sin base de datos, sin brokers: corren en milisegundos.
 */
class TransicionesValidasTest {

    @Test
    @DisplayName("Toda transaccion nace en estado RECIBIDA")
    void estadoInicialEsRecibida() {
        // Arrange / Act
        EstadoTransaccionState estado = EstadoTransaccionState.inicial();

        // Assert
        assertThat(estado).isInstanceOf(Recibida.class);
        assertThat(estado.nombre()).isEqualTo(EstadoTransaccion.RECIBIDA);
    }

    @Test
    @DisplayName("RECIBIDA -> iniciarProcesamiento() -> PROCESANDO")
    void recibidaPasaAProcesando() {
        // Arrange
        EstadoTransaccionState recibida = new Recibida();

        // Act
        EstadoTransaccionState siguiente = recibida.iniciarProcesamiento();

        // Assert
        assertThat(siguiente).isEqualTo(new Procesando());
        assertThat(siguiente.nombre()).isEqualTo(EstadoTransaccion.PROCESANDO);
    }

    @Test
    @DisplayName("PROCESANDO -> completar() -> COMPLETED")
    void procesandoPasaACompletada() {
        // Arrange
        EstadoTransaccionState procesando = new Procesando();

        // Act
        EstadoTransaccionState siguiente = procesando.completar();

        // Assert
        assertThat(siguiente).isEqualTo(new Completada());
        assertThat(siguiente.nombre()).isEqualTo(EstadoTransaccion.COMPLETED);
    }

    @Test
    @DisplayName("PROCESANDO -> rechazar() -> REJECTED")
    void procesandoPasaARechazada() {
        // Arrange
        EstadoTransaccionState procesando = new Procesando();

        // Act
        EstadoTransaccionState siguiente = procesando.rechazar();

        // Assert
        assertThat(siguiente).isEqualTo(new Rechazada());
        assertThat(siguiente.nombre()).isEqualTo(EstadoTransaccion.REJECTED);
    }

    @Test
    @DisplayName("COMPLETED -> marcarComoSospechosa() -> SOSPECHOSA")
    void completadaPasaASospechosa() {
        // Arrange
        EstadoTransaccionState completada = new Completada();

        // Act
        EstadoTransaccionState siguiente = completada.marcarComoSospechosa();

        // Assert
        assertThat(siguiente).isEqualTo(new Sospechosa());
        assertThat(siguiente.nombre()).isEqualTo(EstadoTransaccion.SOSPECHOSA);
    }

    @Test
    @DisplayName("Camino completo: RECIBIDA -> PROCESANDO -> COMPLETED -> SOSPECHOSA")
    void recorreElCicloDeVidaCompleto() {
        // Arrange
        EstadoTransaccionState estado = EstadoTransaccionState.inicial();

        // Act
        EstadoTransaccionState finalSospechosa = estado
                .iniciarProcesamiento()
                .completar()
                .marcarComoSospechosa();

        // Assert
        assertThat(finalSospechosa.nombre()).isEqualTo(EstadoTransaccion.SOSPECHOSA);
    }

    @Test
    @DisplayName("Las transiciones no mutan el estado original (inmutabilidad)")
    void transicionDevuelveNuevoEstadoSinMutarElOriginal() {
        // Arrange
        EstadoTransaccionState recibida = new Recibida();

        // Act
        recibida.iniciarProcesamiento();

        // Assert
        assertThat(recibida.nombre()).isEqualTo(EstadoTransaccion.RECIBIDA);
    }
}
