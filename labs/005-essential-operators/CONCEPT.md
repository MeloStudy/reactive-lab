# CONCEPT: Essential Transformation Operators

Transformation is the most common operation in a reactive pipeline. While synchronous transformations are straightforward, asynchronous "flattening" requires deep understanding of how signals are merged and managed.

## 1. Synchronous vs. Asynchronous

### `map(T -> V)`
A synchronous 1-to-1 transformation. It receives a value and returns a new value.
- **Rule**: Must be a pure function.
- **Thread Affinity**: Executes on the same thread that emitted the element.
- **Best for**: Formatting, simple logic, mapping to DTOs.

### `filter(T -> boolean)`
A synchronous filter. If it returns false, the item is dropped, and the operator requests one more from upstream to maintain demand.

## 2. The Flattening Trio (flatMap, concatMap, switchMap)

When you need to transform an item into another **Publisher** (e.g., calling an async DB or Web service), you cannot use `map`. You need an operator that "flattens" the inner stream into the main pipeline.

### `flatMap` (Concurrent & Interleaved)
- **Behavior**: Subscribes to inner publishers as they arrive, up to a `concurrency` limit.
- **Asynchrony**: Truly non-blocking. It doesn't wait for one result to return before requesting the next.
- **Ordering**: **NOT guaranteed**. Faster inner streams will "overtake" slower ones.
- **Best for**: Performance and maximum throughput where order doesn't matter.

### `concatMap` (Sequential & Ordered)
- **Behavior**: Subscribes to the first inner publisher and **waits** for it to complete (`onComplete`) before subscribing to the next.
- **Ordering**: **Guaranteed** to match the source order.
- **Best for**: Sequential tasks (e.g., dependent DB updates) or when ordering is a business requirement.

### `switchMap` (Latest-only & Cancellation)
- **Behavior**: When a new item arrives from the source, it **immediately cancels** the previous inner subscription and starts the new one.
- **Best for**: Scenarios where only the latest data is relevant (e.g., search-as-you-type, autocomplete).

## 3. Prefetch & Concurrency

Flattening operators don't just "merge" streams; they manage buffers and demand.

- **Concurrency**: The maximum number of active inner subscriptions allowed at once.
- **Prefetch**: The number of elements requested from the upstream source in advance to keep the internal queue populated.

> [!NOTE]
> By default, `flatMap` has a prefetch of **256**. This means it will eagerly request 256 items from the source to maximize throughput, potentially overwhelming downstream if not handled correctly.

## 4. `flatMapIterable`
When your transformation returns an `Iterable` (like a `List`) instead of a `Publisher`, use `flatMapIterable`. It is much more efficient than `flatMap(Flux::fromIterable)` because it avoids the overhead of creating multiple `Flux` instances.
