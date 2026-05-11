# CONCEPT: Combining & Aggregation Operators

Mastering a single stream is only half the battle. In real-world systems, you must orchestrate multiple data sources and aggregate data into meaningful results.

## 1. Stream Orchestration: Combining Multiple Sources

Orchestration is the art of coordinating multiple asynchronous streams. Depending on whether you need to pair related data, maximize throughput, or ensure a strict sequence, you must choose the correct operator.

### `zip(Publisher<A>, Publisher<B>, ...)`
The **Pairing** operator.
- **Mechanics**: It subscribes to all sources simultaneously and waits for an item to be emitted from **each** source. Once it has a "set", it combines them into a `Tuple` or a custom result.
- **The "Slowest Source" Rule**: The throughput of a `zip` operation is strictly limited by the slowest publisher. If one stream emits 10 items/sec and another emits 1 item/sec, `zip` will only emit 1 item/sec.
- **Best for**: Aggregating related data that belongs together (e.g., combining a `User` stream with an `Account` stream where IDs match).

### `merge(Publisher<T>...)`
The **Interleaving** operator.
- **Mechanics**: It subscribes to all upstreams **simultaneously** and passes their emissions to the downstream as they arrive.
- **No Order Guarantee**: Emissions from different sources are interleaved. If source A emits at time 1 and source B emits at time 2, you will see A then B.
- **Eager Execution**: All sources start producing immediately.
- **Best for**: Performance-critical scenarios where you want to process data from multiple sources as fast as possible.

### `concat(Publisher<T>...)`
The **Sequential** operator.
- **Mechanics**: It subscribes to the first source and **waits for it to complete** before subscribing to the second source.
- **Total Order**: It preserves the absolute order of sources. You will never see an item from the second source until the first one is entirely finished.
- **Best for**: Scenarios with dependencies or tiered logic (e.g., Cache first, then Remote).

---

## 2. Aggregation & State Management

Reactive streams are often stateless, but sometimes you need to carry state forward or summarize data.

### `scan(initial, (acc, next) -> ...)`
The **Intermediate Accumulator**. It applies a function to each item and emits the **cumulative state** at every step.
- **Type**: It always returns a `Flux`.
- **Use Case**: Real-time dashboards, running balances, or UI progress bars.

### `reduce(initial, (acc, next) -> ...)`
The **Terminal Aggregator**. It applies a function to each item but **buffers the state** and only emits the final value when the source completes.
- **Type**: It always returns a `Mono`.
- **Use Case**: Calculating a final grand total or an average.

---

## 3. Advanced Grouping & Collections

Sometimes you need to reorganize the stream into complex structures or group related items.

### `groupBy(T -> K)`
The **Clustering** operator.
- **Mechanics**: It splits the main `Flux<T>` into a `Flux<GroupedFlux<K, T>>`. Each `GroupedFlux` corresponds to a unique key.
- **Parallelism**: You can process different groups in parallel.
- **Warning**: Be careful with "High Cardinality" keys (e.g., grouping by unique IDs), as each group consumes memory for its internal state.

### `collectMap(T -> K)`
- **Nature**: Terminal.
- **Output**: `Mono<Map<K, T>>`. 
- **Use Case**: Transforming a stream of results into a lookup table.

### `collectSortedList(Comparator<T>)`
- **Nature**: Terminal.
- **Output**: `Mono<List<T>>`.
- **Use Case**: Gathering all results and sorting them before presenting to the final subscriber.

---

## 4. Batching & Windowing: Grouping for Efficiency

Processing items one-by-one is not always optimal. Sometimes you need to group items for bulk operations.

### `buffer(n)` (Batching)
`buffer` collects incoming items into a `List` and emits that list as a single unit once the size `n` is reached.
- **Trade-off**: Memory-intensive. The entire batch must reside in memory.

### `window(n)` (Windowing)
`window` is the "streaming" version of batching. Instead of a `List`, it emits an **inner Flux**.
- **Efficiency**: Ideal for high-volume streams where holding thousands of items in a `List` would be prohibitive.

---

## 5. The Error Channel: Terminal Signals & Recovery

In Project Reactor, an error is a **terminal signal**. By default, when an exception occurs, the subscription is cancelled.

### The Recovery Strategies

#### 1. Side Effects: `doOnError(Consumer<Throwable>)`
- **Goal**: Logging or metrics. The stream **remains failed**.

#### 2. Static Fallback: `onErrorReturn(T)`
- **Goal**: Providing a safe default value. The stream **completes normally**.

#### 3. Dynamic Failover: `onErrorResume(Throwable -> Publisher<T>)`
- **Goal**: Switching to a backup service or secondary source.

#### 4. Re-subscription: `retry(n)`
- **Nature**: Re-starts the pipeline from the beginning.
- **Use Case**: Transient network glitches. Be careful with non-idempotent operations.

---

## 6. Resource Safety: Discard Support

In high-performance systems, we must clean up resources associated with data that never reaches the subscriber.

### `doOnDiscard(Class<T>, Consumer<T>)`
This operator acts as a "trash collector" hook. It is triggered when an element is "discarded" by an operator before it can reach the final subscriber.

**Common "Discard" Scenarios**:
1.  **Filtering**: If an item is rejected by a `filter`, it is discarded.
2.  **Cancellation**: If a subscriber cancels while a `buffer` is partially full, those items are discarded.
3.  **Errors**: If an error occurs, any buffered items are discarded.

> [!TIP]
> Always use `doOnDiscard` when working with `buffer`, `window`, or any operator that holds state, especially if the data objects (like ByteBufs) require manual cleanup.
