# LAB-005: Essential Transformation Operators 🔄

Welcome to Lab 005! This is where you learn to shape and transform data as it flows through your pipelines. You will master the differences between synchronous mapping and asynchronous flattening.

## 🎯 Learning Objectives
- LO-001: Implement 1-to-1 data transformations using `map`.
- LO-002: Filter emissions based on predicates using `filter`.
- LO-003: Understand asynchronous flattening with `flatMap` and the concept of **Concurrency** and **Prefetch**.
- LO-004: Compare `flatMap` (interleaving) vs `concatMap` (sequential) behavior.
- LO-005: Implement "latest-only" logic using `switchMap`.

## 🛠️ Scenario Walkthrough

### 1. The User Sanitizer
Clean up a stream of messy user input. You will use `map` to trim and lowercase strings, and `filter` to remove entries that are too short.

### 2. The Async Enricher
For each User ID, you need to fetch their profile from a database. Since the database call is asynchronous, you'll use `flatMap` to merge the results back into the main stream.

### 3. The Ordered Task Runner
When order is paramount (like processing transactions), you'll use `concatMap` to ensure that Task B never starts until Task A is completely finished.

### 4. The Search Debouncer
Simulate a modern UI search bar. When a user types quickly, you only want the results for the *last* keystroke. `switchMap` will handle the cancellation of obsolete search requests for you.

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

### `switchMap(id -> service.call(id))`
- **Signal Flow**: When a new item arrives, it sends a `cancel()` signal to the active inner subscription before starting the next one.

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
**Next Lab**: [LAB-006: Combining & Basic Error Handling](../006-combining-errors/README.md)
