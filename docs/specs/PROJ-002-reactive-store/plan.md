# Implementation Plan: PROJ-002: The Reactive Store (ReactiveMart)

**Branch**: `proj-002-reactive-store` | **Date**: 2026-05-15
**Input**: Specification from `/docs/specs/PROJ-002-reactive-store/spec.md`

## Phase 1: Docker Infrastructure
1. Create `projects/002-reactive-store/docker-compose.yml`.
2. Define services: `mongodb`, `inventory-service`, `order-service`.
3. Create shared networks and volume mounts for SQLite files.

## Phase 2: Python Legacy Service (`inventory-service`)
1. Create `inventory-service` folder with `app.py` (Flask).
2. Implement stock/price endpoints with `sqlite3`.
3. Implement `X-Correlation-ID` logging in Flask.
4. Create `requirements.txt` and `Dockerfile`.

## Phase 3: Reactive Order Service (`order-service`)
1. Setup Spring WebFlux with R2DBC and MongoDB Reactive.
2. Implement `InventoryClient` (WebClient) with timeout/retry.
3. Implement `OrderService` pipeline.
4. Create `Dockerfile` (Multi-stage build).

## Phase 4: Distributed Tracing & Integration
1. Implement Java `WebFilter` for Correlation ID.
2. Update `WebClient` to forward headers to the Python service.
3. Verify connectivity between containers using Docker DNS (`http://inventory-service:5000`).

## Phase 5: Streaming & Resilience
1. Implement SSE endpoints.
2. Test "Slow Python" scenario: Verify that Java service falls back when Python exceeds 800ms.

## Phase 6: Final Verification
1. Run `docker-compose up`.
2. Execute test suite against the containerized environment.
3. Validate logs and non-blocking integrity.
