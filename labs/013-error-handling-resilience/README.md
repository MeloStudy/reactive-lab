# LAB-013: Error Handling & Resilience in WebFlux

## Overview
In this laboratory, you will master the art of "taming" failures in a reactive ecosystem. You will build, refactor, and test a non-blocking Spring WebFlux system that handles errors at multiple levels (pipeline, controller, and global), provides standardized RFC 7807 Problem Detail structures, propagates Correlation IDs across thread boundaries, and applies server-side execution timeouts and exponential retry backoffs.

---

## Learning Objectives
- Master the difference between pipeline-level fallbacks (`onErrorReturn`) and flow-switching (`onErrorResume`).
- Design enterprise-standardized API error contracts using **RFC 7807 Problem Details**.
- Propagate trace metadata safely across Event-Loop boundaries using the **Reactor Context**.
- Implement self-healing consumer integrations using **Exponential Backoff and Jitter**.
- Mitigate event-loop starvation using reactive server-side timeouts.

---

## Project Structure & Traceable Implementation
All codebase components are organized in a clean, production-grade package structure. Navigate to the links below to study the implementation:

- 🔗 **Business Service**: [ResilienceService.java](src/main/java/com/reactivelab/resilience/service/ResilienceService.java) (Manages reactive retrieval, timeouts, and retry chains).
- 🔗 **Rest Controller**: [ResilienceController.java](src/main/java/com/reactivelab/resilience/controller/ResilienceController.java) (Defines HTTP mappings and `@ExceptionHandler` business mappings).
- 🔗 **Functional Router**: [FunctionalRouter.java](src/main/java/com/reactivelab/resilience/router/FunctionalRouter.java) (Configures functional security filter-level check endpoints).
- 🔗 **Telemetry Web Filter**: [CorrelationIdFilter.java](src/main/java/com/reactivelab/resilience/filter/CorrelationIdFilter.java) (Injects `X-Correlation-ID` into request attributes and Reactor Context).
- 🔗 **Global Exception Guard**: [GlobalErrorWebExceptionHandler.java](src/main/java/com/reactivelab/resilience/exception/GlobalErrorWebExceptionHandler.java) (Renders standardized RFC 7807 JSON with dynamic Correlation ID resolution).
- 🔗 **Verification Test Suite**: [ResilienceIntegrationTest.java](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java) (Comprehensive JUnit 5 WebTestClient scenarios).

---

## Command Dissections

### 1. Local Fallback Operator: `onErrorReturn`
```java
pipeline.onErrorReturn(fallbackValue)
```
- **What**: Intercepts an `onError` signal and emits a pre-constructed static fallback value instead, completing the stream successfully.
- **Why**: Used for non-critical lookups where a safe default (e.g. empty user dashboard, zero recommendations) is highly preferred over throwing a HTTP 500 error.
- **Visual Signal Flow**: `---(x)---> [onErrorReturn(Default)] --->(Default)---|--->`

### 2. Standardized API Errors: `ProblemDetail`
```java
ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "Product not found");
problem.setProperty("custom_key", value);
```
- **What**: A standard carrier structure introduced in Spring 6/Boot 3 complying with RFC 7807.
- **Why**: Eliminates custom, ad-hoc Map-based JSON representations by establishing a uniform schema format across modern APIs.

### 3. Non-Blocking Event-Loop Telemetry: `Mono.deferContextual`
```java
Mono.deferContextual(context -> {
    String trace = context.getOrDefault("X-Correlation-ID", "N/A");
    return ServerResponse.ok().bodyValue(trace);
})
```
- **What**: Lazily accesses the immutable `ContextView` propagated upstream from the subscriber boundary.
- **Why**: Traditional ThreadLocals corrupt and leak memory in Event-Loop workers because thread pools are shared concurrently. `deferContextual` extracts metadata from the active **Subscription** state.

### 4. Self-Healing Pipelines: `retryWhen`
```java
.retryWhen(Retry.backoff(3, Duration.ofMillis(100)).jitter(0.75))
```
- **What**: Retries a failed pipeline using exponential delays (e.g., 100ms, 200ms, 400ms) randomized with a noise factor (jitter).
- **Why**: Prevents "thundering herd" conditions that crash recovering downstream services by spacing request attempts.

---

## 🧠 Self-Assessment & Knowledge Check

<details>
<summary>1. Why can we NOT use traditional ThreadLocal and MDC in a standard reactive WebFlux application?</summary>
<p>
ThreadLocals assume a strict "Thread-per-Request" model. In WebFlux, a Netty event loop thread handles hundreds of requests concurrently, jumping from one callback to another during non-blocking execution. Storing request-specific context in a ThreadLocal will lead to severe data corruption and metadata leaking between concurrent requests. Telemetry must be stored inside the <b>Reactor Context</b>.
</p>
</details>

<details>
<summary>2. What is the fundamental direction of propagation for the Reactor Context?</summary>
<p>
The Reactor Context propagates <b>UPSTREAM</b> (from the subscriber at the end of the chain up towards the publisher source). This means operators located <i>above</i> the `.contextWrite()` line will see the metadata, while operators placed <i>below</i> it in the subscription flow will not.
</p>
</details>

<details>
<summary>3. Why can a global WebExceptionHandler not read keys directly from the Reactor Context written in a WebFilter?</summary>
<p>
Spring WebFlux wraps the entire WebFilter chain in a <code>FilteringWebHandler</code>. This handler is executed by <code>HttpWebHandlerAdapter</code>, which catches exceptions downstream using an <code>onErrorResume</code> handler mapping to the Exception Handler. Since <code>WebExceptionHandler</code> is downstream of the WebFilter execution, any context written inside the filter chain is invisible at the global exception handling boundary. A hybrid lookup falling back to Exchange Attributes is the standard industry design solution.
</p>
</details>

<details>
<summary>4. How does Project Reactor Context compare with Java 21+ Scoped Values (Project Loom)?</summary>
<p>
Java 21 Scoped Values (<code>ScopedValue<T></code>) are designed for downstream propagation across imperative Call Stacks (typically on Virtual Threads). Reactor Context is designed for upstream propagation across multi-threaded asynchronous Stream Subscriptions. In non-blocking WebFlux architectures, Reactor Context remains the primary and only safe telemetry propagation mechanism.
</p>
</details>

---

## Verification
Validate the entire resilient architecture by running the test suite:
```bash
mvn clean test -pl labs/013-error-handling-resilience
```
