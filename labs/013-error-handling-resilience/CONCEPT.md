# CONCEPT: Error Handling & Resilience in Spring WebFlux

Reactive programming requires a fundamental shift in how we handle failures. Since execution is asynchronous and signal-based, traditional `try-catch` blocks are insufficient for capturing errors in the pipeline.

## 1. The onError Signal
In a reactive stream, an error is a **terminal signal**. When an error occurs:
1. The operator that failed emits an `onError` signal.
2. The signal propagates downstream.
3. The stream is terminated immediately unless an error-handling operator intervenes.

## 2. Recovery Operators

### A. onErrorReturn
Provides a **fallback value** when an error occurs. The stream completes successfully with this value.
```java
Mono.error(new RuntimeException())
    .onErrorReturn("Default Value"); // Returns "Default Value"
```

### B. onErrorResume
Provides a **fallback stream**. This is useful when you want to switch to a different data source (e.g., a cache) on failure.
```java
Mono.error(new RuntimeException())
    .onErrorResume(e -> fetchFromCache());
```

## 3. Resilience Operators

### A. timeout()
Enforces a maximum execution time. If the source does not emit a signal within the duration, a `TimeoutException` is emitted.

### B. retryWhen()
Allows for sophisticated retry logic (exponential backoff, max attempts, filtering by exception type). This is essential for handling transient network issues.

## 4. RFC 7807: Problem Details
Modern Spring Boot applications use **RFC 7807** to return standardized error responses. Instead of returning a generic JSON, we return a `ProblemDetail` object. This ensures that all API consumers receive consistent error metadata.

## 5. Global vs Local Handling
- **Local (`onErrorX`)**: Handle errors near the source (e.g., provide a default value for a specific service call).
- **Controller (`@ExceptionHandler`)**: Handle business exceptions specific to a resource (e.g., `ProductNotFound`).
- **Global (`WebExceptionHandler`)**: The last line of defense. Catch unhandled system errors and format them securely for the client.

## 6. Context Propagation
Reactor's `Context` allows us to pass metadata (like a `trace-id`) through the pipeline without explicitly passing arguments. In this lab, we use it to ensure every error response includes a correlation ID for debugging.
