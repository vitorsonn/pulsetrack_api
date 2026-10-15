# PulseTrack

API de rastreamento de entregas em tempo real com Spring Boot, Redis Streams,
PostgreSQL e Server-Sent Events (SSE).

## Stack

- Java 21 e Spring Boot 4
- PostgreSQL 16
- Redis 7
- Docker Compose

## Deploy com Docker

1. Copie `.env.example` para `.env`.
2. Defina uma senha forte em `POSTGRES_PASSWORD`.
3. Em produção, defina `CORS_ALLOWED_ORIGINS` com a origem pública do frontend.
4. Suba a aplicação:

```bash
docker compose up -d --build
```

A API ficará disponível na porta `8080` por padrão. O endpoint para probes de
disponibilidade é `GET /actuator/health`.

Para acompanhar os logs:

```bash
docker compose logs -f api
```

Para encerrar os containers sem remover os dados:

```bash
docker compose down
```

## Execução local

Com Java 21 e Docker instalados:

```bash
docker compose up -d postgres redis
./mvnw spring-boot:run
```

No Windows, use `mvnw.cmd spring-boot:run`.

## Endpoints principais

- `POST /orders` — cria um pedido
- `GET /orders/{id}` — consulta um pedido
- `POST /deliveries/location` — ingere uma coordenada
- `GET /tracking/{orderId}/current` — consulta a posição atual
- `GET /tracking/stream/{orderId}` — abre o stream SSE
- `GET /actuator/health` — health check da aplicação

## Simulador

O simulador opcional está em
`src/main/java/com/api/pulsetrack/shared/scripts/simulate_delivery.py`.

```bash
pip install -r src/main/java/com/api/pulsetrack/shared/scripts/requirements.txt
python src/main/java/com/api/pulsetrack/shared/scripts/simulate_delivery.py
```
