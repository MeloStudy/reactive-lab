# CONCEPT: Combining Streams & Resilience

Mastering a single stream is only half the battle. In real-world systems, you must orchestrate multiple data sources and handle the inevitable failures of distributed systems.

## 1. Stream Orchestration

### `zip(A, B, C)`
- **Behavior**: Pairwise combination. It waits for one item from each source before emitting a tuple/result.
- **Rule of Cardinality**: The resulting stream will only be as long as the **shortest** source. If one source is empty, the result is empty.
- **Best for**: Aggregating related data (e.g., User + AccountInfo).

### `merge(A, B)`
- **Behavior**: Eager combination. It subscribes to all sources at once and emits items as they arrive.
- **Rule of Order**: **Interleaved**. No order is guaranteed.
- **Best for**: Performance and low latency.

### `concat(A, B)`
- **Behavior**: Sequential combination. It subscribes to A, waits for it to complete, then subscribes to B.
- **Rule of Order**: **Strictly Sequential**.
- **Best for**: Task dependencies (e.g., Step 1 then Step 2).

## 2. The Error Channel

In Reactive Streams, an **Error is a Terminal Signal**. Once a publisher emits an `onError` signal, the subscription is cancelled, and the stream is considered "dead". 

### The Recovery Ladder
To build resilient systems, we use operators to "catch" and handle these errors before they terminate the pipeline:

1. **`doOnError` (The Observer)**: 
   - Side-effect only. 
   - Use for logging or metrics. 
   - It DOES NOT stop the error from propagating.

2. **`onErrorReturn` (The Static Fallback)**:
   - Swallows the error and emits a default value.
   - The stream then completes normally.

3. **`onErrorMap` (The Translator)**:
   - Catches an exception and throws a different one.
   - Useful for hiding implementation details (e.g., mapping `SQLException` to `UserNotFoundException`).

4. **`onErrorResume` (The Dynamic Failover)**:
   - The most powerful recovery operator.
   - Swallows the error and switches to an entirely new `Publisher`.
   - Use for calling backup services or fallback logic.

## 3. Resilience Basics: `retry(n)`

`retry` does not "fix" an error; it **re-subscribes** to the source from the beginning.
- **Warning**: This works well for transient errors (network glitch) but can be dangerous for permanent errors (invalid input), as it will just fail again.
- **Rule**: Only retry operations that are **Idempotent**.
