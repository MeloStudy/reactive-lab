# LAB-008: Programmatic Stream Generation 🛠️📈

Welcome to Lab 008! In this module, you will learn how to create your own reactive sources. You will bridge the gap between imperative code (listeners, callbacks) and the reactive world of Project Reactor.

## 🎯 Learning Objectives
- LO-001: Generate synchronous sequences with `Flux.generate`.
- LO-002: Bridge push-based callback APIs with `Flux.create`.
- LO-003: Understand the push vs. pull emission models.
- LO-004: Implement event buses using `Sinks`.
- LO-005: Handle memory safety in push bridges using `OverflowStrategy`.

## 🛠️ Scenario Walkthrough

### 1. The Fibonacci Generator
Use `Flux.generate` to implement the Fibonacci sequence. You will manage the state (the previous two numbers) within the generator itself, ensuring thread-safe, synchronous emission.

### 2. The Chat Bridge
Wrap a mock `ChatListener` into a `Flux`. You will learn how to register the listener on subscription and, most importantly, how to **unregister** it when the subscriber cancels to avoid memory leaks.

### 3. The Overflow Teaser
Simulate a fast producer and a slow consumer. You will experiment with `OverflowStrategy.DROP` to see how Reactor handles situations where the producer is pushing more data than requested.

### 4. The Notification Bus
Implement a centralized notification system using `Sinks.Many`. You will verify that multiple subscribers can listen to the same stream of events simultaneously.

## 🚀 Execution Guide

Run the validation suite:

```powershell
mvn test -pl labs/008-programmatic-streams
```

## 🔍 Command Dissection

### `Flux.generate(stateSupplier, generator)`
- **Restriction**: Only 1 `sink.next()` per iteration.
- **Nature**: Pull-based. Driven by subscriber demand.

### `Flux.create(sink -> { ... }, strategy)`
- **Flexibility**: Multiple `sink.next()` calls allowed.
- **Nature**: Push-based. The producer dictates the speed.

### `Sinks.many().multicast()`
- **Usage**: Perfect for internal app event buses.

## 📝 Generation Check (Self-Assessment)

1. **Pull vs Push**: I am wrapping a WebSocket listener that receives messages at random intervals. Should I use `generate` or `create`?
   <details>
   <summary>💡 View Answer</summary>
   **`create`**. Since the messages arrive asynchronously and the producer (WebSocket) pushes them, `create` is the correct bridge. `generate` is strictly for synchronous, demand-driven data.
   </details>

2. **The 1-Emission Rule**: What happens if I call `sink.next()` twice inside a single `Flux.generate` block?
   <details>
   <summary>💡 View Answer</summary>
   Reactor will throw an **`IllegalStateException`**. The `generate` contract strictly enforces 1 emission per iteration to maintain the synchronous pull model.
   </details>

3. **Memory Leaks**: Why is `sink.onDispose()` crucial when using `Flux.create` to wrap an external listener?
   <details>
   <summary>💡 View Answer</summary>
   If you don't unregister the listener on dispose, the external service will keep a reference to your bridge object even after the Flux is cancelled. This prevents the object from being Garbage Collected, leading to a memory leak.
   </details>

---
**Next Lab**: [LAB-009: Backpressure & Flow Control](../009-backpressure-flow/README.md)
