# Tasks: PROJ-002: The Reactive Store (ReactiveMart)

## Phase 1: Infrastructure & Python Setup
- [x] T001 Initialize `docker-compose.yml` with MongoDB.
- [x] T002 Implement `inventory-service` in Python (Flask).
- [x] T003 Setup SQLite in Python and create `requirements.txt`.
- [x] T004 Create `Dockerfile` for `inventory-service`.
- [x] T005 Verify `inventory-service` runs in Docker and responds on port 5000.

## Phase 2: Java Order Service Initialization
- [x] T006 Initialize `order-service` with WebFlux, R2DBC, and Mongo.
- [x] T007 Configure `order-service` to connect to Mongo in Docker.
- [x] T008 Implement `Dockerfile` for `order-service`.

## Phase 3: Polyglot Integration
- [x] T009 Implement `WebClient` calling `inventory-service:5000`.
- [x] T010 Implement `X-Correlation-ID` extraction/injection in Java.
- [x] T011 Implement `X-Correlation-ID` logging in Python.
- [x] T012 Verify cross-container tracing in logs.

## Phase 4: Reactive Logic, Resilience & Testing
- [x] T013 Implement `OrderService` pipeline (Mongo -> Python -> R2DBC).
- [x] T014 **Unit Testing**: Implement `StepVerifier` tests for the `OrderService` pipeline.
- [x] T015 **Unit Testing**: Implement `WebTestClient` tests for `OrderController`.
- [x] T016 Add 800ms timeout and fallback logic in Java + Test cases.
- [x] T017 Implement SSE endpoint for order status.

## Phase 5: Final Audit & Documentation
- [x] T018 Run full architecture via `docker-compose up`.
- [x] T019 Integrate **BlockHound** and verify Java service performance.
- [x] T020 Document "How to Run" in `README.md`.
- [x] T021 Final pedagogical audit.
