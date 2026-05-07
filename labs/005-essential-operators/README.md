# LAB-005: Essential Transformation & Filtering 🔄

Welcome to Lab 005! This is where you learn to shape and transform data as it flows through your pipelines. You will master the differences between synchronous mapping and asynchronous flattening.

## 🎯 Learning Objectives
- LO-001: Implement 1-to-1 data transformations using `map`.
- LO-002: Filter emissions based on predicates using `filter`.
- LO-003: Understand asynchronous flattening with `flatMap`, `concatMap`, and `switchMap`.
- LO-004: Apply **Filtering & Slice** operators (`take`, `skip`, `distinct`).
- LO-005: Perform basic **Flux-to-Mono** transitions using `collectList`.

## 🛠️ Scenario Walkthrough

### 1. The User Sanitizer [[Code]](src/main/java/com/reactivelab/operators/UserSanitizer.java)
Clean up a stream of messy user input. You will use `map` to trim and lowercase strings, and `filter` to remove entries that are too short.

### 2. The Async Enricher [[Code]](src/main/java/com/reactivelab/operators/UserEnricher.java)
For each User ID, you need to fetch their profile from a database. Since the database call is asynchronous, you'll use `flatMap` to merge the results back into the main stream.

### 3. The Ordered Task Runner [[Code]](src/main/java/com/reactivelab/operators/TaskRunner.java)
When order is paramount (like processing transactions), you'll use `concatMap` to ensure that Task B never starts until Task A is completely finished.

### 4. The Search Debouncer [[Code]](src/main/java/com/reactivelab/operators/SearchDebouncer.java)
Simulate an autocomplete search. When a user types rapidly, you don't want to show results for stale queries. You'll use `switchMap` to cancel the previous search as soon as a new query arrives.

### 5. The Data Slicer [[Code]](src/main/java/com/reactivelab/operators/DataStreamSlicer.java)
Learn to extract specific segments of a stream. You will use `skip` to ignore initial noisy data, `take` to limit the result set, and `distinct` to ensure no duplicates reach the end.

### 6. The Batch Collector [[Code]](src/main/java/com/reactivelab/operators/CollectionCollector.java)
Sometimes you need to gather all reactive emissions into a traditional Java collection. You'll use `collectList` to transition from a `Flux` to a `Mono<List<T>>`.

## 🚀 Execution Guide

Run the validation tests:

```powershell
mvn test -pl labs/005-essential-operators
```

## 🔍 Command Dissection

### `flatMap(id -> service.call(id))`
- **Concurrency**: Default 256.
- **Prefetch**: Default 256.
- **Signal Flow**: When an item arrives, it triggers an inner subscription. Multiple inner subscriptions run in parallel.

### `switchMap(query -> search(query))`
- **Cancellation**: Immediately cancels the previous inner subscription when a new item arrives.
- **Use Case**: UI interactions, autocompletes, or any scenario where only the *latest* signal is relevant.

### `take(n)` / `skip(n)`
- `take(n)`: Emits `n` items and **cancels** upstream.
- `skip(n)`: Ignores the first `n` items.

### `collectList()`
- **Transition**: Converts `Flux<T>` into `Mono<List<T>>`.
- **Trigger**: Only emits when the source Flux sends the `onComplete` signal.

## 📝 Operator Check (Self-Assessment)

1. **Order vs. Performance**: I have 1000 items and a service that takes 100ms. I don't care about the order of results. Which operator should I use?
   <details>
   <summary>💡 View Answer</summary>
   **`flatMap`**. It will process items concurrently (up to 256 by default), making it significantly faster than `concatMap`, which would take 100 seconds (1000 * 100ms).
   </details>

2. **The Autocomplete Race**: A user types "R", then "Re". The search for "R" is still running when "Re" is sent. What happens if I use `switchMap`?
   <details>
   <summary>💡 View Answer</summary>
   `switchMap` will immediately send a **cancellation signal** to the "R" search and subscribe to the "Re" search. This prevents "stale" data from appearing in the UI.
   </details>

3. **Prefetching**: If I use `flatMap` with a source of 1000 items, and I only `take(1)` from the final Flux, how many items were requested from the source?
   <details>
   <summary>💡 View Answer</summary>
   **256** (by default). Even though you only needed 1, `flatMap` requests its full prefetch amount as soon as it's subscribed to. To change this, you must specify the `prefetch` parameter.
   </details>

---
**Next Lab**: [LAB-006: Combining & Aggregation Operators](../006-combining-errors/README.md)
