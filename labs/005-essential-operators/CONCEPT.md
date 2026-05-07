# CONCEPT: Essential Transformation, Filtering & Slicing

Transformation and Filtering are the most common operations in a reactive pipeline. While synchronous transformations are straightforward, asynchronous "flattening" and "slicing" require a deeper understanding of stream control.

## 1. Synchronous vs. Asynchronous

### `map(T -> V)`
A synchronous 1-to-1 transformation. It receives a value and returns a new value.
- **Thread Affinity**: Executes on the same thread that emitted the element.
- **Best for**: Formatting, simple logic, mapping to DTOs.

### `filter(T -> boolean)`
A synchronous filter. If it returns false, the item is dropped, and the operator requests one more from upstream to maintain demand.

## 2. The Flattening Trio (flatMap, concatMap, switchMap)

When you need to transform an item into another **Publisher** (e.g., calling an async service), you need an operator that "flattens" the inner stream.

| Operator | Subscription Type | Emission Order | Best For |
| :--- | :--- | :--- | :--- |
| **`flatMap`** | Simultaneous | Interleaved | Maximum throughput. |
| **`concatMap`** | Sequential | Strict Order | Task dependencies. |
| **`switchMap`** | Latest-only | Cancellation | Search-as-you-type. |

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
