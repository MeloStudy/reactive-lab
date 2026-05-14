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

## 6. Context Propagation & Evolution

Reactor's `Context` allows us to pass metadata (like a `trace-id`) through the pipeline without explicitly passing arguments. In this lab, we use it to ensure every error response includes a correlation ID for debugging.

### Evolution: Reactor Context vs. Scoped Values (Java 21+)
As the JVM evolves with **Project Loom**, Java 21+ introduces **Scoped Values** (`ScopedValue<T>`) as a modern, lightweight alternative to `ThreadLocal`. 
- **In an Imperative/Virtual Thread model**: You would use `ScopedValue.where(TRACE_ID, "123").run(...)` to implicitly pass data down the call stack.
- **In a Reactive model**: WebFlux and Project Reactor still heavily rely on the `Context` because a single reactive pipeline might hop across multiple physical and virtual threads via the Event Loop. While Scoped Values are powerful for blocking virtual threads, the Reactor `Context` remains the safest and most idiomatic way to propagate state across asynchronous, non-blocking boundaries in Spring WebFlux.
