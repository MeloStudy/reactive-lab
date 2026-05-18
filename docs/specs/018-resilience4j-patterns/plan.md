# Implementation Plan: LAB-018: Advanced Resilience Patterns (Resilience4j) [READY]

**Branch**: `018-resilience4j-patterns` | **Date**: 2026-05-18
**Input**: Specification from `/specs/018-resilience4j-patterns/spec.md`
**Constitution Version**: v0.2.8

## Summary
The learner will build a resilient Spring WebFlux application that integrates the **Resilience4j** library to protect reactive pipelines. They will implement four interactive scenarios covering Circuit Breakers, Bulkheads, Rate Limiters, and Time Limiters. 

The application will showcase how Resilience4j's reactive operators (`transformDeferred`) allow event-loop thread protection without blocking physical OS threads.

---

## Design Decisions & Technical Architecture

### 1. Configuration Strategy: Programmatic Java Configuration
We will programmatically define all Resilience4j configuration registries inside a dedicated Java class, `@Configuration public class ResilienceConfig`.
* **Pedagogical Rationale**: Declaring configurations in `application.yml` hides the underlying builder parameters. Programmatic Java configuration allows students to inspect the exact builder methods (e.g., `CircuitBreakerConfig.custom().failureRateThreshold(50.0).build()`), directly bridging the gap between library mechanics and reactive architecture.
* **Test Utility**: Allows tests to easily inject custom registries or retrieve active state metrics.

### 2. Downstream Failure & Delay Simulation: Reactive non-blocking operators
To simulate downstream database latency or failures, we will use Reactor's native non-blocking operators:
* For delays: `Mono.delayElement(Duration.ofMillis(delay))` which schedules events on Reactor's `parallel` or `single` schedulers, avoiding worker thread blocking.
* For failures: `Mono.error(new ServiceUnavailableException(...))`.

---

## Phase 1: Monorepo Infrastructure (Base Setup)
1. **Scaffold**: Clone base setup under `labs/018-resilience4j-patterns/`.
2. **Workspace Registration**: Register the new lab as a `<module>` in the root `pom.xml`.
3. **Data Requirements**: Ensure no shadow versions exist. Inherit standard dependencies from root parent POM, registering only:
   - `io.github.resilience4j:resilience4j-reactor`
   - `io.github.resilience4j:resilience4j-spring-boot3`

---

## Phase 2: Scenario 1 & 2 - Circuit Breakers & Bulkheads
1. **Instructional Path**:
   - Create custom data model representations: `Order` and `Report`.
   - Implement `ResilienceConfig` exposing customizable `CircuitBreakerRegistry` and `BulkheadRegistry` beans.
   - Configure a custom Circuit Breaker:
     - Sliding Window Type: Count-based (10 requests).
     - Failure Rate Threshold: 50%.
     - Wait Duration in Open State: 2 seconds.
     - Automatic transition from OPEN to HALF_OPEN.
   - Configure a Bulkhead:
     - Max Concurrent Calls: 2.
     - Max Wait Duration: 0ms (fail-fast to protect thread capacity).
   - In `OrderController` / `ReportController`, apply operators:
     - `.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))`
     - `.transformDeferred(BulkheadOperator.of(bulkhead))`
     - Implement fallback mappings capturing `CallNotPermittedException` and `BulkheadFullException`.
2. **Testing**: Write JUnit 5 integration tests (`Resilience4jIntegrationTest`) using `WebTestClient`:
   - Simulate a series of client failures to trip the Circuit Breaker and assert fallback defaults.
   - Trigger concurrent background calls using `CompletableFuture` or reactive `zip` to assert Bulkhead rejection (HTTP 429).

---

## Phase 3: Scenario 3 & 4 - Rate Limiters & Time Limiters
1. **Instructional Path**:
   - Configure `RateLimiterRegistry` bean:
     - Limit For Period: 3 requests.
     - Limit Refresh Period: 5 seconds.
     - Timeout Duration: 0ms (fail-fast).
   - Configure `TimeLimiterRegistry` bean:
     - Limit Timeout Duration: 500ms.
     - Cancel Running Future: true.
   - Apply operators in controller:
     - `.transformDeferred(RateLimiterOperator.of(rateLimiter))`
     - `.transformDeferred(TimeLimiterOperator.of(timeLimiter))`
     - Wire a `.onErrorResume` chain to handle `RequestNotPermitted` and `TimeoutException` with custom fallback JSON details.
2. **Testing**: 
   - Write integration tests to fire rapid requests, validating the HTTP 429 Rate Limit block.
   - Simulate a slow downstream task (delaying 1000ms) and verify the Time Limiter intercepts it under 600ms to return the fallback response.

---

## Phase 4: Full Documentation & Dissection
1. **CONCEPT.md**: Explain the reactive thread-hopping execution landscape. Explain why traditional thread-blocking resilience libraries like Hystrix (relying on `ThreadLocal` context passing) fail under WebFlux's Event-Loop architecture, and how Resilience4j's `transformDeferred` bridges these models. Detail the internal state transition machine of Circuit Breakers (CLOSED 🔄 OPEN 🔄 HALF_OPEN).
2. **README.md**: Educational walkthrough showcasing how to run, trigger, and verify each pattern. Integrate **Command Dissection** blocks for `transformDeferred` and Resilience4j registry configurations.
3. **Collapsible Self-Assessment**: Expose interactive Q&A blocks covering core design principles.

---

## Constitution Compliance Check
- [x] No `.sh` wrapper scripts are present in the lab.
- [x] Code comments explicitly describe what every test line validates.
- [x] Language used across all text is explicitly English.
- [x] **Dependency Governance**: Correctly inherits from root parent POM without shadowing.
- [x] **Clean Modular Packages**: Establishes correct subpackages: `config`, `controller`, `model`, `service`, `exception`.
