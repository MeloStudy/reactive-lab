# Concept: Backpressure Strategies & Flow Control

## The Backpressure Problem in Reactive Streams

In a non-blocking, asynchronous pipeline, data producers and consumers often operate at different speeds. When a producer (upstream) emits data faster than a consumer (downstream) can process it, we face an **overflow condition**.

Traditional blocking systems handle this by naturally slowing down the producer (e.g., the thread is blocked until the consumer is ready). In a reactive system, where threads are not blocked, we need an explicit protocol to signal demand. This is **Backpressure**.

### Push vs. Pull Model

1.  **Pull (Demand-based)**: The subscriber explicitly requests `n` elements. The publisher only sends at most `n`. This is the ideal Reactive Streams behavior.
2.  **Push (Uncontrolled)**: The publisher ignores demand and pushes data as it becomes available (e.g., a mouse move event, a hot sensor stream). This is where backpressure strategies are mandatory.

---

## Project Reactor Backpressure Operators

When the "Pull" model is bypassed, Project Reactor provides several strategies to handle the mismatch:

### 1. Buffering (`onBackpressureBuffer`)

This strategy stores elements in a queue until the subscriber is ready to process them.
-   **When to use**: When spikes are temporary and memory allows for storage.
-   **Risk**: If the mismatch is permanent, the buffer will eventually overflow, leading to `OutOfMemoryError` or a `BackpressureOverflowException`.
-   **Configurability**: You can limit the buffer size and define what happens on overflow (e.g., drop oldest, drop latest, or error).

### 2. Dropping (`onBackpressureDrop`)

This strategy simply discards elements that arrive when there is no demand.
-   **When to use**: When only the "newest" data is relevant at any given time, and missing intermediate data is acceptable (e.g., telemetry logs).
-   **Advantage**: Lowest memory overhead and guaranteed system stability.

### 3. Latest (`onBackpressureLatest`)

Similar to dropping, but it always keeps the *most recent* element. If a new element arrives while demand is 0, it replaces the previous "latest" element.
-   **When to use**: Real-time dashboards or UI updates where you only care about the very last state.

---

## Higher-Level Flow Control: Buffering and Windowing

Sometimes, the best way to handle a high-frequency stream is to change the "grain" of the data.

### `buffer()`
Collects incoming elements into a `List` based on count or time and emits the list as a single element.
-   **Result**: `Flux<T>` becomes `Flux<List<T>>`.
-   **Impact**: Reduces the frequency of signals but increases the payload of each signal.

### `window()`
Similar to `buffer`, but instead of a `List`, it emits another `Flux`.
-   **Result**: `Flux<T>` becomes `Flux<Flux<T>>`.
-   **Impact**: Allows for nested reactive processing (e.g., calculating averages per 10-second window in a non-blocking way).

---

## Internal Mechanics: The request(n) Signal

Every reactive stream starts with a `Subscription`. When a subscriber is ready, it calls `request(n)`. This signal propagates upstream.
-   Operators like `publishOn` act as **backpressure boundaries**. They have an internal buffer (default 256) and handle requests to the upstream on behalf of the downstream.
-   If you use a `Sink` (like `Sinks.many().multicast()`), you are moving into the "Push" world. You must decide how the sink behaves when subscribers are slow.

## Decision Matrix

| Scenario | Strategy | Outcome |
| :--- | :--- | :--- |
| Temporary Spikes | `buffer` | No data loss, increased latency. |
| Critical State Logs | `drop` | Data loss allowed, system stability prioritized. |
| Real-time UI | `latest` | Only last state matters. |
| High-freq Telemetry | `window` / `buffer` | Change granularity to reduce signal overhead. |
