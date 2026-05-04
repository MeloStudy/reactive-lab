# Tasks: LAB-011: Reactive Web with Spring WebFlux

**Input**: Design documents from `/specs/011-reactive-web-webflux/`
**Prerequisites**: plan.md, spec.md.

## Phase 1: Project Scaffolding
- [x] T001 Create `labs/011-reactive-web-webflux` with Spring Boot 3.x structure.
- [x] T002 Configure `pom.xml` with `spring-boot-starter-webflux` and `reactor-test`.
- [x] T003 Register module in root `pom.xml`.

## Phase 2: Business Logic & Controllers
- [x] T004 Implement `StockService` simulating a reactive data source.
- [x] T005 Implement `StockController` (@RestController).
- [x] T006 Write `WebTestClient` tests for the annotated controller.
- [x] T007 Implement `StockRouter` and `StockHandler` (Functional).
- [x] T008 Write `WebTestClient` tests for the functional router.

## Phase 3: Streaming Endpoints
- [x] T009 Implement SSE Stock Ticker endpoint.
- [x] T010 Implement NDJSON Bulk Export endpoint.
- [x] T011 Write tests for streaming integrity and Content-Type validation.
- [x] T012 Implement `HandlerFilterFunction` and Global Exception Translator.
- [x] T013 Write tests for Filters (header presence) and Error Handling (status codes).

## Phase 4: Educational Content
- [x] T014 Write `CONCEPT.md` comparing Servlet vs Netty.
- [x] T015 Write `README.md` with curl examples for each scenario.
- [x] T016 Verify "Learner Journey" (clean startup and test pass).
