package com.banco.ledger.processor.infrastructure.persistence;

import com.banco.ledger.processor.application.port.CuentaRepository;
import com.banco.ledger.processor.domain.model.CuentaBancaria;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Adaptador (arquitectura hexagonal): implementa el puerto CuentaRepository con Spring Data JPA.
 * El caso de uso solo ve el puerto; esta clase es la unica que sabe que hay PostgreSQL.
 */
@Component
public class JpaCuentaRepositoryAdapter implements CuentaRepository {

    private final SpringDataCuentaRepository repository;

    public JpaCuentaRepositoryAdapter(SpringDataCuentaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    @Override
    public Optional<CuentaBancaria> buscarPorUsuario(String usuario) {
        return repository.findById(usuario).map(CuentaMapper::toDomain);
    }

    @Override
    public void guardar(CuentaBancaria cuenta) {
        // Si la cuenta ya existe se modifica la entidad cargada, para que @Version funcione
        CuentaJpaEntity entidad = repository.findById(cuenta.usuario())
                .map(existente -> {
                    CuentaMapper.actualizar(existente, cuenta);
                    return existente;
                })
                .orElseGet(() -> CuentaMapper.toEntity(cuenta));
        repository.save(entidad);
    }
}
