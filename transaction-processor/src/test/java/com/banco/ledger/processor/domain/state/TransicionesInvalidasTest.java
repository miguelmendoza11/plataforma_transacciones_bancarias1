package com.banco.ledger.processor.domain.state;

import com.banco.ledger.contracts.EstadoTransaccion;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Pruebas de dominio puro del patron State: transiciones PROHIBIDAS.
 *
 * 5 estados x 4 transiciones = 20 combinaciones; 4 son validas, asi que aqui se prueban
 * las 16 restantes. Cada una debe lanzar TransicionInvalidaException (una IllegalStateException)
 * indicando el estado en el que estaba la transaccion.
 */
class TransicionesInvalidasTest {

    private static final UnaryOperator<EstadoTransaccionState> INICIAR = EstadoTransaccionState::iniciarProcesamiento;
    private static final UnaryOperator<EstadoTransaccionState> COMPLETAR = EstadoTransaccionState::completar;
    private static final UnaryOperator<EstadoTransaccionState> RECHAZAR = EstadoTransaccionState::rechazar;
    private static final UnaryOperator<EstadoTransaccionState> SOSPECHAR = EstadoTransaccionState::marcarComoSospechosa;

    static Stream<Arguments> transicionesProhibidas() {
        return Stream.of(
                // RECIBIDA: solo puede iniciar procesamiento
                arguments(new Recibida(), "completar", COMPLETAR),
                arguments(new Recibida(), "rechazar", RECHAZAR),
                arguments(new Recibida(), "marcarComoSospechosa", SOSPECHAR),
                // PROCESANDO: solo puede completar o rechazar
                arguments(new Procesando(), "iniciarProcesamiento", INICIAR),
                arguments(new Procesando(), "marcarComoSospechosa", SOSPECHAR),
                // COMPLETED: solo puede marcarse como sospechosa
                arguments(new Completada(), "iniciarProcesamiento", INICIAR),
                arguments(new Completada(), "completar", COMPLETAR),
                arguments(new Completada(), "rechazar", RECHAZAR),
                // REJECTED: estado final
                arguments(new Rechazada(), "iniciarProcesamiento", INICIAR),
                arguments(new Rechazada(), "completar", COMPLETAR),
                arguments(new Rechazada(), "rechazar", RECHAZAR),
                arguments(new Rechazada(), "marcarComoSospechosa", SOSPECHAR),
                // SOSPECHOSA: estado final
                arguments(new Sospechosa(), "iniciarProcesamiento", INICIAR),
                arguments(new Sospechosa(), "completar", COMPLETAR),
                arguments(new Sospechosa(), "rechazar", RECHAZAR),
                arguments(new Sospechosa(), "marcarComoSospechosa", SOSPECHAR));
    }

    @ParameterizedTest(name = "{0} no permite {1}()")
    @MethodSource("transicionesProhibidas")
    void transicionProhibidaLanzaExcepcion(EstadoTransaccionState estado,
                                           String accion,
                                           UnaryOperator<EstadoTransaccionState> transicion) {
        // Arrange
        EstadoTransaccion nombreEstado = estado.nombre();

        // Act / Assert
        assertThatThrownBy(() -> transicion.apply(estado))
                .isInstanceOf(TransicionInvalidaException.class)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("No se puede ")
                .hasMessageEndingWith("una transaccion en estado " + nombreEstado)
                .satisfies(e -> assertThat(((TransicionInvalidaException) e).estadoActual())
                        .isEqualTo(nombreEstado));
    }
}
