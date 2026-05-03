# LAB-006: Combining & Basic Error Handling 🧩⚠️

Welcome to Lab 006! In this module, you will learn to orquestrate multiple reactive streams into a single result and implement basic resilience strategies to handle failures gracefully.

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

### 2. The Resilient Service
Implement a "Recovery Ladder" for a flaky service:
1. Log the failure with `doOnError`.
2. Translate the technical stack trace with `onErrorMap`.
3. Provide a safe default value with `onErrorReturn`.

### 3. The Failover Strategy
Simulate a high-availability system. When the primary source fails, use `onErrorResume` to automatically switch to a backup data source.

### 4. The Flaky Network
Use `retry` to overcome transient failures. You will verify exactly how many times the source is re-subscribed to before succeeding.

## 🚀 Execution Guide

Run the validation suite:

```powershell
mvn test -pl labs/006-combining-errors
```

## 🔍 Command Dissection

### `zip(mono1, mono2)`
Creates a `Tuple` of results. 
- **Wait Policy**: Waits for all sources.
- **Completion Policy**: Completes when any source completes (or as soon as it has a complete set of pairs).

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

2. **Logging vs Handling**: Does `doOnError` stop the error from reaching the final subscriber?
   <details>
   <summary>💡 View Answer</summary>
   **No**. `doOnError` is for side-effects only (logging, metrics). The error signal will continue to move downstream until it is handled by a recovery operator like `onErrorReturn` or terminates the subscription.
   </details>

3. **Retry Dangers**: Why shouldn't you use `retry()` on a `POST` request that creates a user in a database?
   <details>
   <summary>💡 View Answer</summary>
   If the first request reached the DB but the *response* failed, a `retry()` would attempt to create the user AGAIN. Unless the operation is **idempotent** (safe to repeat), retrying can cause duplicate data or state corruption.
   </details>

---
**Next Lab**: [LAB-007: Threading Models & Schedulers](../007-threading-schedulers/README.md)
