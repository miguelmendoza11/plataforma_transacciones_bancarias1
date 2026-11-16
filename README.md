# Plataforma de Transacciones Bancarias (Ledger de Eventos)

Núcleo de un sistema bancario P2P con **CQRS / Event Sourcing**:
RabbitMQ como *Command Bus*, Kafka como *Event Bus* (ledger inmutable) y PostgreSQL para auditoría.

Proyecto para **Programación Orientada a Objetos**, **Sistemas Operativos** y **Sistemas Distribuidos**.
Equipo: Miguel, Juanse, Andrés. Reparto en [`docs/REPARTO.md`](docs/REPARTO.md).

## Arquitectura

```
POST /transfer
     │
 transaction-api ──(RabbitMQ: transfer_commands)──► transaction-processor ──► PostgreSQL
                                                          │
                                         (Kafka: transactions_log, key = tx_id)
                         ┌────────────────────────┼─────────────────────────┐
                         ▼                        ▼                         ▼
                  fraud-detector         dashboard-aggregator       notification-router
                   │        │              ▲        │ WebSocket :8081       │
            PostgreSQL  (Kafka: fraud_alerts)┘      ▼                       │
                                             dashboard.html    (RabbitMQ: email_queue)
                                                                 ▲          │
                                                     ad-generator┘          ▼
                                                               email-worker ×3 réplicas
```

## Estructura del repositorio

| Módulo | Rol | Responsable | Estado |
|---|---|---|---|
| `common-contracts` | Records inmutables compartidos (`TransferCommandRecord`, `TransactionEventRecord`, `FraudAlertRecord`, `EmailCommandRecord`, `MontoDinero`, `TxId`) | Todos | ✅ Hecho |
| `transaction-processor` | Rabbit → dominio rico (State/Strategy) → PostgreSQL → Kafka | **Miguel** | 🟡 Esqueleto |
| `fraud-detector` | Kafka stateful + Strategy → PostgreSQL → Kafka | **Miguel** | 🟡 Esqueleto |
| `transaction-api` | REST → RabbitMQ | Juanse | 🟡 Esqueleto |
| `dashboard-aggregator` | Kafka (2 topics) → vista materializada → WebSocket | Juanse | 🟡 Esqueleto |
| `notification-router` | Kafka → RabbitMQ | Andrés | 🟡 Esqueleto |
| `ad-generator` | Tarea programada → RabbitMQ | Andrés | 🟡 Esqueleto |
| `email-worker` | RabbitMQ consumer (servicio de **referencia**) | Andrés | ✅ Hecho |

Cada servicio sigue arquitectura hexagonal:

```
com.banco.ledger.<servicio>
├── domain/            entidades ricas, State, Strategy (Java puro, sin Spring)
├── application/       casos de uso (@Service) + port/ (interfaces de salida)
└── infrastructure/    adaptadores: Rabbit, Kafka, JPA, WebSocket
```

> **Patrón a copiar:** `email-worker` muestra el flujo completo
> *listener → caso de uso → puerto → adaptador* y su prueba con Mockito.

## Cómo ejecutar

Requisitos: JDK 17+, Maven 3.9+, Docker con Compose v2.

```bash
mvn test
```

```bash
docker compose up --build
```

```bash
curl -X POST localhost:8080/transfer -H 'Content-Type: application/json' -d '{"from_user":"A","to_user":"B","amount":50}'
```

- Dashboard: http://localhost:8081/dashboard.html
- Consola RabbitMQ: http://localhost:15672 (guest / guest)
- Ver el reparto entre réplicas: `docker compose logs -f email-worker`

## Convenciones

- JSON en `snake_case` (`tx_id`, `from_user`…), definido en los records de `common-contracts`.
- Nombres de colas/topics en `Topologia` — nunca strings sueltos.
- Inyección **solo por constructor**; nada de `@Autowired` en campos.
- Sin setters en el dominio; las entidades JPA viven separadas en `infrastructure/persistence`.
- Pruebas de dominio sin Spring (AAA); pruebas de servicio con `@ExtendWith(MockitoExtension.class)`.
