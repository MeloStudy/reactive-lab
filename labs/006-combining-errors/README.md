# LAB-006: Combining & Aggregation Operators 🧩📊

Welcome to Lab 006! In this module, you will learn to orchestrate multiple reactive streams, aggregate data into complex structures, and implement advanced resilience and resource safety patterns.

## 🎯 Learning Objectives
- LO-001: Combine streams eagerly with `merge` (interleaving).
- LO-002: Combine streams sequentially with `concat`.
- LO-003: Pair elements from different sources with `zip`.
- LO-004: **Accumulate state** over time using `scan`.
- LO-005: **Reduce** a stream into a single final value with `reduce`.
- LO-006: **Batch items** into collections using `buffer`.
- LO-007: **Partition items** into sub-streams using `window`.
- LO-008: Handle errors and recovery using `onErrorResume`, `onErrorReturn`, and `retry`.
- LO-009: Implement **Resource Safety** and handle dropped elements with `doOnDiscard`.
- LO-010: Group elements into sub-streams by a key using `groupBy`.

## 🛠️ Scenario Walkthrough

### I. Stream Orchestration
Coordination of multiple asynchronous sources.

1.  **The Dashboard Aggregator** [[Code]](src/main/java/com/reactivelab/orchestration/DashboardService.java) [[Test]](src/test/java/com/reactivelab/orchestration/DashboardServiceTest.java)
    Pair a User Profile (`Mono`) with their Friends count (`Mono`) using `zip`.
2.  **The Social Feed Aggregator** [[Code]](src/main/java/com/reactivelab/orchestration/SocialFeedService.java) [[Test]](src/test/java/com/reactivelab/orchestration/SocialFeedServiceTest.java)
    Fetch multiple feeds simultaneously using `merge` or sequentially using `concat`.

### II. Data Aggregation & State
Summarizing streams into single values or accumulating state.

3.  **The Running Balance** [[Code]](src/main/java/com/reactivelab/orchestration/TransactionTracker.java) [[Test]](src/test/java/com/reactivelab/orchestration/TransactionTrackerTest.java)
    Calculate the cumulative sum of transactions as they happen using `scan`.
4.  **The Final Total** [[Code]](src/main/java/com/reactivelab/orchestration/TotalCalculator.java) [[Test]](src/test/java/com/reactivelab/orchestration/TotalCalculatorTest.java)
    Aggregate all emissions into a single final value once the stream completes using `reduce`.

### III. Batching & Windowing
Reorganizing high-frequency data for efficiency.

5.  **The Batch Processor** [[Code]](src/main/java/com/reactivelab/orchestration/BatchProcessor.java) [[Test]](src/test/java/com/reactivelab/orchestration/BatchProcessorTest.java)
    Group items into `List` batches using `buffer` for bulk operations.
6.  **The Windowed Stream** [[Code]](src/main/java/com/reactivelab/orchestration/WindowProcessor.java) [[Test]](src/test/java/com/reactivelab/orchestration/WindowProcessorTest.java)
    Split a stream into sub-fluxes using `window` for concurrent window processing.

### IV. Resilience & Error Recovery
Building pipelines that survive and recover from failures.

7.  **The Resilient Client** [[Code]](src/main/java/com/reactivelab/orchestration/ResilientClient.java) [[Test]](src/test/java/com/reactivelab/orchestration/ResilientClientTest.java)
    Implement a "Recovery Ladder": `doOnError` (log) -> `onErrorMap` (translate) -> `onErrorReturn` (fallback value) -> `onErrorResume` (failover publisher).
8.  **The Reliable Service** [[Code]](src/main/java/com/reactivelab/orchestration/ReliableService.java) [[Test]](src/test/java/com/reactivelab/orchestration/ReliableServiceTest.java)
    Implement transient error recovery with `retry`.

### V. Advanced Collections & Grouping
Transforming streams into complex Java structures or keyed sub-streams.

9.  **The Advanced Analytics** [[Code]](src/main/java/com/reactivelab/orchestration/CollectionProcessor.java) [[Test]](src/test/java/com/reactivelab/orchestration/CollectionProcessorTest.java)
    Aggregate a stream into lookups using `collectMap` or sorted results with `collectSortedList`.
10. **The Event Grouper** [[Code]](src/main/java/com/reactivelab/orchestration/EventGrouper.java) [[Test]](src/test/java/com/reactivelab/orchestration/EventGrouperTest.java)
    Group a stream of diverse events by their type using `groupBy`.

### VI. Resource Safety
Ensuring no leaks in complex stateful pipelines.

11. **Resource Safety** [[Code]](src/main/java/com/reactivelab/orchestration/ResourceSafetyService.java) [[Test]](src/test/java/com/reactivelab/orchestration/ResourceSafetyTest.java)
    Use `doOnDiscard` to clean up resources when items are filtered or subscriptions are cancelled.

## 🚀 Execution Guide

Run the full validation suite:

```powershell
mvn test -pl labs/006-combining-errors
```

## 🔍 Command Dissection

### `zip(mono1, mono2)`
- **Wait Policy**: Waits for all sources.
- **Cardinality**: Completes when any source completes (shortest-source rule).

### `groupBy(T -> K)`
- **Nature**: Clustering.
- **Output**: `Flux<GroupedFlux<K, T>>`. Each inner flux is a stream for a specific key.

### `collectMap(keyExtractor)`
- **Nature**: Terminal Aggregator.
- **Output**: `Mono<Map<K, T>>`. Emits only after source completion.

### `doOnDiscard(Class, Consumer)`
- **Nature**: Lifecycle Hook.
- **Trigger**: When an item is "dropped" (filter, take, cancel, error). Essential for memory management.

### `retry(n)`
- **Nature**: Terminal Recovery.
- **Action**: Re-subscribes to the upstream if an error occurs.

## 📝 Concept Check (Self-Assessment)

1. **The Zip Trap**: You zip `Flux.range(1, 10)` with `Flux.range(1, 5)`. How many items will the resulting stream emit?
   <details>
   <summary>💡 View Answer</summary>
   **5 items**. `zip` follows the "shortest source" rule.
   </details>

2. **Window vs Buffer**: If you have a high-volume stream and want to start processing as soon as the first item of a group arrives, which one should you use?
   <details>
   <summary>💡 View Answer</summary>
   **`window`**. Unlike `buffer` which waits for the `List` to be full, `window` emits an inner `Flux` immediately.
   </details>

3. **Discard Support**: Why is `doOnDiscard` important when using `buffer`?
   <details>
   <summary>💡 View Answer</summary>
   If the stream is cancelled or errors while a buffer is partially full, the items currently in the buffer would be lost without processing. `doOnDiscard` allows you to clean up or release those items.
   </details>

---
**Next Lab**: [LAB-007: Threading Models & Schedulers](../007-threading-schedulers/README.md)
