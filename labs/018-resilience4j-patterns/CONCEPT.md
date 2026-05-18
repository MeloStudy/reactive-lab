# CONCEPT: Advanced Resilience Patterns with Resilience4j Reactor

In a microservices architecture, network partitions, high CPU utilization, and downstream latency spikes are inevitable realities. In a traditional imperative application, these failures are mitigated using thread isolation, thread timeouts, and blocking retries. However, in non-blocking, reactive architectures, applying imperative resilience strategies will cause event-loop starvation and catastrophic cascade failures.

This document explores how to architect enterprise-grade resilience within reactive pipelines using **Resilience4j** and **Project Reactor**.

---

## 1. Why Imperative Resilience Fails in Event Loops (EX-001)

Historically, libraries like **Netflix Hystrix** protected systems by assigning downstream service calls to isolated thread pools (the bulkhead pattern) and blocking calls with timeouts:

```
[Inbound Netty Worker Thread]
             │
             ▼
    [Hystrix Thread Pool Queue] ──(Blocks Thread)──> [Thread Pool Worker] ──(Sync HTTP Call)──> [Downstream API]
```

In a non-blocking WebFlux runtime (Netty), a single physical CPU core is assigned to one Netty worker thread (an event-loop worker). If any operation blocks that thread, **all** concurrent requests multiplexed on that event-loop are instantly halted:

1. **Thread Starvation**: If you allocate thread pools for asynchronous tasks and block to wait for results, you rapidly deplete CPU context switching capability.
2. **Reactor Stream Interference**: Using standard thread-blocking locks or synchronous timeouts on a reactive publisher halts the Netty worker, causing severe latency spikes and violating the reactive foundation.

### The Resilience4j Reactor Bridge (`transformDeferred`)

Resilience4j resolves this by providing a native, non-blocking bridge via the `resilience4j-reactor` module. Instead of wrapping calls in blocking proxies, it uses custom Reactor operators (e.g., `CircuitBreakerOperator`, `BulkheadOperator`, `RateLimiterOperator`) applied during assembly time using **`transformDeferred`**:

```java
Mono.just(request)
    .flatMap(service::call)
    .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
    .onErrorResume(ex -> handleFallback(ex));
```

#### Why `transformDeferred` instead of `transform`?
* **`transform` (Static Assembly)**: Applies the operator exactly once when the pipeline is first built (assembly time). This means all subscriptions share a static context representation, which can leak state across distinct client requests.
* **`transformDeferred` (Dynamic Subscription)**: Evaluates the pipeline setup *individually for each subscription* (runtime execution). This ensures that every subscriber receives an isolated, dynamically monitored operator wrapper, allowing accurate metrics tracking per call.

---

## 2. Circuit Breaker State Transition Mechanics (EX-002)

The `CircuitBreaker` acts as a safety switch. Instead of constantly hammering a failing downstream service, it fails-fast to protect the system.

```mermaid
stateDiagram-v2
    [*] --> CLOSED : Initialize
    
    state CLOSED {
        [*] --> CountFailures : Track sliding window
        CountFailures --> CLOSED : Failure Rate < Threshold
        CountFailures --> OPEN : Failure Rate >= Threshold
    }
    
    CLOSED --> OPEN : Trips (Failure Rate >= 50%)
    
    state OPEN {
        [*] --> FailFast : Immediately reject (CallNotPermittedException)
        FailFast --> HALF_OPEN : Wait Duration Expires (2 seconds)
    }
    
    OPEN --> HALF_OPEN : Wait duration elapsed
    
    state HALF_OPEN {
        [*] --> TrialExecutions : Permit test probe calls
        TrialExecutions --> CLOSED : Success Rate >= Threshold
        TrialExecutions --> OPEN : Failure Rate >= Threshold
    }
    
    HALF_OPEN --> CLOSED : Probes successful (Resets)
    HALF_OPEN --> OPEN : Probe fails (Trips again)
```

### The Three Operational States

1. **CLOSED**: The circuit breaker is fully functional, permitting all requests to pass to the downstream service. It tracks calls inside a configured **sliding window** (either count-based or time-based).
2. **OPEN**: If the failure rate (e.g., HTTP 5xx errors or exceptions) exceeds the configured threshold (e.g., 50%) within the sliding window, the circuit breaker trips **OPEN**. All subsequent requests are rejected immediately with a `CallNotPermittedException` (fail-fast), bypassing the downstream service entirely.
3. **HALF_OPEN**: After a configured wait duration (e.g., 2 seconds), the circuit breaker transitions to **HALF_OPEN**. It permits a small, restricted number of trial calls (probes) to test the downstream service:
   * If the probe calls succeed, the circuit transitions back to **CLOSED** and resets all metrics.
   * If any probe call fails, the circuit immediately returns to **OPEN** and resets the wait timer.

---

## 3. Semaphore-based Bulkheads vs. Thread-Pool Bulkheads (EX-003)

The Bulkhead pattern isolates resources to prevent a failure in one downstream dependency from consuming all system capacity. Resilience4j offers two implementations:

| Attribute | Semaphore Bulkhead | Thread-Pool Bulkhead |
| :--- | :--- | :--- |
| **Mechanics** | Leverages a lock-free atomic counter to track active concurrent execution counts. | Isolates executions inside a dedicated, bound queue and custom thread pool. |
| **Blocking Impact** | Highly efficient. Does not block physical threads; immediately rejects calls if the counter limit is breached (`BulkheadFullException`). | Introduces physical thread queues, increasing context switching and memory allocation overhead. |
| **WebFlux Context** | **Recommended for WebFlux**. Complements Reactor's non-blocking model by enforcing numeric caps without allocating extra threads. | **Not recommended for native non-blocking WebFlux**. Thread context switches break Reactor's Event-Loop efficiency. |
| **Metadata Security**| Preserves Reactor `Context` seamlessly because execution remains on the same subscription thread pipeline. | Thread-hopping disrupts Reactor `Context` propagation, requiring manual context copying configurations. |

---

## 4. Rate Limiters & Time Limiters

### A. Rate Limiter (Token Bucket Algorithm)
The `RateLimiter` enforces client-side API throttling to prevent abuse and API exhaustion. It tracks execution counts inside a rolling time window:
* **Limit For Period**: Maximum number of permits available during a single period (e.g., 3 requests).
* **Limit Refresh Period**: The duration after which the permit pool is fully refreshed (e.g., 5 seconds).
* **Timeout Duration**: The maximum time a client will block to acquire a permit. In high-throughput reactive APIs, this is set to **0ms (fail-fast)** to avoid event-loop queue blocks.

### B. Time Limiter
A `TimeLimiter` restricts the duration of an active reactive execution:
* **Timeout Duration**: If the upstream publisher does not emit a completion or element within this window (e.g., 500ms), the stream is aborted, throwing a `TimeoutException`.
* **Cancel Running Future**: Tells the publisher to instantly cancel the underlying subscription and release resources upon timing out, preventing orphan background computations.

---

## 5. Collapsible Self-Assessment Questions (TR-005)

<details>
<summary><b>Q1: Why is using ThreadLocal-based security contexts or tracing (like MDC) challenging when applying a Thread-Pool Bulkhead?</b></summary>

**Answer:**  
In Java, `ThreadLocal` variables are bound to the specific operating system thread currently executing the instruction. 
When using a Thread-Pool Bulkhead, Resilience4j intercepts the execution and schedules the task onto a *different worker thread* managed by the bulkhead's custom pool. 
Because of this "thread hop", all metadata stored in the original thread's `ThreadLocal` context is lost. 

In a reactive WebFlux pipeline, metadata must instead be stored inside Reactor's subscription-bound `Context`. If you must hop threads, you must write custom decorators (e.g., using `ContextRegistry` or custom hook handlers) to manually copy context from the parent thread to the worker pool thread. This is why **Semaphore-based Bulkheads** are preferred for WebFlux: they operate on atomic counters without switching threads, naturally preserving Reactor Context.
</details>

<details>
<summary><b>Q2: What happens if a developer places the <code>.onErrorResume()</code> block BEFORE the <code>.transformDeferred(CircuitBreakerOperator.of(...))</code> block?</b></summary>

**Answer:**  
If `onErrorResume` is placed *before* the `CircuitBreakerOperator`, the error emitted by the upstream service is caught and resolved to a success signal (a fallback value) *before* it reaches the Circuit Breaker. 
To the Circuit Breaker, the pipeline appears to have completed successfully (emitting an `onNext` and `onComplete` instead of an `onError`). 

As a result:
1. The Circuit Breaker's internal failure counter will never increment.
2. The Circuit Breaker will remain in `CLOSED` state indefinitely, failing to protect downstream systems.

**Correct Order:**
```java
return service.call()
        .transformDeferred(CircuitBreakerOperator.of(breaker)) // 1. Circuit Breaker observes raw errors
        .onErrorResume(ex -> fallbackStream());                // 2. Fallback intercepts error and resolves it safely
```
</details>

<details>
<summary><b>Q3: How does a Time Limiter react to a lazy, cold publisher that does not emit any data immediately?</b></summary>

**Answer:**  
In Project Reactor, standard publishers are "cold" (lazy), meaning they do not start execution until a subscriber registers a subscription. 
Resilience4j's `TimeLimiterOperator` starts its countdown timer *at the moment of subscription*. 

If the cold publisher delays starting its execution or takes longer than the timeout threshold to emit its first element (`onNext`), the `TimeLimiter` will trip, cancel the active subscription, and emit a `TimeoutException`. This protects Netty workers from waiting indefinitely on dormant publishers that are stalled due to thread pool starvation or connection pool leaks.
</details>
