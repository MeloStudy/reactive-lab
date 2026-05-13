# Concept: WebClient & Reactive Orchestration

In a microservices architecture, services must communicate without blocking threads. Traditionally, Java developers used `RestTemplate`, which is synchronous and blocking. In the reactive world, we use **WebClient**.

## 1. Under the Hood: Netty & The Event Loop

`WebClient` is built on top of **Project Reactor Netty**. Unlike `RestTemplate`, which uses a "Thread-per-Request" model, `WebClient` uses a small number of **Event Loop threads** (typically equal to the number of CPU cores).

- **The Cycle**: When you initiate a request, `WebClient` registers the request with the OS (using epoll/kqueue) and immediately releases the thread.
- **The Signal**: When the response data arrives, the OS notifies the Event Loop, which then executes the reactive pipeline (mapping, filtering, etc.).
- **The Golden Rule**: You must **never block** inside a `WebClient` pipeline. Blocking an Event Loop thread stops it from handling hundreds of other concurrent requests, leading to severe performance degradation.

## 2. retrieve() vs exchangeToMono()

Handling response bodies correctly is critical for resource management.

### A. retrieve() (The Safe Default)
`retrieve()` is the easiest and safest way to fetch a response body. It handles the consumption of the body automatically. Even if you don't use the body (e.g., `.toBodilessEntity()`), `WebClient` ensures the connection is released back to the pool.

```java
webClient.get().uri("/...").retrieve().bodyToMono(User.class);
```

### B. exchangeToMono() / exchangeToFlux() (The Expert Choice)
`exchangeToMono` gives you full access to the `ClientResponse` (headers, cookies, status code).
**IMPORTANT**: You are responsible for consuming the body. If you don't call `.bodyToMono()`, `.bodyToFlux()`, or `.releaseBody()`, you will **leak memory** and the connection will not return to the pool.

```java
webClient.get().uri("/...")
  .exchangeToMono(response -> {
      if (response.statusCode().is2xxSuccessful()) {
          return response.bodyToMono(User.class);
      } else {
          // Manual release if we don't want the body
          return response.releaseBody().then(Mono.error(new RuntimeException("Error")));
      }
  });
```

## 3. Connection Pooling & Backpressure

`WebClient` uses a `ConnectionProvider` to manage a pool of persistent connections (Keep-Alive).
- **Backpressure**: While `WebClient` itself doesn't "block" when the pool is full, the reactive stream will propagate backpressure to the producer if the downstream service is slow and all connections are busy.
- **Resource Management**: Properly closing streams and consuming bodies ensures that connections are recycled, preventing `PoolAcquireTimeoutException`.

## 4. Resilience Patterns

Reactive orchestration is not just about combining streams; it's about surviving failures.
- **Timeouts**: `timeout(Duration)` protects you from "zombie" services that never respond.
- **Retries**: `retryWhen(Retry)` allows for intelligent recovery strategies like Exponential Backoff with Jitter.
- **Fallbacks**: `onErrorResume` provides a way to return default data or call a secondary service when the primary one fails.
