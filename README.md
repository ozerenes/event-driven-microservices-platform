# Event-Driven E-Commerce Platform

Monorepo for Order, Payment, and Notification microservices (Java/Spring Boot, Kafka, Saga choreography).

- **Architecture:** [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- **Event contracts:** [docs/EVENT-CONTRACTS.md](docs/EVENT-CONTRACTS.md)
- **Folder structure:** [docs/FOLDER-STRUCTURE.md](docs/FOLDER-STRUCTURE.md)

## Build (Maven)

From repo root:

```bash
mvn clean install
```

Build a single service (and its dependencies):

```bash
mvn -pl order-service -am package
```

## Run locally (Docker Compose)

From repo root:

```bash
docker compose -f docker/docker-compose.yml up -d
```

Build images first:

```bash
docker compose -f docker/docker-compose.yml build
```

## Modules

| Module | Description |
|--------|-------------|
| **event-contracts** | Shared event DTOs and envelope (no Spring/Kafka). |
| **order-service** | Order aggregate; REST API; publishes OrderCreated/OrderCancelled; consumes PaymentCompleted/PaymentFailed. |
| **payment-service** | Payment aggregate; consumes OrderCreated; publishes PaymentCompleted/PaymentFailed. |
| **notification-service** | Consumes order/payment events; sends email/SMS. |

Each service is independently deployable and has its own database.
