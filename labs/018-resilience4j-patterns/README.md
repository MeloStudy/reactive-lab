# LAB-018: Advanced Resilience Patterns (Resilience4j)

## Overview
In this laboratory, you will master the implementation of advanced microservice resilience patterns within non-blocking Spring WebFlux pipelines. You will build, refactor, and test a resilient system utilizing **Resilience4j**'s reactive operators (`transformDeferred`) to guard your application against downstream delays, concurrency rushes, network limit breaches, and processing time overruns.

---

## Learning Objectives
* **LO-001**: Integrate Resilience4j with Reactor using the reactive bridge operators (`transformDeferred`).
* **LO-002**: Master Circuit Breaker states (CLOSED, OPEN, HALF_OPEN) and sliding window metrics.
* **LO-003**: Configure Semaphore-based Bulkheads to limit concurrent execution counts in WebFlux.
* **LO-004**: Apply token-bucket Rate Limiters to throttle inbound client requests.
* **LO-005**: Integrate Time Limiters to guard sluggish reactive streams and cancel running tasks.
* **LO-006**: Design custom Fallback handlers to recover from tripped state exceptions (e.g., `CallNotPermittedException`).

---

## Project Structure & Traceable Implementation
All codebase components are organized in a clean, production-grade package structure. Click the links below to study the active implementations:

* 🔗 **Domain Models**:
  * [Order.java](src/main/java/com/reactivelab/resilience/model/Order.java) (Order domain mapping)
  * [Report.java](src/main/java/com/reactivelab/resilience/model/Report.java) (Financial Report domain mapping)
  * [Weather.java](src/main/java/com/reactivelab/resilience/model/Weather.java) (Weather domain mapping)
  * [Analytics.java](src/main/java/com/reactivelab/resilience/model/Analytics.java) (Analytics telemetry domain mapping)
* 🔗 **Resilience Configuration Registry**: [ResilienceConfig.java](src/main/java/com/reactivelab/resilience/config/ResilienceConfig.java) (Exposes custom programmatic `@Bean` registries for testability).
* 🔗 **Downstream Mock Service**: [ResilienceService.java](src/main/java/com/reactivelab/resilience/service/ResilienceService.java) (Simulates delayed, failing, or flaky downstream processes).
* 🔗 **Reactive Controller**: [ResilienceController.java](src/main/java/com/reactivelab/resilience/controller/ResilienceController.java) (Maps endpoints and applies `transformDeferred` bridges).
* 🔗 **Verification Test Suite**: [Resilience4jIntegrationTest.java](src/test/java/com/reactivelab/resilience/Resilience4jIntegrationTest.java) (Automated verification including BlockHound integrations).

---

## Command Dissections

### 1. Reactive Circuit Breaker Operator
```java
pipeline.transformDeferred(CircuitBreakerOperator.of(orderCircuitBreaker))
```
* **What**: Applies the Circuit Breaker state machine dynamically per subscription.
* **Why**: When the failure rate (e.g. `ServiceUnavailableException`) exceeds the 50% threshold in the count window (10 requests), it trips `OPEN`, raising `CallNotPermittedException` instantly without hammering the database.

### 2. Concurrent Bulkhead Isolation Operator
```java
pipeline.transformDeferred(BulkheadOperator.of(reportBulkhead))
```
* **What**: Wraps a Semaphore-based limit around active subscriptions.
* **Why**: WebFlux is single-threaded per CPU core; standard thread pools break the event loop. The Semaphore-based bulkhead uses lock-free atomic counters to restrict active execution to a maximum of 2, rejecting extra concurrent requests immediately with `BulkheadFullException` (HTTP 429).

### 3. Reactive Rate Limiter Operator
```java
pipeline.transformDeferred(RateLimiterOperator.of(weatherRateLimiter))
```
* **What**: Applies token-bucket rate quotas to reactive publishers.
* **Why**: Restricts inbound requests (e.g., max 3 requests per 5 seconds). If a client breaches the limit, it throws `RequestNotPermitted` instantly, prompting our controller fallback handler to return HTTP 429 with `Retry-After: 5` header.

### 4. Time Limiter Timeout Operator
```java
pipeline.transformDeferred(TimeLimiterOperator.of(analyticsTimeLimiter))
```
* **What**: Aborts sluggish publishers exceeding the execution window (500ms).
* **Why**: Prevents resource starvation by issuing a thread cancellation signal to the delayed publisher, dropping execution and transitioning to the cached static fallback.

---

## Verification & Self-Validation
Validate the entire resilient architecture by running the Maven test suite. BlockHound is automatically initialized to guarantee no blocking operations disrupt the event-loop threads:

```bash
mvn clean test -pl labs/018-resilience4j-patterns
```

If the execution succeeds, you will see a `BUILD SUCCESS` with all 4 integration tests passing flawlessly.
