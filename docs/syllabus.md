# Reactive Programming Lab Syllabus

## Level 1: Foundations (Project Reactor)
Focus: Shifting the mindset and mastering the core building blocks of reactive pipelines.

- [x] (AUDITED) **LAB-000: The Reactive Mindset & Foundational Analogies**
  - Concepts: Thinking in Streams, The Excel Analogy, The Delegation Matrix, I/O Blocking.
- [x] (AUDITED) **LAB-001: The Reactive Manifesto & Asynchronous Paradigms**
  - Concepts: Responsiveness, Resilience, Elasticity, Message Driven. Evolution: Callbacks -> Futures -> Streams. Alternatives (RxJava, Mutiny). Architecture: Tomcat vs Netty.
- [x] (AUDITED) **LAB-002: The Reactive Streams Specification & TCK**
  - Concepts: Publisher, Subscriber, Subscription. Compliance with the TCK rules.
- [x] (AUDITED) **LAB-003: Flux & Mono Foundations**
  - Concepts: Cardinality (0..1 vs 0..N), Lazy Execution, Pipeline Immutability, StepVerifier basics.
- [x] (AUDITED) **LAB-004: Subscriptions, Lifecycle & Side Effects**
  - Concepts: `subscribe()`, `Disposable`, `BaseSubscriber`. Handling the stream lifecycle and cancellations. **Side Effects (Signal Peekers)**: `doOnNext`, `doOnError`, `doOnSubscribe`, `doFinally`.
- [x] (AUDITED) **LAB-005: Essential Transformation & Filtering**
  - Concepts: `map`, `filter`, `flatMap`, `concatMap`, `switchMap`. **Filtering & Slice**: `take`, `skip`, `distinct`. Flux-to-Mono basics: `collectList`.
- [x] (AUDITED) **LAB-006: Combining & Aggregation Operators**
  - Concepts: `merge`, `concat`, `zip`. **Accumulators**: `buffer`, `window`, `scan`, `reduce`. **Collections**: `collectList`, `collectMap`, `collectSortedList`. Basic Error Handling.
- [ ] **MINI-PROJECT-1: The Reactive Data Processor**
  - Objective: Processing a massive dataset from a file using foundational operators and error handling.

## Level 2: The Core Spec & Advanced Control
Focus: Deep dive into internal mechanics, flow control, and multi-threading.

- [x] (AUDITED) **LAB-007: Threading Models & Schedulers**
  - Concepts: `publishOn` vs `subscribeOn`. The Event Loop vs Parallel thread pools.
- [x] (AUDITED) **LAB-008: Programmatic Stream Generation & Hot/Cold**
  - Concepts: `Sinks.Many`, `Sinks.One`. Manual control with `Flux.create` vs `Flux.generate`. **Hot vs Cold Publishers**: `ConnectableFlux`, `publish`, `refCount`, `autoConnect`.
- [x] (AUDITED) **LAB-009: Backpressure Strategies & Rate Limiting**
  - Concepts: `buffer`, `window`, `onBackpressureDrop`, `onBackpressureBuffer`. **Rate Limiting**: `limitRate`, `limitRequest`.
- [x] (AUDITED) **LAB-010: Testing & Debugging Matrix**
  - Concepts: Advanced `StepVerifier`, `withVirtualTime`, `PublisherProbe`, `Hooks.onOperatorDebug`, Reactor Context, **BlockHound**.

## Level 3: Enterprise Reactive Services (Spring WebFlux)
Focus: Building non-blocking, high-performance REST APIs and microservices.

- [x] (AUDITED) **LAB-011: Reactive Web with Spring WebFlux**
  - Concepts: Annotated Controllers vs Functional Endpoints (`RouterFunction`), Server-Sent Events (SSE), Streaming JSON.
- [x] (AUDITED) **LAB-012: WebClient: Orchestrating Downstream Services**
  - Concepts: `retrieve()` vs `exchange()`, non-blocking HTTP calls, zip/flatmap for service orchestration.
- [x] (AUDITED) **LAB-013: Error Handling & Resilience in WebFlux**
  - Concepts: Global Error Handlers, `onErrorReturn`, `onErrorResume`, `retry`, `retryWhen` (Exponential Backoff), `timeout` at the web layer.
- [x] (AUDITED) **LAB-014: Reactive Persistence with R2DBC**
  - Concepts: Non-blocking DB drivers, Connection Pooling in R2DBC, Spring Data R2DBC repositories.
- [x] (AUDITED) **LAB-015: Reactive Persistence with MongoDB**
  - Concepts: Reactive Streams with NoSQL, Tailable Cursors, Change Streams, GridFS.
- [x] (AUDITED) **LAB-016: Reactive Context & Tracing Propagation**
  - Concepts: `Reactor Context`, Propagating MDC (Logging) and context state across reactive threads.
- [x] (AUDITED) **LAB-017: Reactive Security & Identity (OAuth2/JWT)**
  - Concepts: `SecurityWebFilterChain`, `ReactiveSecurityContextHolder`, Non-blocking Authentication & Authorization.
- [ ] **LAB-017-B: Reactive Contract Testing**
  - Concepts: Spring Cloud Contract (Reactive), Verifying WebClient interactions.
  - Type: Deep Dive Alternative
- [x] (AUDITED) **MINI-PROJECT-2: The Polyglot Reactive Store**
  - Objective: A distributed architecture orchestrating a **Spring WebFlux (Java)** order service with a **Legacy Flask (Python)** inventory.
  - Concepts: Mixed Persistence (**MongoDB + R2DBC/SQLite + SQLite**), Distributed Tracing (**Reactor Context** propagation), Resilience (**WebClient Timeouts/Fallbacks**), Non-blocking Validation (**BlockHound**), and Full CRUD Lifecycle.

## Level 4: Resilient & Event-Driven Systems
Focus: Production-grade patterns, observability, and integrating with streaming platforms.

- [x] (AUDITED) **LAB-018: Advanced Resilience Patterns (Resilience4j)**
  - Concepts: Circuit Breakers, Bulkheads, Rate Limiters, and Time Limiters integrated with Reactor.
- [x] (AUDITED) **LAB-019: Modern Observability (Micrometer Observation)**
  - Concepts: `ObservationRegistry`, `ObservationHandler`, Automatic instrumentation (WebFlux/WebClient), and Tagging strategies (High/Low cardinality).
- [x] (AUDITED) **LAB-020: Reactive Messaging with Apache Kafka**
  - Concepts: Reactor Kafka, non-blocking Producer/Consumer, backpressure-aware message processing.
- [ ] **LAB-021: Reactive Messaging with RabbitMQ**
  - Concepts: Reactor RabbitMQ, AMQP vs Kafka in reactive flows.
  - Type: Deep Dive Alternative

## Level 5: The New Frontier & Final Project
Focus: Modern Java concurrency comparisons and consolidation in a real-world architecture.

- [ ] **LAB-022: The Modern Decision Matrix: Virtual Threads vs Reactor**
  - Concepts: Project Loom (Virtual Threads) vs Project Reactor. Performance benchmarks and decision matrix.
- [ ] **LAB-023: Alternative Frameworks: Quarkus Mutiny**
  - Concepts: The `Uni` and `Multi` pattern, Quarkus reactive ecosystem vs Spring WebFlux.
  - Type: Deep Dive Alternative
- [ ] **CAPSTONE PROJECT: Event-Driven Reactive Architecture**
  - Objective: Build a distributed system with WebFlux, R2DBC, and Kafka handling high throughput.

---
**Version**: 0.7.6 | **Author**: MeloStudy
