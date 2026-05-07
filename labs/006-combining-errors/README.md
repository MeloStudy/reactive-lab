# LAB-006: Combining & Aggregation Operators 🧩📊

Welcome to Lab 006! In this module, you will learn to orchestrate multiple reactive streams into a single result and implement basic resilience strategies to handle failures gracefully.

## 🎯 Learning Objectives
- LO-001: Combine streams eagerly with `merge` (interleaving).
- LO-002: Combine streams sequentially with `concat`.
- LO-003: Pair elements from different sources with `zip`.
- LO-004: **Accumulate state** over time using `scan`.
- LO-005: **Reduce** a stream into a single final value with `reduce`.
- LO-006: **Batch items** into collections using `buffer`.
- LO-007: **Partition items** into sub-streams using `window`.
- LO-008: Handle errors and recovery using `onErrorResume` and `retry`.

## 🛠️ Scenario Walkthrough

### 1. The Dashboard Aggregator [[Code]](src/main/java/com/reactivelab/orchestration/DashboardService.java)
Build a user header by zipping together a User Profile and their Friends count. You will observe how `zip` ensures data integrity by waiting for all pieces to arrive.

### 2. The Social Feed Aggregator [[Code]](src/main/java/com/reactivelab/orchestration/SocialFeedService.java)
Explore the difference between eager and lazy combination. Use `merge` to fetch multiple feeds simultaneously (interleaving) or `concat` to ensure a strict sequence (e.g., Cache first, then Remote).

### 3. The Running Balance [[Code]](src/main/java/com/reactivelab/orchestration/TransactionTracker.java)
Calculate the cumulative sum of transactions as they happen. You will use `scan` to emit the updated balance every time a new transaction occurs.

### 4. The Final Total [[Code]](src/main/java/com/reactivelab/orchestration/TotalCalculator.java)
Use `reduce` to aggregate all emissions into a single final value once the stream completes.

### 5. The Batch Processor [[Code]](src/main/java/com/reactivelab/orchestration/BatchProcessor.java)
Group high-frequency data into `List` batches using `buffer`. This is essential for bulk operations like database inserts.

### 6. The Windowed Stream [[Code]](src/main/java/com/reactivelab/orchestration/WindowProcessor.java)
Similar to batching, but instead of Lists, `window` produces sub-streams (`Flux<Flux<T>>`). This allows for concurrent processing of windows without waiting for the full batch to be collected.

### 7. The Resilient Client [[Code]](src/main/java/com/reactivelab/orchestration/ResilientClient.java)
Implement a "Recovery Ladder" for a flaky service:
1. Log the failure with `doOnError`.
2. Translate technical exceptions with `onErrorMap`.
3. Provide a safe default value with `onErrorReturn` or failover with `onErrorResume`.

## 🚀 Execution Guide

Run the validation suite:

```powershell
mvn test -pl labs/006-combining-errors
```

## 🔍 Command Dissection

### `zip(mono1, mono2)`
Creates a `Tuple` of results. 
- **Wait Policy**: Waits for all sources.
- **Cardinality**: Completes when any source completes (shortest-source rule).

### `merge(flux1, flux2)`
Combines multiple streams eagerly.
- **Subscription**: Subscribes to all sources at once.
- **Interleaving**: Items appear as they arrive, regardless of source order.

### `scan(initial, (acc, val) -> ...)`
- **Nature**: Stateful and intermediate.
- **Output**: Emits the current state after each item.

### `reduce(initial, (acc, val) -> ...)`
- **Nature**: Terminal.
- **Output**: Emits a single `Mono` only when the source completes.

### `buffer(n)`
- **Nature**: Grouping.
- **Output**: Converts `Flux<T>` to `Flux<List<T>>`.

### `onErrorResume(e -> backupPublisher)`
The ultimate safety net. 
- It intercepts the error signal.
- It cancels the original failed subscription.
- It starts a new subscription to the `backupPublisher`.

## 📝 Resilience Check (Self-Assessment)

1. **The Zip Trap**: You zip `Flux.range(1, 10)` with `Flux.range(1, 5)`. How many items will the resulting stream emit?
   <details>
   <summary>💡 View Answer</summary>
   **5 items**. `zip` follows the "shortest source" rule. Once the second flux completes at 5, zip has no more pairs to produce and completes.
   </details>

2. **Eager vs Lazy**: If you use `concat` to join a fast source and a slow source, does the fast source start immediately?
   <details>
   <summary>💡 View Answer</summary>
   **Only if it is the FIRST source**. If the slow source is first, `concat` will wait for it to complete before even subscribing to the fast one. If you want both to run in parallel, use `merge`.
   </details>

3. **Logging vs Handling**: Does `doOnError` stop the error from reaching the final subscriber?
   <details>
   <summary>💡 View Answer</summary>
   **No**. `doOnError` is for side-effects only (logging, metrics). The error signal will continue to move downstream until it is handled by a recovery operator or terminates the subscription.
   </details>

---
**Next Lab**: [LAB-007: Threading Models & Schedulers](../007-threading-schedulers/README.md)
