# Reactive Lab Constitution

## Core Principles

### I. Asynchronous & Non-Blocking First
The laboratory is built on the foundation of the Reactive Manifesto. Every lab MUST prioritize non-blocking I/O and asynchronous data streams. Concepts like Thread-per-Request vs. Event-Loop MUST be clearly explained to justify the reactive approach.

### II. The Java-First Path: Project Reactor & Spring WebFlux
To ensure maximum engineering rigor and professional relevance, the laboratory focuses exclusively on the **Java Ecosystem**. We use **Project Reactor** as the core reactive library and **Spring WebFlux** for enterprise integration. This eliminates context-switching and allows deep-dives into JVM internals (Threads, Memory, Schedulers) from the very first module.

### III. Reproducible & Infrastructure-Aware
Reactive systems often depend on specific infrastructure (Message Brokers like Kafka, Reactive Databases like MongoDB/PostgreSQL). All external dependencies MUST be containerized via **Docker Compose** or **Podman Compose**. Sizing and resource constraints (CPU/Memory) should be explored to see their impact on backpressure and throughput.

### IV. Educational Clarity & Theoretical Foundation
Theoretical depth is MANDATORY. Documenting operators is not enough; each lab must explain the internal mechanics (e.g., how Schedulers manage thread context, how `onNext/onError/onComplete` signals propagate). Theory MUST reside in `CONCEPT.md`, while practice stays in `README.md`.

### V. Backpressure & Resilience by Design
Reactive programming without backpressure is just asynchronous callback hell. Every advanced lab MUST include a scenario where flow control is tested (e.g., slow consumers vs. fast producers). Resilience patterns (Retry, Timeout, Fallback, Circuit Breaker) are core requirements.

### VI. Modern Technology Assimilation & Evolution
As the JVM evolves (Java 21+), the laboratory SHOULD evaluate how reactive programming integrates with or is challenged by new platform features. This includes assessing the impact of **Virtual Threads (Project Loom)**, **Structured Concurrency**, and **Scoped Values** on the reactive paradigm. The objective is to understand how to assimilate these technologies to build superior systems, ensuring the curriculum remains at the engineering frontier.



## Lab Design Standards

- **Spec Planning**: Mandatory `spec.md`, `plan.md`, and `tasks.md` before coding. These artifacts MUST reside in `docs/specs/{lab-slug}/` to keep the `labs/` directory focused on code and student-facing documentation.
- **Naming Convention**: `XXX-slug-name` (e.g., `001-flux-foundations`).
- **Native Execution**: Guide students to run `mvn test` directly in the README.
- **Interactive Self-Assessment**: Every laboratory README MUST include a "Self-Assessment" or "Knowledge Check" section using collapsible `<details>` blocks to provide immediate pedagogical feedback.
- **Backpressure Scenarios**: Labs involving data streams MUST explain how backpressure is handled.
- **Testing Standard**: Labs MUST use **StepVerifier** (Project Reactor) for validating reactive sequences and signal timing. Following JUnit 5 and SonarQube best practices:
    - Test classes and methods MUST NOT use the `public` access modifier.
    - **AssertJ** assertions MUST use dedicated methods for better readability and error messages (e.g., use `assertThat(map).containsEntry(key, value)` instead of `assertThat(map.get(key)).isEqualTo(value)`).
- **Logging Standard**: Direct use of `System.out.println` or `System.err.println` is PROHIBITED. All logging MUST use **SLF4J** via the **Lombok `@Slf4j`** annotation to reduce boilerplate. In reactive pipelines, the `.log()` operator SHOULD be used for debugging stream signals.
- **Traceable Implementation**: Every scenario described in the laboratory `README.md` MUST include relative links (from the project root) to the corresponding Java implementation files and their unit tests. This ensures students can easily navigate between theory and code across different environments.
- **Git Hygiene**: A global `.gitignore` MUST be maintained at the root. Individual labs SHOULD NOT have local `.gitignore` files unless they have unique, non-standard dependencies.
- **Infrastructure Parity**: Labs requiring external infrastructure MUST provide alternative instructions or aliases for **Podman** users (e.g., using `podman-compose` or `alias docker=podman`). This ensures the laboratory remains accessible to students in diverse container environments.


## Technical Stack & Standards

- **Java**: JDK 21+ (Project Reactor, Spring WebFlux, Virtual Threads).
- **Tooling**: Maven 3.9+, Docker / Podman, `curl`, `httpie`, Kafka.
- **Libraries**: Lombok (for boilerplate reduction).
- **Language**: English.

## VII. Dependency & Build Governance

To ensure security, consistency, and maintainability across the laboratory ecosystem, the following build standards are MANDATORY:

- **Inheritance Hierarchy**: Every lab module (located in `labs/`) MUST inherit from the root `pom.xml` using the `<parent>` tag.
- **Centralized Dependency Management**: All common dependency versions MUST be defined in the root parent POM's `<properties>` and managed via `<dependencyManagement>`.
- **Shadowing Prohibited**: Lab modules MUST NOT define explicit versions for dependencies that are already managed in the parent POM. Overriding parent versions is strictly prohibited unless specifically justified for experimental purposes.
- **Security Baseline**: All libraries MUST be checked for known vulnerabilities (CVEs). Dependencies MUST be kept at stable, patched versions in the parent POM (e.g. Logback 1.5.32+ to address CVE-2025-11226, Jackson 2.18.7+ to address GHSA-72hv-8253-57qq).
- **Mini-Project Isolation**: Standalone projects (located in `projects/`) MAY maintain independent POMs to simulate real-world isolation, but they MUST still adhere to the project's security and version standards (e.g. using patched versions of Jackson and Logback).

## Lab Lifecycle & Statuses

- **`DRAFT`**: Initial spec in progress.
- **`PLANNED`**: Artifacts created, pending audit.
- **`READY`**: Ready for coding.
- **`IMPLEMENTED`**: Code and docs complete.
- **`AUDITED`**: Pedagogical audit passed.

**Version**: 0.2.8 | **Ratified**: 2026-05-15
