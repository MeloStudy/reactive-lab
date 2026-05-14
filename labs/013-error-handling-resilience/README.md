# LAB-013: Error Handling & Resilience in WebFlux

## Overview
In this laboratory, you will master the art of "taming" failures in a reactive ecosystem. You will build a system that gracefully handles localized errors, provides business-friendly error responses, and remains resilient under stress.

## Learning Objectives
- Implement graceful fallbacks with `onErrorReturn`.
- Design standardized API errors using **RFC 7807 Problem Details**.
- Propagate **Correlation IDs** through the Reactor Context to enrich error responses.
- Apply **Exponential Backoff** and **Timeouts** to handle unstable dependencies.

## Instructions

1.  **Scenario 1: Local Fallbacks**
    Examine `ResilienceController.getItem`. Observe how `onErrorReturn` is used to provide a default item when the service fails.
    🔗 **Traceable Implementation**: [ResilienceImplementation.java](src/main/java/com/reactivelab/resilience/ResilienceImplementation.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)
    
2.  **Scenario 2: Business Exceptions**
    Check `@ExceptionHandler(ProductNotFoundException.class)`. This handles errors specific to the controller layer.
    🔗 **Traceable Implementation**: [ResilienceImplementation.java](src/main/java/com/reactivelab/resilience/ResilienceImplementation.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)

3.  **Scenario 3 & 4: The Global Guard & Context Propagation**
    Look at `GlobalErrorWebExceptionHandler.java`. It catches all unhandled exceptions and formats them using `ProblemDetail`, including a correlation ID extracted from the context.
    🔗 **Traceable Implementation**: [ErrorHandlers.java](src/main/java/com/reactivelab/resilience/ErrorHandlers.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)

4.  **Scenario 5: Execution Limits**
    See how `.timeout(Duration.ofSeconds(1))` is applied to a slow endpoint to prevent resource exhaustion.
    🔗 **Traceable Implementation**: [ResilienceImplementation.java](src/main/java/com/reactivelab/resilience/ResilienceImplementation.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)

5.  **Scenario 6: Intelligent Retries**
    Review `ResilienceService.callUnstableService`. It uses `retryWhen` with exponential backoff to handle transient failures.
    🔗 **Traceable Implementation**: [ResilienceImplementation.java](src/main/java/com/reactivelab/resilience/ResilienceImplementation.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)

6.  **Scenario 7: Filter-Level Errors**
    Review how the `GlobalErrorWebExceptionHandler` catches errors thrown before reaching the controller.
    🔗 **Traceable Implementation**: [ErrorHandlers.java](src/main/java/com/reactivelab/resilience/ErrorHandlers.java) | [Test Suite](src/test/java/com/reactivelab/resilience/ResilienceIntegrationTest.java)

## Command Dissections

### 1. `onErrorReturn`
```java
pipeline.onErrorReturn(defaultValue)
```
- **What**: Replaces an error signal with a value.
- **Why**: Used for simple fallbacks where a "Safe Default" is better than a failure.

### 2. `ProblemDetail`
```java
ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
```
- **What**: A Spring/RFC 7807 class for standardizing error JSON.
- **Why**: Decouples error logic from the response format.

### 3. `retryWhen`
```java
.retryWhen(Retry.backoff(3, Duration.ofMillis(100)))
```
- **What**: Sophisticated retry mechanism.
- **Why**: "Backoff" prevents overwhelming a service that is already struggling.

## 🧠 Self-Assessment
<details>
<summary>1. What is the difference between <code>onErrorResume</code> and <code>onErrorReturn</code>?</summary>
<code>onErrorReturn</code> provides a static fallback value, while <code>onErrorResume</code> provides a fallback Publisher (stream), allowing you to execute alternative reactive flows (like calling a backup service).
</details>

<details>
<summary>2. Why is <code>ProblemDetail</code> preferred over custom Map-based error responses?</summary>
<code>ProblemDetail</code> is part of Spring 3 and implements RFC 7807, providing a standardized, industry-wide JSON schema for HTTP API errors, making it easier for clients to parse.
</details>

<details>
<summary>3. How does <code>timeout()</code> improve system stability?</summary>
It prevents slow external dependencies from indefinitely tying up the reactive Event Loop, ensuring the application remains responsive even when upstream services degrade.
</details>

## Verification
Run the comprehensive test suite:
```bash
mvn test -pl labs/013-error-handling-resilience
```
