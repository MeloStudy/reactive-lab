# Reactive Lab Constitution

## Core Principles

### I. Asynchronous & Non-Blocking First
The laboratory is built on the foundation of the Reactive Manifesto. Every lab MUST prioritize non-blocking I/O and asynchronous data streams. Concepts like Thread-per-Request vs. Event-Loop MUST be clearly explained to justify the reactive approach.

### II. The Hybrid Path: RxJS for Concepts, Reactor for Enterprise
To ensure a broad understanding, we use **JavaScript (RxJS)** for initial modules to master fundamental stream operators due to its visual and low-boilerplate nature. However, the core of the professional curriculum focuses on the **Java Ecosystem (Project Reactor, Spring WebFlux)** as the primary enterprise framework.

### III. Reproducible & Infrastructure-Aware
Reactive systems often depend on specific infrastructure (Message Brokers like Kafka, Reactive Databases like MongoDB/PostgreSQL). All external dependencies MUST be containerized via **Docker Compose**. Sizing and resource constraints (CPU/Memory) should be explored to see their impact on backpressure and throughput.

### IV. Educational Clarity & Theoretical Foundation
Theoretical depth is MANDATORY. Documenting operators is not enough; each lab must explain the internal mechanics (e.g., how Schedulers manage thread context, how `onNext/onError/onComplete` signals propagate). Theory MUST reside in `CONCEPT.md`, while practice stays in `README.md`.

### V. Backpressure & Resilience by Design
Reactive programming without backpressure is just asynchronous callback hell. Every advanced lab MUST include a scenario where flow control is tested (e.g., slow consumers vs. fast producers). Resilience patterns (Retry, Timeout, Fallback, Circuit Breaker) are core requirements.

### VI. The Modern Context (Virtual Threads)
With Java 21+ as the baseline, the curriculum MUST address Project Loom (Virtual Threads). The trade-offs between Virtual Threads and Reactive Streams must be explicitly compared to provide a complete, modern perspective.

## Lab Design Standards

- **Spec Planning**: Mandatory `spec.md`, `plan.md`, and `tasks.md` before coding.
- **Naming Convention**: `XXX-slug-name` (e.g., `001-flux-foundations`).
- **Native Execution**: Guide students to run `mvn test` or `npm test` directly in the README.
- **Backpressure Scenarios**: Labs involving data streams MUST explain how backpressure is handled.
- **Testing Standard**: Java labs MUST use `StepVerifier` for validating reactive sequences. JS labs MUST use `jest` with marble testing or async/await validation.
- **Git Hygiene**: A global `.gitignore` MUST be maintained at the root. Individual labs SHOULD NOT have local `.gitignore` files unless they have unique, non-standard dependencies.


## Technical Stack & Standards

- **Java**: JDK 21+ (Project Reactor, Spring WebFlux, Virtual Threads).
- **Node.js**: v20+ (RxJS).
- **Tooling**: Docker, `curl`, `httpie`, Kafka.
- **Language**: English.

## Lab Lifecycle & Statuses

- **`DRAFT`**: Initial spec in progress.
- **`PLANNED`**: Artifacts created, pending audit.
- **`READY`**: Ready for coding.
- **`IMPLEMENTED`**: Code and docs complete.
- **`AUDITED`**: Pedagogical audit passed.

**Version**: 0.1.1 | **Ratified**: 2026-04-30
