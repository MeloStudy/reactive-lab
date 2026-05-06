# LAB-007: Threading Models & Schedulers 🧵⚡

Welcome to Lab 007! In this module, you will master the art of execution control. You will learn how to offload heavy tasks, isolate blocking code, and safely propagate state across thread boundaries using modern Java 21+ features.

## 🎯 Learning Objectives
- LO-001: Understand the default single-threaded nature of Reactor.
- LO-002: Master `publishOn` for downstream thread switching.
- LO-003: Master `subscribeOn` for upstream (source) thread switching.
- LO-004: Choose the right scheduler (`parallel`, `boundedElastic`, `Virtual Threads`).
- LO-005: Propagate state across threads using the Reactor `Context`.
- LO-006: Understand the **Scheduler Trap** (Immutable Upstream).

## 🛠️ Scenario Walkthrough

### 1. The Blocking Sin
You have a legacy method that uses `Thread.sleep()`. Running this on the main thread freezes the app. 
- **Legacy Fix**: Use `subscribeOn(Schedulers.boundedElastic())`.
- **Modern Fix**: Use `subscribeOn(Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()))`.
- **Code**: [BlockingService.java](./src/main/java/com/reactivelab/threading/BlockingService.java)
- **Test**: [BlockingServiceTest.java](./src/test/java/com/reactivelab/threading/BlockingServiceTest.java)

### 2. The CPU Cruncher
Simulate a heavy mathematical calculation. You will use `publishOn(Schedulers.parallel())` to ensure the computation happens on a pool optimized for CPU work.
- **Code**: [ComputeService.java](./src/main/java/com/reactivelab/threading/ComputeService.java)
- **Test**: [ComputeServiceTest.java](./src/test/java/com/reactivelab/threading/ComputeServiceTest.java)

### 3. The Vanishing Identity
Observe how a `Correlation ID` (stored in `ThreadLocal`) is lost when you switch threads. You will refactor the pipeline to use Reactor's `Context`.
- **Code**: [ContextualService.java](./src/main/java/com/reactivelab/threading/ContextualService.java)
- **Test**: [ContextualServiceTest.java](./src/test/java/com/reactivelab/threading/ContextualServiceTest.java)

### 4. The Scheduler Trap
Demonstrate that multiple `subscribeOn` calls do not work as some might expect. Only the one closest to the source defines the execution context for emission.

## 🚀 Execution Guide

Run the validation suite:

```powershell
mvn test -pl labs/007-threading-schedulers
```

## 🔍 Command Dissection

### `subscribeOn(Schedulers.boundedElastic())`
- **Impact**: Upstream (Source).
- **When to use**: Blocking I/O (DB, File, Legacy API).
- **Behavior**: Influences where the source starts emitting.

### `publishOn(Schedulers.parallel())`
- **Impact**: Downstream.
- **When to use**: CPU-intensive mapping or processing.
- **Behavior**: Shifts all subsequent operations to the new thread pool.

### `contextWrite(Context.of(key, value))`
- **Direction**: Upstream (towards the Publisher).
- **Replacement**: The reactive-safe replacement for `ThreadLocal`.

## 📝 Scheduler Check (Self-Assessment)

1. **The Race**: I have two `subscribeOn` calls: `subscribeOn(A)` near the source and `subscribeOn(B)` near the subscriber. Which one wins?
   <details>
   <summary>💡 View Answer</summary>
   **`subscribeOn(A)` wins**. The one closest to the source (upstream) defines the initial execution context.
   </details>

2. **Virtual vs Platform**: Why would I use Virtual Threads instead of `boundedElastic`?
   <details>
   <summary>💡 View Answer</summary>
   Virtual Threads have extremely low overhead and don't require pooling. They are ideal for high-concurrency blocking I/O where you might have thousands of concurrent requests waiting on network calls.
   </details>

3. **The Context**: If I set a value in the context at the bottom of the pipeline, can an operator at the top read it?
   <details>
   <summary>💡 View Answer</summary>
   **Yes**. The Context travels **upstream** during the subscription phase, allowing all operators above the `contextWrite` to access the data.
   </details>

---
**Next Lab**: [LAB-008: Programmatic Stream Creation](../008-programmatic-streams/README.md)
