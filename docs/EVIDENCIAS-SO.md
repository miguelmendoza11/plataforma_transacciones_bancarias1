# Evidencias de Sistemas Operativos

Comandos para sacar los pantallazos de SO y qué debe verse en cada uno.
Antes: `docker compose up -d --build` y esperar a que todo esté arriba. Para tener correos en los logs,
correr también `bash scripts/demo.sh` (en Windows, con Git Bash).

## 1. Contenedores corriendo

```bash
docker compose ps
```

Debe verse:
- 13 contenedores: `postgres-db`, `zookeeper`, `kafka`, `rabbitmq`, los 6 servicios Spring y
  **3 réplicas** `email-worker-1`, `email-worker-2`, `email-worker-3` (`deploy.replicas: 3`).
- `postgres-db`, `kafka` y `rabbitmq` en `Up (healthy)`: tienen healthcheck y los demás esperan por
  ellos con `depends_on: condition: service_healthy`.
- Los servicios Spring en `Up`. Si alguno arrancó antes de que Kafka/RabbitMQ aceptaran conexiones,
  `restart: on-failure` lo vuelve a levantar.

## 2. Límites de CPU y RAM (cgroups)

```bash
docker stats --no-stream
```

Debe verse en la columna `MEM USAGE / LIMIT` el límite de `deploy.resources.limits.memory`, no la RAM
total del equipo:

| Servicio | Límite de memoria | Límite de CPU |
|---|---|---|
| transaction-api, notification-router, ad-generator, email-worker (×3) | 384MiB | 0.50 |
| fraud-detector, dashboard-aggregator | 512MiB | 0.75 |
| transaction-processor | 768MiB | 1.0 |
| postgres-db / zookeeper / rabbitmq / kafka | 512MiB / 512MiB / 512MiB / 768MiB | 1.0 / 0.50 / 0.75 / 1.0 |

`docker stats` no muestra el límite de CPU (la columna `CPU %` es el uso). Se ve dentro del cgroup:

```bash
docker compose exec email-worker cat /sys/fs/cgroup/cpu.max
```

→ `50000 100000`: el contenedor puede usar 50 ms de CPU cada 100 ms, o sea medio núcleo (`cpus: '0.50'`).

## 3. Reparto de correos entre las 3 réplicas (Work Queue)

```bash
docker compose logs email-worker
```

Debe verse `ENVIANDO EMAIL a [...]: [...] (worker=<hostname>)` con **tres hostnames distintos**
(el hostname de cada contenedor es su id corto) y prefijos `email-worker-1 |`, `-2 |`, `-3 |`:
- El correo de la transferencia: `ENVIANDO EMAIL a [user_a@email.com]: [Envió $50.00 a B]`.
- Los anuncios de ad-generator cada 30 s: `ENVIANDO EMAIL a [all_users@banco.com]: [¡Obtenga nuestra nueva Tarjeta de Crédito!]`, ...

Cada mensaje sale en UNA sola réplica: compiten por `email_queue` y con `prefetch: 1` (fair dispatch)
RabbitMQ no le entrega otro mensaje a un worker hasta que confirme el anterior.

Para verlo en vivo: `docker compose logs -f email-worker`.

## 4. Usuario no privilegiado

```bash
docker compose exec email-worker whoami
```

→ `appuser`. El Dockerfile crea el usuario con `adduser -S appuser` y hace `USER appuser`, así que la JVM
no corre como `root` dentro del contenedor. Con réplicas, `exec` entra a la 1; para otra:
`docker compose exec --index 2 email-worker whoami`.

## 5. Límite de memoria del cgroup

```bash
docker compose exec email-worker cat /sys/fs/cgroup/memory.max
```

→ `402653184` (= 384 × 1024 × 1024 bytes = 384MiB). Es el mismo límite de `docker stats`, pero leído
desde el propio kernel (cgroup v2). La JVM lo lee al arrancar gracias a `-XX:+UseContainerSupport` y,
con `-XX:MaxRAMPercentage=75.0`, deja el heap en ~288MiB en vez de calcularlo con la RAM del equipo.

## 6. Prueba de OOM con `ExitOnOutOfMemoryError`

```bash
docker compose run --rm -e JAVA_OPTS="-Xmx8m -XX:+ExitOnOutOfMemoryError" email-worker
```

Con solo 8 MB de heap Spring Boot no alcanza a arrancar (con 12 MB o más sí arranca, lento, y se
queda corriendo como una réplica más; si pasa eso, se para con Ctrl + C). Debe verse:

```
Terminating due to java.lang.OutOfMemoryError: Java heap space
```

y el comando termina con **código 3**. Para ver el código:
- PowerShell: `$LASTEXITCODE`
- Git Bash: `echo $?`

Sin `ExitOnOutOfMemoryError` la JVM podría quedarse viva pero inservible (hilos muertos, sin consumir la
cola). Con el flag sale de una vez y, en los servicios levantados con `up`, `restart: on-failure` los
relanza. (`docker compose run` ignora la política de reinicio, por eso aquí se ve el código de salida.)
