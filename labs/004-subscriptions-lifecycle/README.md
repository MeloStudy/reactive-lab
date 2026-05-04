# LAB-004: Subscriptions & Lifecycle Control ⏱️

Welcome to Lab 004! In this module, you will move beyond passive subscriptions and learn how to actively manage the lifecycle of reactive streams.

## 🎯 Learning Objectives
- LO-001: Execute manual subscriptions using various `subscribe()` overloads.
- LO-002: Control stream lifecycle using the `Disposable` interface for cancellation.
- LO-003: Utilize `Disposables.composite()` for multi-stream resource management.
- LO-004: Implement custom demand control using `BaseSubscriber`.
- LO-005: Distinguish between manual `dispose()` and operator-driven lifecycle (`take`).

## 🛠️ Scenario Walkthrough

### 1. The Controlled Firehose
Infinite streams like `Flux.interval` will run forever unless stopped. You will use the `Disposable` handle returned by `.subscribe()` to programmatically kill the stream after a delay.
- **Java Class**: [`FirehoseManagerTest.java`](src/test/java/com/reactivelab/lifecycle/FirehoseManagerTest.java)
- **Key Command**: `disposable.dispose()`

### 2. The Resource Manager
When dealing with multiple reactive components, you'll learn to use a `CompositeDisposable` to clean up all resources with a single call.
- **Java Class**: [`SubscriptionGroupTest.java`](src/test/java/com/reactivelab/lifecycle/SubscriptionGroupTest.java)
- **Key Command**: `Disposables.composite()`

### 3. The Greedy Subscriber
Instead of letting the publisher push data as fast as possible, you will implement a `BaseSubscriber` that manually requests items one by one. This is the foundation of flow control.
- **Java Class**: [`SmartSubscriberTest.java`](src/test/java/com/reactivelab/lifecycle/SmartSubscriberTest.java)
- **Key Command**: `request(n)` inside `hookOnNext`

### 4. Automatic vs. Manual Lifecycle
Compare how manually calling `.dispose()` differs from using the `.take(n)` operator, which automatically handles the cancellation signal for you.
- **Java Class**: [`LifecycleComparisonTest.java`](src/test/java/com/reactivelab/lifecycle/LifecycleComparisonTest.java)

### 5. The Lifecycle Watcher
Attach side-effect hooks (`doOnSubscribe`, `doOnCancel`, etc.) to a pipeline to observe exactly how and when signals move through the stream.
- **Java Class**: [`LifecycleTrackerTest.java`](src/test/java/com/reactivelab/lifecycle/LifecycleTrackerTest.java)

## 🚀 Execution Guide

Run the TDD validation suite to verify your implementation:

```powershell
mvn test -pl labs/004-subscriptions-lifecycle
```

## 🔍 Command Dissection

### `subscribe(...)`
The entry point to every reactive stream. 
- `flux.subscribe()`: Fire and forget.
- `flux.subscribe(valueConsumer)`: Handle data.
- `flux.subscribe(valueConsumer, errorConsumer)`: Handle failures.

### `dispose()`
A method on the `Disposable` interface. It sends a **cancellation** signal upstream. It is NOT a terminal signal like `onComplete`—it is an interruption.

### `BaseSubscriber<T>`
An abstract class provided by Reactor to make implementing custom subscribers easier.
- `hookOnSubscribe`: Called once when the subscription starts.
- `hookOnNext`: Called for each item.
- `request(n)`: The most important method for controlling demand.

### `Disposables.composite()`
Creates a container that can hold multiple `Disposable` objects. 
- `composite.add(disposable)`: Adds a new subscription to the group.
- `composite.dispose()`: Atomically cancels ALL subscriptions currently in the container and prevents any future additions from being active.

## 📝 Lifecycle Check (Self-Assessment)

Test your knowledge of Reactive Lifecycles:

1. **Interruption vs Termination**: If a stream is cancelled using `dispose()`, will the `onComplete` signal be triggered?
   <details>
   <summary>💡 View Answer</summary>
   **No**. `dispose()` is an asynchronous interruption (cancellation). Terminal signals (`onComplete` or `onError`) are only emitted by the Publisher when the stream finishes normally or fails.
   </details>

2. **The Lazy Firehose**: You subscribe to `Flux.interval(Duration.ofSeconds(1))` and get a `Disposable` handle. If you call `handle.dispose()` 500ms later, will you see any items?
   <details>
   <summary>💡 View Answer</summary>
   **No**. The first item of `Flux.interval` is emitted after the first period (1 second). Since you cancelled at 500ms, the subscription is killed before the first emission.
   </details>

3. **Composite Responsibility**: Why use `CompositeDisposable` instead of a list of `Disposable` objects?
   <details>
   <summary>💡 View Answer</summary>
   `CompositeDisposable` provides **atomic** and thread-safe cancellation. Additionally, once a composite is disposed, any new `Disposable` added to it will be automatically and immediately disposed, preventing resource leaks in race conditions.
   </details>

4. **Manual Demand**: In a `BaseSubscriber`, if you call `request(1)` in `hookOnSubscribe` but forget to call it in `hookOnNext`, what happens?
   <details>
   <summary>💡 View Answer</summary>
   The stream will "hang" after the first item. You will receive exactly one `onNext` signal, and then the Publisher will wait forever for more demand that never arrives.
   </details>

---
**Next Lab**: [LAB-005: Error Handling & Resilience](../005-error-handling/README.md)
