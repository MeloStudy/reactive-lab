# CONCEPT: Combining & Aggregation Operators

Mastering a single stream is only half the battle. In real-world systems, you must orchestrate multiple data sources and aggregate data into meaningful results.

## 1. Stream Orchestration (Combination)

| Operator | Subscription Type | Emission Order | Best For |
| :--- | :--- | :--- | :--- |
| **`zip`** | Simultaneous | Paired (Tuple) | Aggregating related data for a single object. |
| **`merge`** | Simultaneous | Interleaved | Performance/Latency when order is irrelevant. |
| **`concat`** | Sequential | Strict Order | Dependent steps or cache-then-remote logic. |

## 2. Aggregation & State Management

Reactive streams are often stateless, but sometimes you need to carry state forward or summarize data.

### `scan(initial, (acc, next) -> ...)`
The **Accumulator**. It applies a function to each item and emits the **intermediate result** at every step.
- **Use Case**: Running totals, live balances, or cumulative logs.
- **Emission**: If source emits 10 items, `scan` emits 10 items.

### `reduce(initial, (acc, next) -> ...)`
The **Summarizer**. It applies a function to each item but **only emits the final result** once the source stream completes.
- **Use Case**: Final sums, averages, or max values.
- **Emission**: Always emits exactly **one** item (as a `Mono`).

## 3. Batching & Windowing (Grouping)

Sometimes you want to process items in groups rather than individually.

### `buffer(n)` vs `window(n)`
While they look similar, their memory footprint and backpressure behavior differ significantly:

- **`buffer(n)`**: Is **blocking at the collection level**. It must hold all `n` items in memory as a `List` before emitting. If `n` is large or items are big, this can lead to `OutOfMemoryError`.
- **`window(n)`**: Is **streaming**. It emits the inner `Flux` immediately. Items are pushed into the inner Flux as they arrive. This allows for lower latency and better memory management, as the subscriber can start processing the first item of a window before the last item has even been emitted by the source.

| Feature | `buffer` | `window` |
| :--- | :--- | :--- |
| **Output Type** | `Flux<List<T>>` | `Flux<Flux<T>>` |
| **Memory** | High (buffers entire list) | Low (streams items) |
| **Latency** | High (waits for full batch) | Low (immediate inner emission) |
| **Complexity** | Simple | High (requires nested sub) |

## 4. The Error Channel & Recovery

Errors in Reactive Streams are **Terminal Signals**. Once an `onError` is sent, the stream is dead.

### The Recovery Ladder
1. **`doOnError`**: Side-effect only (logging).
2. **`onErrorReturn`**: Static fallback value.
3. **`onErrorResume`**: Dynamic failover to a new `Publisher`.
4. **`retry(n)`**: Transient error recovery by re-subscribing.

> [!IMPORTANT]
> A recovered stream is technically a **new subscription**. The old one is gone.
