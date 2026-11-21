package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Pruebas de la rehidratacion del estado desde su nombre persistido
 * (EstadoTransaccionState.desde, respaldado por un EnumMap).
 */
class BusquedaEstadoTest {

    @ParameterizedTest(name = "desde({0}) devuelve un estado cuyo nombre es {0}")
    @EnumSource(EstadoTransaccion.class)
    void cadaNombreTieneSuEstado(EstadoTransaccion nombre) {
        // Arrange / Act
        EstadoTransaccionState estado = EstadoTransaccionState.desde(nombre);

        // Assert (ida y vuelta: nombre -> estado -> nombre)
        assertThat(estado.nombre()).isEqualTo(nombre);
    }

    @Test
    @DisplayName("desde() devuelve la implementacion concreta correcta")
    void devuelveLaClaseCorrecta() {
        // Arrange / Act / Assert
        assertThat(EstadoTransaccionState.desde(EstadoTransaccion.RECIBIDA)).isInstanceOf(Recibida.class);
        assertThat(EstadoTransaccionState.desde(EstadoTransaccion.PROCESANDO)).isInstanceOf(Procesando.class);
        assertThat(EstadoTransaccionState.desde(EstadoTransaccion.COMPLETED)).isInstanceOf(Completada.class);
        assertThat(EstadoTransaccionState.desde(EstadoTransaccion.REJECTED)).isInstanceOf(Rechazada.class);
        assertThat(EstadoTransaccionState.desde(EstadoTransaccion.SOSPECHOSA)).isInstanceOf(Sospechosa.class);
    }

    @Test
    @DisplayName("Un estado rehidratado conserva su comportamiento (COMPLETED puede marcarse sospechosa)")
    void estadoRehidratadoConservaSuComportamiento() {
        // Arrange
        EstadoTransaccionState completada = EstadoTransaccionState.desde(EstadoTransaccion.COMPLETED);

        // Act
        EstadoTransaccionState siguiente = completada.marcarComoSospechosa();

        // Assert
        assertThat(siguiente.nombre()).isEqualTo(EstadoTransaccion.SOSPECHOSA);
    }

    @Test
    @DisplayName("Un estado final rehidratado sigue bloqueado")
    void estadoFinalRehidratadoSigueBloqueado() {
        // Arrange
        EstadoTransaccionState rechazada = EstadoTransaccionState.desde(EstadoTransaccion.REJECTED);

        // Act / Assert
        assertThatThrownBy(rechazada::completar).isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    @DisplayName("desde(null) falla rapido con IllegalArgumentException")
    void nombreNuloLanzaIllegalArgument() {
        // Act / Assert
        assertThatThrownBy(() -> EstadoTransaccionState.desde(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nulo");
    }
}
