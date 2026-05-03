# CONCEPT: Essential Transformation Operators

Transformation is the most common operation in a reactive pipeline. While synchronous transformations are straightforward, asynchronous "flattening" requires deep understanding of how signals are merged and managed.

## 1. Synchronous vs. Asynchronous

### `map(T -> V)`
A synchronous 1-to-1 transformation. It receives a value and returns a new value.
- **Rule**: Must be a pure function.
- **Best for**: Formatting, simple logic, mapping to DTOs.

### `filter(T -> boolean)`
A synchronous filter. If it returns false, the item is dropped, and the operator requests one more from upstream to maintain demand.

## 2. The Flattening Trio (flatMap, concatMap, switchMap)

When you need to transform an item into another **Publisher** (e.g., calling an async DB or Web service), you cannot use `map`. You need an operator that "flattens" the inner stream into the main pipeline.

### `flatMap` (Concurrent & Interleaved)
- **Behavior**: Subscribes to inner publishers as they arrive.
- **Ordering**: **NOT guaranteed**. If Inner B is faster than Inner A, B will emit first.
- **Best for**: Performance and maximum throughput.

### `concatMap` (Sequential & Ordered)
- **Behavior**: Subscribes to the first inner publisher and **waits** for it to complete before subscribing to the next.
- **Ordering**: **Guaranteed**.
- **Best for**: Tasks that must happen in a specific sequence (e.g., DB updates).

### `switchMap` (Latest-only & Cancellation)
- **Behavior**: When a new item arrives from the source, it **cancels** the previous inner subscription and starts the new one.
- **Best for**: Autocomplete, search bars, or any scenario where the latest data renders previous data obsolete.

## 3. Prefetch & Concurrency

 flattening operators don't just "merge" streams; they manage buffers.

- **Concurrency**: How many inner publishers can be active at the same time.
- **Prefetch**: How many items the operator requests from the source *before* it actually needs them, to keep its internal buffer full.

By default, `flatMap` has a prefetch of **256**. This means it will request 256 items from the source as soon as it is subscribed to, even if it hasn't processed one yet!

## 4. `flatMapIterable`
When your transformation returns an `Iterable` (like a `List`) instead of a `Publisher`, use `flatMapIterable`. It is much more efficient than `flatMap(Flux::fromIterable)` because it avoids the overhead of creating multiple `Flux` instances.
