# Reactive Programming Lab Syllabus

## Level 1: Foundations (Project Reactor)
Focus: Shifting the mindset and mastering the core building blocks of reactive pipelines.

- [x] (AUDITED) **LAB-000: The Reactive Mindset & Foundational Analogies**
  - Concepts: Thinking in Streams, The Excel Analogy, The Delegation Matrix, I/O Blocking.
- [x] (AUDITED) **LAB-001: The Reactive Manifesto & Asynchronous Paradigms**
  - Concepts: Responsiveness, Resilience, Elasticity, Message Driven. Evolution: Callbacks -> Futures -> Streams.
- [x] (AUDITED) **LAB-002: The Reactive Streams Specification & TCK**
  - Concepts: Publisher, Subscriber, Subscription. Compliance with the TCK rules.
- [x] (AUDITED) **LAB-003: Flux & Mono Foundations**
  - Concepts: Cardinality (0..1 vs 0..N), Lazy Execution, Pipeline Immutability, StepVerifier basics.
- [x] (AUDITED) **LAB-004: Subscriptions & Lifecycle Control**
  - Concepts: `subscribe()`, `Disposable`, `BaseSubscriber`. Handling the stream lifecycle and cancellations.
- [x] (AUDITED) **LAB-005: Essential Transformation Operators**
  - Concepts: `map`, `filter`, `flatMap`, `concatMap`, `switchMap` foundations.
- [x] (AUDITED) **LAB-006: Combining & Basic Error Handling**
  - Concepts: `merge`, `concat`, `zip`. Basic `onErrorResume` and `retry`.

## Level 2: The Core Spec & Advanced Control
Focus: Deep dive into internal mechanics, flow control, and multi-threading.

- [x] (AUDITED) **LAB-007: Threading Models & Schedulers**
  - Concepts: `publishOn` vs `subscribeOn`. The Event Loop vs Parallel thread pools.
- [x] (AUDITED) **LAB-008: Programmatic Stream Generation**
  - Concepts: `Sinks.Many`, `Sinks.One`. Manual control with `Flux.create` vs `Flux.generate`.
- [ ] **LAB-009: Backpressure Strategies & Flow Control**
  - Concepts: `buffer`, `window`, `onBackpressureDrop`, `onBackpressureBuffer`.
- [ ] **LAB-010: Testing & Debugging Matrix**
  - Concepts: Advanced `StepVerifier`, `PublisherProbe`, `Hooks.onOperatorDebug`, Reactor Context.

## Level 3: Enterprise Reactive Services (Spring WebFlux)
Focus: Building non-blocking, high-performance REST APIs and microservices.

- [ ] **LAB-011: Reactive Web with Spring WebFlux**
  - Concepts: Annotated Controllers vs Functional Endpoints (`RouterFunction`), Server-Sent Events (SSE), Streaming JSON.
- [ ] **LAB-012: WebClient: Orchestrating Downstream Services**
  - Concepts: `retrieve()` vs `exchange()`, non-blocking HTTP calls, zip/flatmap for service orchestration.
- [ ] **LAB-013: Error Handling & Resilience in WebFlux**
  - Concepts: Global Error Handlers, `onErrorReturn`, `onErrorResume`, `retry`, `timeout` at the web layer.
- [ ] **LAB-014: Reactive Persistence with R2DBC**
  - Concepts: Non-blocking DB drivers, Connection Pooling in R2DBC, Spring Data R2DBC repositories.
- [ ] **LAB-015: Reactive Context & Tracing Propagation**
  - Concepts: `Reactor Context`, Propagating MDC (Logging) and Security state across reactive threads.

## Level 4: Resilient & Event-Driven Systems
Focus: Production-grade patterns, observability, and integrating with streaming platforms.

- [ ] **LAB-016: Advanced Resilience Patterns (Resilience4j)**
  - Concepts: Circuit Breakers, Bulkheads, Rate Limiters, and Time Limiters integrated with Reactor.
- [ ] **LAB-017: Observability & Tracing (Zipkin/Jaeger)**
  - Concepts: Micrometer Observation, Distributed Tracing, Span propagation in non-blocking pipelines.
- [ ] **LAB-018: Reactive Messaging with Apache Kafka**
  - Concepts: Reactor Kafka, non-blocking Producer/Consumer, backpressure-aware message processing.

## Level 5: The New Frontier & Final Project
Focus: Modern Java concurrency comparisons and consolidation in a real-world architecture.

- [ ] **LAB-019: Modern Concurrency: Virtual Threads vs Reactive Streams**
  - Concepts: Project Loom (Virtual Threads) vs Project Reactor. Performance benchmarks and decision matrix.
- [ ] **LAB-020: Alternative Frameworks: Quarkus Mutiny**
  - Concepts: The `Uni` and `Multi` pattern, Quarkus reactive ecosystem vs Spring WebFlux.
- [ ] **CAPSTONE PROJECT: Event-Driven Reactive Architecture**
  - Objective: Build a distributed system with WebFlux, R2DBC, and Kafka handling high throughput.

---
**Version**: 0.6.0 | **Author**: MeloStudy
