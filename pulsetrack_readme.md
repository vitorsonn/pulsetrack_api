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

### Variáveis em um provedor de deploy

Se a API for publicada separadamente do `docker compose`, configure no serviço
da aplicação as variáveis abaixo apontando para os serviços gerenciados pelo
provedor. Não use `localhost` para PostgreSQL ou Redis em produção:

```text
POSTGRES_HOST=<host interno do PostgreSQL>
POSTGRES_PORT=5432
POSTGRES_SSLMODE=require
POSTGRES_DB=<nome do banco>
POSTGRES_USER=<usuário do banco>
POSTGRES_PASSWORD=<senha do banco>
REDIS_HOST=<host interno do Redis>
REDIS_PORT=6379
CORS_ALLOWED_ORIGINS=https://<domínio-do-frontend>
```

No Docker Compose, os hosts são `postgres` e `redis` e já são configurados
automaticamente. O erro `Unable to determine Dialect` do Hibernate é
secundário: ele aparece quando a conexão PostgreSQL falha.

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
