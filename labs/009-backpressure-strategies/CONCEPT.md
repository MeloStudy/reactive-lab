# CONCEPT: Backpressure Strategies & Rate Limiting

In a reactive system, producers and consumers often operate at different speeds. **Backpressure** is the feedback mechanism that allows a slow consumer to tell a fast producer to slow down, preventing resource exhaustion and system crashes.

## 1. Pull vs. Push: The Conflict

Reactive Streams are designed as a **Pull-based** model where the `Subscriber` controls the flow:
- **Demand Propagation**: The `Subscriber` sends a `request(n)` signal upstream.
- **Compliance**: The `Publisher` is obligated to send *no more* than `n` items until the next request signal.

However, some data sources are naturally **Push-based** (e.g., mouse movements, real-time sensor data, or high-volume multicasters like `Sinks.Many`). These sources ignore demand, creating a conflict that must be resolved with **Overflow Strategies**.

## 2. Overflow Strategies (The Shield)

When a push-based source is too fast for the downstream, you must explicitly handle the excess data using one of these strategies:

### `onBackpressureBuffer`
Stores items in an internal queue until the subscriber is ready.
- **Best for**: Smoothing out temporary spikes in traffic where every data point is valuable.
- **The Overflow Strategy**: Buffers are not infinite. You can configure what happens when the buffer is full:
    - **ERROR**: Throw a `BackpressureOverflowException` (default).
    - **DROP_OLDEST**: Discard the oldest item in the buffer to make room for the new one.
    - **DROP_LATEST**: Discard the incoming item and keep the buffer as is.

### `onBackpressureDrop`
Immediately discards any item emitted while the subscriber has zero outstanding demand.
- **Best for**: Non-critical telemetry or logs where system stability is more important than data completeness.
- **Hook**: You can provide a callback (e.g., `onBackpressureDrop(item -> log.warn("Dropped: " + item))`) to track data loss.

### `onBackpressureLatest`
Keeps only the very last item emitted, overwriting any previous buffered item.
- **Best for**: Real-time state indicators (e.g., current stock price, vehicle speed) where only the most recent value is relevant.

## 3. Rate Limiting (The Regulator)

**Rate Limiting** proactively manages the **prefetch** behavior of the pipeline to protect downstream resources.

### `limitRate(highRequest, lowRequest)`
Sets the maximum number of items the operator will request from upstream at once.
- **The 75% Rule**: If only `highRequest` is provided, Reactor uses a "replenishment threshold". It requests `highRequest` items, and only when the subscriber has consumed **75%** of them (leaving 25% in the buffer), it requests the next batch to refill.
- **Low Tide**: You can explicitly set the `lowRequest` (low tide) to adjust when replenishment happens. For example, `limitRate(100, 10)` will only request more when only 10 items are left.
- **Why?**: This prevents "Request Storms" and ensures a steady, predictable flow of data, protecting external resources from bursty demand.

### `limitRequest(n)`
Enforces a hard limit on the **total** number of items that can be requested. Once the limit is reached, the stream emits a `onComplete` signal, effectively severing the connection.

## 4. The Request Propagation Chain

Backpressure works because the `request(n)` signal travels **upstream**. Consider this chain:

1. **Subscriber**: Calls `request(1)`.
2. **`limitRate(10)`**: Intercepts this. Since its internal buffer is empty, it sends `request(10)` to the source.
3. **Source**: Emits 10 items as they become available.
4. **`limitRate`**: Buffers those 10 items. It delivers the `1` item requested by the subscriber.
5. **Subscriber**: After processing, calls `request(7)`.
6. **`limitRate`**: Delivers those 7 items from its buffer. Total items delivered: 8.
7. **Replenishment**: Having delivered 80% (>= 75%), `limitRate` sends a new `request(8)` to the source to refill its internal buffer.

> [!CAUTION]
> Operators like `publishOn` have a default internal buffer of **256**. If your downstream processing is slower than the upstream emission, this buffer will fill up, and the operator will stop requesting items from the upstream, triggering backpressure naturally.

- **Context Switching**: Reactive operators are highly optimized for minimal context switching. In high-density pipelines, the overhead of virtual thread management can still exceed the efficiency of a well-tuned event loop.

## 6. Backpressure vs. Virtual Threads (Project Loom)

A common misconception is that **Virtual Threads** eliminate the need for reactive programming and backpressure. This is false.

- **Resource Protection**: Even if you can have millions of virtual threads, your database or external API cannot handle millions of concurrent requests. Backpressure remains the essential tool for **throttling** and protecting these downstream systems.
- **Memory Pressure**: If a producer sends 1 million items to a slow consumer running on a virtual thread, and there's no backpressure, those 1 million items will sit in memory, potentially causing an `OutOfMemoryError`.
- **Hybrid Approach**: Virtual threads are excellent for handling **blocking I/O** within a reactive pipeline (e.g., using `publishOn` with a virtual thread executor), while reactive operators provide the **orchestration and flow control** layer.
