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
- **Best for**: Performance-critical scenarios where you want to process data from multiple sources (like redundant sensors or multiple regional caches) as fast as possible, and the relative order between sources is irrelevant.

### `concat(Publisher<T>...)`
The **Sequential** operator.
- **Mechanics**: It subscribes to the first source and **waits for it to complete** before subscribing to the second source.
- **Total Order**: It preserves the absolute order of sources. You will never see an item from the second source until the first one is entirely finished.
- **Lazy Subscription**: Subsequent sources are not even subscribed to until their turn comes.
- **Best for**: Scenarios with dependencies or tiered logic (e.g., "Check local cache, and ONLY if it's empty or completes, fetch from the remote API").

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

## 3. Batching & Windowing: Grouping for Efficiency

Processing items one-by-one is not always optimal. Sometimes you need to group items to perform bulk operations (e.g., batch database inserts) or time-based analysis.

### `buffer(n)` (Batching)
`buffer` collects incoming items into a `List` and emits that list as a single unit once the size `n` is reached.
- **Trade-off**: It is **memory-intensive**. The entire batch must reside in memory before it can be processed. 
- **Backpressure**: It provides a form of "chunking" that can help downstream consumers process items in larger, more efficient blocks.

### `window(n)` (Windowing)
`window` is the "streaming" version of batching. Instead of a `List`, it emits an **inner Flux**.
- **Non-Blocking**: The inner Flux is emitted immediately as soon as the first item of a window is available.
- **Parallelism**: It allows the downstream to start processing the *contents* of a window while the source is still producing the rest of the items for that same window.
- **Memory Efficiency**: Ideal for high-volume streams where holding thousands of items in a `List` would be prohibitive.

## 4. The Error Channel: Terminal Signals & Recovery

In Project Reactor, an error is a **terminal signal**. By default, when an exception occurs, the subscription is cancelled, and the error propagates downstream until it is handled or reaches the final subscriber.

### The Recovery Strategies

#### 1. Side Effects: `doOnError(Consumer<Throwable>)`
Use this when you want to "peek" at the error without stopping its propagation.
- **Goal**: Logging, metrics, or external alerts.
- **State**: The stream **remains failed** and will terminate after this call.

#### 2. Static Fallback: `onErrorReturn(T)`
The simplest recovery. It replaces the error signal with a default value.
- **Goal**: Providing a safe "zero" or "empty" value.
- **State**: The stream **completes normally** after emitting the fallback value.

#### 3. Dynamic Failover: `onErrorResume(Throwable -> Publisher<T>)`
The most powerful recovery tool. It catches the error and **switches** the subscriber to a completely different pipeline.
- **Goal**: Fetching from a secondary database, calling a backup service, or returning an empty `Flux` (`Flux.empty()`).
- **State**: The subscriber is transparently moved to the new publisher.

#### 4. Re-subscription: `retry(n)`
A "brute force" recovery for transient errors (like network glitches).
- **Goal**: Re-subscribing to the upstream exactly `n` times.
- **Warning**: Be careful with non-idempotent operations; `retry` re-executes everything from the subscription point.

> [!IMPORTANT]
> Error recovery operators (`return`, `resume`) effectively **"swallow"** the error and convert it into a normal `onNext` + `onComplete` sequence, allowing the downstream to continue as if nothing happened.
