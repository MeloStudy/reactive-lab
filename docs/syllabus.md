# Reactive Programming Lab Syllabus

## Level 1: The Reactive Mindset & Foundations (Node.js & RxJS)
Focus: Understanding the 'Why', The Reactive Manifesto, and the Observer Pattern without JVM boilerplate.

- [x] (AUDITED) **LAB-001: The Reactive Manifesto & Asynchronous Paradigms**
  - Concepts: Responsiveness, Resilience, Elasticity, Message Driven. Callbacks vs Promises vs Observables.
- [ ] **LAB-002: Stream Creation & Subscription Lifecycle**
  - Concepts: `of`, `from`, `interval`, `timer`. Cold vs Hot Observables (Intro).
- [ ] **LAB-003: Subjects & Multicasting: State in Streams**
  - Concepts: `Subject`, `BehaviorSubject`, `ReplaySubject`. Multicasting with `shareReplay`.
- [ ] **LAB-004: Essential Operators: Transformation & Filtering**
  - Concepts: `map`, `filter`, `scan`, `reduce`, `take`, `distinctUntilChanged`.
- [ ] **LAB-005: Combining Streams & Flattening Strategies**
  - Concepts: `merge`, `concat`, `zip`. Higher-order Observables: `switchMap`, `mergeMap`, `concatMap`.

## Level 2: The JVM Reactive Core (Java 21 & Project Reactor)
Focus: Transitioning to Java, strong typing, and mastering the Reactive Streams Specification (Publisher/Subscriber).

- [x] (AUDITED) **LAB-006: The Reactive Streams Specification & TCK** (Completed)
  - Concepts: Publisher, Subscriber, Subscription. The TCK (Technology Compatibility Kit).
- [x] (AUDITED) **LAB-007: Flux & Mono Foundations** (Completed)
  - Concepts: Lazy execution, immutable pipelines, factory methods.
- [ ] **LAB-008: Bridging Imperative & Reactive: Sinks & Emitters**
  - Concepts: `Sinks.Many`, `Sinks.One`. Manual stream control with `Flux.create`.
- [ ] **LAB-009: Threading Models & Schedulers**
  - Concepts: `publishOn` vs `subscribeOn`. Parallel processing with `parallel()`.
- [ ] **LAB-010: Backpressure Strategies & Flow Control**
  - Concepts: `buffer`, `window`, `onBackpressureDrop`, `onBackpressureBuffer`.
- [ ] **MINI-PROJECT 1: Reactive Local File Processor**
  - Objective: Read, transform, and write large files asynchronously without OutOfMemory errors using Project Reactor.

## Level 3: Enterprise Reactive Services (Spring WebFlux)
Focus: Building non-blocking, high-performance REST APIs and microservices.

- [ ] **LAB-011: Reactive Web with Spring WebFlux**
  - Concepts: Annotated Controllers vs Functional Endpoints (`RouterFunction`).
- [ ] **LAB-012: WebClient: Orchestrating Downstream Services**
  - Concepts: `retrieve()` vs `exchange()`. Combining multiple service calls non-blockingly.
- [ ] **LAB-013: Error Handling & Resilience in WebFlux**
  - Concepts: Global Error Handlers, `onErrorReturn`, `onErrorResume`, `retry`, `timeout`.
- [ ] **LAB-014: Reactive Persistence with R2DBC**
  - Concepts: Non-blocking DB drivers. Connection pooling and Spring Data R2DBC.
- [ ] **LAB-015: Reactive Context & Tracing Propagation**
  - Concepts: `Reactor Context`. Propagating MDC and Security state in reactive pipelines.
- [ ] **MINI-PROJECT 2: Reactive CRUD Microservice**
  - Objective: Build a complete Spring WebFlux microservice with R2DBC and secured endpoints.

## Level 4: Resilient & Event-Driven Systems
Focus: Production-grade patterns, observability, and integrating with streaming platforms.

- [ ] **LAB-016: The Testing Matrix: Unit & Integration**
  - Concepts: `StepVerifier` deep-dive, `PublisherProbe`, and `WebTestClient`.
- [ ] **LAB-017: Observability & Debugging Reactive Streams**
  - Concepts: Micrometer Observation, Tracing (Zipkin/Jaeger), `Hooks.onOperatorDebug`.
- [ ] **LAB-018: Advanced Resilience Patterns**
  - Concepts: Resilience4j Circuit Breakers, Bulkheads, and Rate Limiters with Spring WebFlux.
- [ ] **LAB-019: Reactive Messaging with Apache Kafka**
  - Concepts: Reactor Kafka. Consuming and producing event streams non-blockingly.

## Level 5: The New Frontier & Extras
Focus: Modern Java concurrency comparisons and alternative reactive frameworks.

- [ ] **LAB-020: Modern Concurrency: Virtual Threads vs Reactive Streams**
  - Concepts: Project Loom vs Project Reactor. Performance benchmarks and use-cases.
- [ ] **LAB-021: Alternative Frameworks: Quarkus Mutiny (Extra)**
  - Concepts: `Uni` & `Multi`. Quarkus reactive ecosystem.
- [ ] **CAPSTONE PROJECT: Event-Driven Reactive Architecture**
  - Objective: Orchestrate multiple microservices using Spring WebFlux, R2DBC, and Kafka.

---
**Version**: 0.3.0 | **Author**: Reactive Lab Engineer
