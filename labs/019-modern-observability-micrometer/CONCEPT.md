# Micrometer Observation and Context Propagation

## The Challenge in Reactive Systems
In traditional, blocking Spring MVC applications, contexts (such as security context, trace IDs, and MDC for logging) are stored in `ThreadLocal` variables. Because a single thread handles the request from start to finish, this works perfectly. 

However, in Project Reactor (Spring WebFlux), the execution paradigm shifts to an **Event Loop** model where a single thread (or a small pool of threads) handles multiple concurrent requests, interleaving their execution. Furthermore, a reactive pipeline might hop across different threads using operators like `.publishOn()` or `.subscribeOn()`. 
If we rely on `ThreadLocal`, the context is lost or wrongly shared as soon as a thread switch occurs.

## Reactor Context
To solve this, Project Reactor provides the `Context`. The `Context` is an immutable, Map-like structure bound to the **Subscription** lifecycle, not the Thread. It flows backwards from the subscriber up the reactive chain, ensuring that every operator in the pipeline has access to it, regardless of which thread it executes on.

## Micrometer Observation API
Micrometer Observation acts as a unified facade for Metrics, Tracing, and Logging. 
Instead of writing separate code for counting requests (Metrics) and creating spans (Tracing), you wrap your logic in an `Observation`.

```java
// In a reactive pipeline, we use Micrometer operators instead of blocking callbacks:
Mono.just("data")
    .name("my.operation") // Defines the Observation name
    .tag("type", "process") // Defines a Low Cardinality Tag
    .tap(Micrometer.observation(observationRegistry)); // Injects Observation into Context
```

### High vs. Low Cardinality
- **Low Cardinality**: Values that have a small, bounded set of possible options (e.g., HTTP methods, status codes, region). These are safe to use as tags in Time-Series Databases (like Prometheus) because they don't cause an explosion of metric dimensions.
- **High Cardinality**: Values that have a massive or unbounded set of possibilities (e.g., User IDs, Transaction IDs). These MUST NOT be used in Metrics (it would crash Prometheus), but they are incredibly useful for Distributed Tracing (like Zipkin). 

Micrometer automatically routes low cardinality keys to both Metrics and Tracing, and high cardinality keys exclusively to Tracing.

### Bringing It Together in WebFlux
Spring Boot 3 automatically integrates Micrometer Observation with Reactor. When a request hits WebFlux, an Observation is started, and its state is placed into the `Reactor Context`. If you make an outbound call using `WebClient`, Spring extracts the Observation from the Reactor Context and injects the Trace ID headers into the outgoing HTTP request, enabling distributed tracing across microservices seamlessly.
