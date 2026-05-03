# LAB-007: Threading Models & Schedulers 🧵⚡

Welcome to Lab 007! In this module, you will master the art of execution control. You will learn how to offload heavy tasks, isolate blocking code, and safely propagate state across thread boundaries.

## 🎯 Learning Objectives
- LO-001: Understand the default single-threaded nature of Reactor.
- LO-002: Master `publishOn` for downstream thread switching.
- LO-003: Master `subscribeOn` for upstream (source) thread switching.
- LO-004: Choose the right scheduler (`parallel` vs `boundedElastic`).
- LO-005: Propagate state across threads using the Reactor `Context`.

## 🛠️ Scenario Walkthrough

### 1. The Blocking Sin
You have a legacy method that uses `Thread.sleep()`. Running this on the main thread freezes the app. You will use `subscribeOn(Schedulers.boundedElastic())` to isolate this "sin" in a dedicated pool.

### 2. The CPU Cruncher
Simulate a heavy mathematical calculation. You will use `publishOn(Schedulers.parallel())` to ensure the computation happens on a thread pool optimized for CPU work, leaving the event loop free to handle other requests.

### 3. The Vanishing Identity
Observe how a `Correlation ID` (stored in `ThreadLocal`) is lost when you switch threads. You will refactor the pipeline to use Reactor's `Context` to ensure the ID is available regardless of which thread is executing the code.

## 🚀 Execution Guide

Run the validation suite:

```powershell
mvn test -pl labs/007-threading-schedulers
```

## 🔍 Command Dissection

### `subscribeOn(Schedulers.boundedElastic())`
- **When to use**: When you have blocking I/O (DB, File, Legacy API).
- **Why**: It has a large number of threads and handles blocking gracefully without starving the CPU.

### `contextWrite(Context.of(key, value))`
- **Direction**: Propagates **upstream**.
- **Scope**: Tied to the specific `Subscription`.
- **Note**: This is the reactive-safe replacement for `ThreadLocal`.

## 📝 Scheduler Check (Self-Assessment)

1. **The Race**: I have two `subscribeOn` calls: `subscribeOn(A)` near the source and `subscribeOn(B)` near the subscriber. Which one wins?
   <details>
   <summary>💡 View Answer</summary>
   **`subscribeOn(A)` wins**. The one closest to the source (upstream) defines the execution context for the emission. Multiple `subscribeOn` calls do not "jump" threads; only the first one has effect.
   </details>

2. **The Event Loop**: Why is it a "sin" to run a blocking DB call on `Schedulers.parallel()`?
   <details>
   <summary>💡 View Answer</summary>
   `Schedulers.parallel()` has a fixed number of threads (usually equal to CPU cores). If you block one, you are significantly reducing the system's capacity. If you block all of them, the entire application stops processing. For blocking I/O, always use `boundedElastic()`.
   </details>

3. **The Context**: If I set a value in the context at the bottom of the pipeline using `contextWrite()`, can an operator at the top read it?
   <details>
   <summary>💡 View Answer</summary>
   **Yes**. Signals (onSubscribe, request, cancel) and the Context travel **upstream** (from Subscriber to Publisher). This is why operators can access context data defined further down the chain.
   </details>

---
**Next Lab**: [LAB-008: Backpressure & Flow Control](../008-backpressure-flow/README.md)
