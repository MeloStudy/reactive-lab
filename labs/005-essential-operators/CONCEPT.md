# CONCEPT: Essential Transformation, Filtering & Slicing

Transformation and Filtering are the most common operations in a reactive pipeline. While synchronous transformations are straightforward, asynchronous "flattening" and "slicing" require a deeper understanding of stream control.

## 1. Synchronous vs. Asynchronous

### `map(T -> V)`
A synchronous, functional transformation. It intercepts every item in the stream and applies a 1-to-1 mapping function.
- **Thread Affinity**: By default, it executes on the same thread that emitted the element from the upstream.
- **Pure Function**: The mapping function SHOULD be a pure function (no side effects) to maintain the integrity of the pipeline.
- **Best for**: Data conversion (e.g., mapping a `UserEntity` to a `UserDto`), formatting, or simple computational logic.

### `filter(T -> boolean)`
A synchronous conditional gate that decides which items are allowed to pass through the pipeline.
- **Demand Awareness**: If the filter returns `false`, the item is silently dropped. Crucially, the operator then immediately sends a request for **one more element** from the upstream to satisfy the pending demand of the subscriber.
- **Predicate Requirement**: Use for business rule enforcement (e.g., `user.isActive()`).

## 2. The Flattening Trio: Managing Asynchronous Transformations

When your transformation function returns another **Publisher** (like a `Flux` or `Mono` from a database or remote API), you cannot use a simple `map`. You need a "flattening" operator that subscribes to these inner publishers and merges their emissions back into the main pipeline.

Choosing the right operator depends on your requirements for **concurrency**, **ordering**, and **cancellation**.

### `flatMap(T -> Publisher<V>)`
The most common operator for high-concurrency scenarios.
- **Mechanics**: It subscribes to multiple inner publishers **simultaneously**.
- **Interleaving**: Since inner publishers execute concurrently, their emissions are merged into the main stream as they arrive. This means the original order is **not guaranteed**.
- **Concurrency Control**: By default, it has a prefetch/concurrency limit (usually 256). You can tune this to control how many simultaneous subscriptions are active.
- **Best for**: Scenarios where throughput is prioritized over ordering (e.g., fetching multiple user profiles in parallel).

### `concatMap(T -> Publisher<V>)`
The go-to operator when **ordering is mandatory**.
- **Mechanics**: It subscribes to the first inner publisher and **waits** for it to complete (`onComplete`) before subscribing to the next one.
- **Strict Order**: It preserves the original order of elements perfectly, but it is strictly sequential.
- **Performance**: Slower than `flatMap` because it processes one item at a time, eliminating concurrency.
- **Best for**: Scenarios where tasks have dependencies or must be processed in a specific sequence (e.g., executing database transactions in order).

### `switchMap(T -> Publisher<V>)`
The "Cancellation" operator, perfect for **dynamic/stale data**.
- **Mechanics**: As soon as a new item arrives from the upstream, it **cancels** the current inner subscription and subscribes to the new one immediately.
- **Single Active Stream**: Only the most recent inner publisher's emissions reach the downstream. All previous ones are discarded upon cancellation.
- **Best for**: "Search-as-you-type" or "Latest-only" scenarios. If a user types 'A' and then 'B', we don't care about the results for 'A' anymore; we only want the results for 'B'.

## 3. Slicing the Stream

Sometimes you only need a segment of the stream or unique values.

### `take(n)`
Emits the first `n` items and then **cancels** the upstream subscription. It is a very efficient way to stop a stream early.

### `skip(n)`
Drops the first `n` items and then begins emitting everything else.

### `distinct()`
Filters out duplicate items. It tracks previously seen items in an internal state (usually a `HashSet`).
> [!CAUTION]
> `distinct()` on an infinite stream can lead to high memory consumption as the internal set of seen items grows forever.

## 4. Flux to Mono Transitions (Aggregation)

When you need to collect all items from a `Flux` into a single container, you transition to a `Mono`.

### `collectList()`
Collects all elements into a `java.util.List` and emits it as a `Mono<List<T>>` when the source `Flux` completes.

### `elementAt(index)`
Picks a single element at the given index and emits it as a `Mono<T>`.

## 5. `flatMapIterable`
When your transformation returns an `Iterable` (like a `List`) instead of a `Publisher`, use `flatMapIterable`. It is much more efficient than `flatMap(Flux::fromIterable)` because it avoids the overhead of creating multiple `Flux` instances.
