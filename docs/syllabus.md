# Reactive Programming Lab Syllabus

## Level 1: The Reactive Mindset & Foundations (Node.js & RxJS)
Focus: Understanding the 'Why', The Reactive Manifesto, and the Observer Pattern without JVM boilerplate.

- [ ] **LAB-001: The Reactive Manifesto & Asynchronous Paradigms**
  - Concepts: Responsiveness, Resilience, Elasticity, Message Driven. Callbacks vs Promises vs Observables.
- [ ] **LAB-002: Stream Creation & Subscription Lifecycle**
  - Concepts: `of`, `from`, `interval`. Cold vs Hot Observables (Basic).
- [ ] **LAB-003: Essential Operators: Transformation & Filtering**
  - Concepts: `map`, `filter`, `scan`, `reduce`.
- [ ] **LAB-004: Combining Streams & Flattening**
  - Concepts: `merge`, `concat`, `zip`, `switchMap`, `mergeMap`.

## Level 2: The JVM Reactive Core (Java 21 & Project Reactor)
Focus: Transitioning to Java, strong typing, and mastering the Reactive Streams Specification (Publisher/Subscriber).

- [ ] **LAB-005: The Reactive Streams Specification & TCK**
  - Concepts: Publisher, Subscriber, Subscription. Push vs Pull.
- [ ] **LAB-006: Flux & Mono Foundations**
  - Concepts: Factory methods. Lazy execution. `StepVerifier` basics.
- [ ] **LAB-007: Threading Models & Schedulers**
  - Concepts: `publishOn` vs `subscribeOn`. The Event-Loop in the JVM.
- [ ] **LAB-008: Backpressure Strategies**
  - Concepts: `buffer`, `window`, `onBackpressureDrop`, `onBackpressureBuffer`.
- [ ] **MINI-PROJECT 1: Reactive Local File Processor**
  - Objective: Read, transform, and write large files asynchronously without OutOfMemory errors using Project Reactor.

## Level 3: Enterprise Reactive Services (Spring WebFlux)
Focus: Building non-blocking, high-performance REST APIs and microservices.

- [ ] **LAB-009: Reactive Web with Spring WebFlux**
  - Concepts: Annotated Controllers vs Functional Endpoints (`RouterFunction`).
- [ ] **LAB-010: Error Handling & Resilience in WebFlux**
  - Concepts: Global Error Handlers, `onErrorReturn`, `onErrorResume`, `retry`.
- [ ] **LAB-011: Reactive Persistence with R2DBC**
  - Concepts: Database connections without blocking. Spring Data R2DBC repositories.
- [ ] **LAB-012: Reactive Context & Security**
  - Concepts: Propagating state in reactive pipelines (`Reactor Context`). Spring Security Reactive.
- [ ] **MINI-PROJECT 2: Reactive CRUD Microservice**
  - Objective: Build a complete Spring WebFlux microservice with R2DBC, secured endpoints, and comprehensive `StepVerifier` testing.

## Level 4: Resilient & Event-Driven Systems
Focus: Production-grade patterns, observability, and integrating with streaming platforms.

- [ ] **LAB-013: Observability & Debugging Reactive Streams**
  - Concepts: Micrometer Observation, Tracing (Zipkin/Jaeger), `Hooks.onOperatorDebug`, `reactor-tools`.
- [ ] **LAB-014: Advanced Resilience Patterns**
  - Concepts: Resilience4j Circuit Breakers, Bulkheads, and Rate Limiters with Spring WebFlux.
- [ ] **LAB-015: Reactive Messaging with Apache Kafka**
  - Concepts: Reactor Kafka. Consuming and producing event streams non-blockingly.

## Level 5: The New Frontier & Extras
Focus: Modern Java concurrency comparisons and alternative reactive frameworks.

- [ ] **LAB-016: Modern Concurrency: Virtual Threads vs Reactive Streams**
  - Concepts: Project Loom. Spring MVC with Virtual Threads vs Spring WebFlux. When to use which?
- [ ] **LAB-017: Alternative Frameworks: Quarkus Mutiny (Extra)**
  - Concepts: `Uni` & `Multi`. Quarkus reactive extensions.
- [ ] **CAPSTONE PROJECT: Event-Driven Reactive Architecture**
  - Objective: Orchestrate multiple microservices using Spring WebFlux, R2DBC, and Kafka. Implement circuit breakers and end-to-end tracing.

---
**Version**: 0.2.0 | **Author**: Reactive Lab Engineer
