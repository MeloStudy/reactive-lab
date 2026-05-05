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
    
2.  **Scenario 2: Business Exceptions**
    Check `@ExceptionHandler(ProductNotFoundException.class)`. This handles errors specific to the controller layer.

3.  **Scenario 3 & 4: The Global Guard**
    Look at `GlobalErrorWebExceptionHandler.java`. It catches all unhandled exceptions and formats them using `ProblemDetail`, including a correlation ID extracted from the context.

4.  **Scenario 5: Execution Limits**
    See how `.timeout(Duration.ofSeconds(1))` is applied to a slow endpoint to prevent resource exhaustion.

5.  **Scenario 6: Intelligent Retries**
    Review `ResilienceService.callUnstableService`. It uses `retryWhen` with exponential backoff to handle transient failures.

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

## Self-Assessment
1. What is the difference between `onErrorResume` and `onErrorReturn`?
2. Why is `ProblemDetail` preferred over custom Map-based error responses?
3. How does `timeout()` improve system stability?

## Verification
Run the comprehensive test suite:
```bash
mvn test -pl labs/013-error-handling-resilience
```
