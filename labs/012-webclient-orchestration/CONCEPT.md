# Concept: WebClient & Reactive Orchestration

In a microservices architecture, services rarely work in isolation. They need to communicate with each other. Traditionally, Java developers used `RestTemplate`, which is synchronous and blocking. In the reactive world, we use **WebClient**.

## 1. Why WebClient?

`RestTemplate` uses the "Thread-per-Request" model. When you make an HTTP call, the calling thread blocks until the response arrives. If the downstream service is slow, your thread is held hostage, leading to thread exhaustion.

**WebClient** is:
- **Non-blocking**: It initiates the request and frees the thread immediately. The response is handled via callbacks (signals) when it arrives.
- **Reactive**: It returns `Mono` or `Flux`, allowing for easy composition with other reactive streams.
- **Efficient**: It uses an underlying Event Loop (usually Netty) to handle thousands of concurrent connections with very few threads.

## 2. Key APIs: retrieve() vs exchange()

When making a call, you have two main ways to handle the response:

### A. retrieve()
The easiest way to get the body. It automatically handles error status codes (4xx, 5xx) if you provide `.onStatus()` handlers.
```java
webClient.get().uri("/...").retrieve().bodyToMono(User.class);
```

### B. exchangeToMono() / exchangeToFlux()
Provides full control over the `ClientResponse` (headers, cookies, status). 
**WARNING**: You are responsible for consuming the response body or closing it. If you don't, you will leak memory and connection pool slots.
```java
webClient.get().uri("/...")
  .exchangeToMono(response -> {
      if (response.statusCode().equals(HttpStatus.OK)) {
          return response.bodyToMono(User.class);
      } else {
          return response.createException().flatMap(Mono::error);
      }
  });
```

## 3. Orchestration Patterns

### Parallel Calls (zip)
When you need data from multiple independent services, don't call them one by one. Use `Mono.zip`.
- **Imperative**: `Time = T1 + T2 + T3`
- **Reactive**: `Time = Max(T1, T2, T3)`

### Sequential Calls (flatMap)
When the second call depends on the result of the first call.
```java
getUser(id).flatMap(user -> getPreferences(user.getPrefId()));
```

## 4. Resilience: The Reactive Way

Reactive streams provide built-in operators for common resilience patterns:
- **Timeout**: `timeout(Duration.ofSeconds(2))` triggers an error if the service is too slow.
- **Retry**: `retryWhen(Retry.backoff(...))` allows for sophisticated retry strategies (exponential, jitter).
- **Fallback**: `onErrorReturn` or `onErrorResume` provides a graceful way to recover from failures.

## 5. Threading Model

WebClient uses the **Event Loop**. When a request is sent, the task is delegated to the OS kernel. When the data returns, the Event Loop thread is notified, and it resumes the pipeline execution. This is why you should **never block** inside a WebClient pipeline, as you would block the Event Loop that is potentially handling hundreds of other requests.
