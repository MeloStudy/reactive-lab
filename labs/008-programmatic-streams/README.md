# LAB-008: Programmatic Streams & Hot/Cold 🛠️🔥

Welcome to Lab 008! In this module, you will learn how to create your own reactive sources. You will bridge the gap between imperative code (listeners, callbacks) and the reactive world of Project Reactor, while mastering the lifecycle of shared streams.

## 🎯 Learning Objectives
- LO-001: Generate synchronous sequences with `Flux.generate` (Pull model).
- LO-002: Bridge push-based callback APIs with `Flux.create` (Push model).
- LO-003: Distinguish between **Cold** (lazy) and **Hot** (live) publishers.
- LO-004: Implement event buses using the modern `Sinks` API.
- LO-005: Control the connection lifecycle with `share()`, `autoConnect()`, and `refCount()`.
- LO-006: Optimize resource sharing with `cache(n)`.

## 🛠️ Scenario Walkthrough

### 1. The Fibonacci Generator
Use `Flux.generate` to implement the Fibonacci sequence. You will manage the state (the previous two numbers) within the generator itself, ensuring thread-safe, synchronous emission.
- **Source**: [SequenceGenerator.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/SequenceGenerator.java)
- **Test**: [SequenceGeneratorTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/SequenceGeneratorTest.java)

### 2. The Chat Bridge
Wrap a mock `ChatListener` into a `Flux`. You will learn how to register the listener on subscription and, most importantly, how to **unregister** it when the subscriber cancels using `onDispose` to avoid memory leaks.
- **Source**: [ChatBridge.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/ChatBridge.java)
- **Test**: [ChatBridgeTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/ChatBridgeTest.java)

### 3. The Radio Broadcaster (Hot vs Cold)
Observe the fundamental difference between a movie (Cold) and a live concert (Hot). You will transform a standard Flux into a "Hot" publisher using `publish().autoConnect()` and verify that late subscribers miss data.
- **Source**: [Broadcaster.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/Broadcaster.java)
- **Test**: [BroadcasterTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/BroadcasterTest.java)

### 4. The On-Demand Resource
Use `refCount(n)` to manage an expensive upstream source. The resource should only start when the second subscriber joins and stop immediately when the last one leaves, preventing wasted CPU/Memory.
- **Source**: [OnDemandResource.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/OnDemandResource.java)
- **Test**: [OnDemandResourceTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/OnDemandResourceTest.java)

### 5. The Result Cache
Implement a scenario where an expensive calculation is shared among multiple consumers. Use `cache(n)` to ensure that late subscribers don't trigger a re-calculation but still get the last `n` results instantly.
- **Source**: [ResultCache.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/ResultCache.java)
- **Test**: [ResultCacheTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/ResultCacheTest.java)

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
- **Flexibility**: Multiple `sink.next()` calls allowed; asynchronous production.
- **Cleanup**: Always use `sink.onDispose()` to release external resources.

### `publish().autoConnect(n)`
- **Behavior**: Converts Cold to Hot. Starts when `n` subscribers arrive.
- **Persistence**: Stay connected even if all subscribers leave.

### `publish().refCount(n)`
- **Behavior**: Smart lifecycle management.
- **Cleanup**: Automatically cancels upstream when subscriber count drops to 0.

### `cache(n)`
- **Behavior**: Shares the upstream AND replays the last `n` items to new subscribers.
- **Effect**: Turns a Cold source into a "Warm" source (Hot with a memory).

## ⚠️ Troubleshooting

### `OverflowException` in `create`
If you encounter `OverflowException`, it means your producer is faster than your consumer.
- **Solution**: Adjust the `OverflowStrategy` (e.g., `BUFFER(n)`, `DROP`, or `LATEST`). See [ChatBridge.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/ChatBridge.java) for implementation details.

### `Sinks` Emission Failures
If `tryEmitNext` returns a failure code:
- `FAIL_NON_SERIALIZED`: Multiple threads are trying to emit concurrently.
- `FAIL_OVERFLOW`: The internal buffer is full.
- **Solution**: Ensure serialization or check buffer capacity.

### Resource Leaks
If external listeners are not being unregistered:
- **Check**: Ensure you called `sink.onDispose(() -> ...)` inside the `Flux.create` block.

## 📝 Generation Check (Self-Assessment)

1. **The Choice**: I am wrapping a database cursor that I need to read row by row. Which operator is more efficient: `generate` or `create`?
   <details>
   <summary>💡 View Answer</summary>
   **`generate`**. Since reading from a cursor is typically a synchronous, demand-driven process (pull), `generate` maps perfectly to this model and avoids the need for complex overflow strategies.
   </details>

2. **The Connection**: I have a stream of sensor data that I want to start only when at least 3 dashboards are active, and stop as soon as the last dashboard is closed. What should I use?
   <details>
   <summary>💡 View Answer</summary>
   **`publish().refCount(3)`**. This ensures the upstream sensor subscription is only active when the demand threshold (3) is met and is disposed of immediately when demand drops to zero.
   </details>

3. **Late Joiners**: A subscriber joins a `share()` stream while it is already running. Will they see the items that were emitted 10 seconds ago?
   <details>
   <summary>💡 View Answer</summary>
   **No**. `share()` (which is `refCount(1)`) does not replay data. Late joiners only see items emitted after their subscription time. To see past data, you would need `cache(n)` or `replay()`.
   </details>

4. **Sinks Safety**: Why should I prefer `Sinks.many().multicast().onBackpressureBuffer()` over the deprecated `TopicProcessor`?
   <details>
   <summary>💡 View Answer</summary>
   Modern `Sinks` provide a much safer API for concurrent emissions (`tryEmitNext`) and follow the Reactive Streams specification more strictly, avoiding common pitfalls related to internal state corruption in high-concurrency scenarios.
   </details>

5. **Virtual Threads**: If I run a `Flux.generate` that blocks on a carrier thread, will it paralyze the Event Loop?
   <details>
   <summary>💡 View Answer</summary>
   **Yes**, if it blocks the carrier thread directly. However, if you run the subscription on a scheduler backed by **Virtual Threads**, the blocking call will only park the virtual thread, releasing the carrier thread for other tasks. This makes reactive pipelines even more resilient when integrating with blocking legacy code.
   </details>

---
**Next Lab**: [LAB-009: Backpressure & Flow Control](../009-backpressure-flow/README.md)
