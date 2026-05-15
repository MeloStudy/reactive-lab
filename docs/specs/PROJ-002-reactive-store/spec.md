# Project Specification: PROJ-002: The Reactive Store (ReactiveMart)

**Feature Branch**: `proj-002-reactive-store`
**Created**: 2026-05-15
**Status**: DRAFT
**Syllabus Section**: Mini-Project 2 (Integration of Level 3 - Enterprise Reactive Services)

## Project Alignment *(mandatory)*

- **Concept**: Polyglot microservice orchestration involving Reactive (Java) and Blocking (Python) stacks, containerized via Docker Compose.
- **Prerequisites**: LAB-011 through LAB-017.
- **Goal**: Build a resilient, containerized hybrid architecture that demonstrates distributed tracing and cross-framework integration.

## Architecture & Components

### 1. `order-service` (Modern Reactive)
- **Stack**: Java 21, Spring WebFlux, Project Reactor.
- **DB 1 (Relational)**: **SQLite** via **R2DBC**.
- **DB 2 (NoSQL)**: **MongoDB** (Reactive Driver).
- **Role**: High-performance orchestrator.
- **Container**: `Dockerfile` (OpenJDK 21 Alpine).

### 2. `inventory-service` (Legacy Python)
- **Stack**: Python 3.12, **Flask** (Blocking).
- **DB**: **SQLite** via `sqlite3`.
- **Role**: Simple stock/pricing provider. Intentionally uses `time.sleep()` to simulate legacy latency.
- **Container**: `Dockerfile` (Python 3.12 Alpine).

### 3. Infrastructure
- **Orchestration**: **Docker Compose** managing all services and MongoDB.
- **Persistence**: 
  - MongoDB container.
  - Persistent volumes for SQLite files to allow cross-restart data retention.

## Business Requirements *(mandatory)*

### Functional Requirements
1.  **Polyglot Orchestration**: `order-service` fetches metadata from Mongo and stock from the Python `inventory-service`.
2.  **Order Flow**:
    - `POST /orders` -> `order-service`.
    - `order-service` -> `inventory-service:5000/stock/{id}`.
    - `order-service` -> Save to SQLite & Mongo.
3.  **Real-time SSE**: Stream order status updates.
4.  **Distributed Tracing**: Preserve `X-Correlation-ID` from Java to Python.

## Technical Requirements *(mandatory)*

- **TR-001**: **Containerization**: Both microservices and MongoDB MUST run via `docker-compose up`.
- **TR-002**: **Reactive Networking**: `order-service` must use `WebClient` for all external communication.
- **TR-003**: **Tracing Propagation**: Headers must be propagated from WebFlux (Context) to Flask (Requests context).
- **TR-004**: **Resilience**: 
    - Java side: 800ms timeout for Python calls.
    - Python side: Randomized latency between 100ms and 2s to trigger fallback logic.

## Success Criteria
- **SC-001**: Full architecture starts with a single `docker-compose up`.
- **SC-002**: Successful order processing with logs showing the same Trace ID in both Java and Python containers.
- **SC-003**: `order-service` handles Python timeouts gracefully without crashing.
- **SC-004**: Zero blocking calls in the Java service.
