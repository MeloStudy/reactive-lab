# LAB-006: Combining & Basic Error Handling 🧩⚠️

Welcome to Lab 006! In this module, you will learn to orchestrate multiple reactive streams into a single result and implement basic resilience strategies to handle failures gracefully.

## 🎯 Learning Objectives
- LO-001: Combine streams eagerly with `merge` and understanding interleaving.
- LO-002: Combine streams sequentially with `concat`.
- LO-003: Pair elements from different sources with `zip`.
- LO-004: Handle errors with side-effects using `doOnError`.
- LO-005: Implement recovery fallbacks using `onErrorReturn` and `onErrorResume`.
- LO-006: Translate exceptions using `onErrorMap`.
- LO-007: Apply basic transient error recovery with `retry`.

## 🛠️ Scenario Walkthrough

### 1. The Dashboard Aggregator
Build a user header by zipping together a User Profile and their Friends count. You will observe how `zip` ensures data integrity by waiting for all pieces to arrive.
- **Code**: [DashboardService.java](./src/main/java/com/reactivelab/orchestration/DashboardService.java)
- **Test**: [DashboardServiceTest.java](./src/test/java/com/reactivelab/orchestration/DashboardServiceTest.java)

### 2. The Social Feed Service
Orchestrate data from multiple sources with different performance profiles.
- **Eager (merge)**: Combine Twitter and Instagram feeds for low latency. Items will be interleaved.
- **Sequential (concat)**: Load a local cache before refreshing from a remote API.
- **Code**: [SocialFeedService.java](./src/main/java/com/reactivelab/orchestration/SocialFeedService.java)
- **Test**: [SocialFeedServiceTest.java](./src/test/java/com/reactivelab/orchestration/SocialFeedServiceTest.java)

### 3. The Resilient Client
Implement a "Recovery Ladder" for a flaky service:
1. Log the failure with `doOnError`.
2. Translate technical exceptions with `onErrorMap`.
3. Provide a safe default value with `onErrorReturn` or failover with `onErrorResume`.
- **Code**: [ResilientClient.java](./src/main/java/com/reactivelab/orchestration/ResilientClient.java)
- **Test**: [ResilientClientTest.java](./src/test/java/com/reactivelab/orchestration/ResilientClientTest.java)

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

### `concat(flux1, flux2)`
Combines multiple streams sequentially.
- **Subscription**: Subscribes to `flux2` ONLY after `flux1` completes.
- **Order**: Guarantees all items from `flux1` appear before `flux2`.

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
