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
- **Key Command**: `disposable.dispose()`

### 2. The Resource Manager
When dealing with multiple reactive components, you'll learn to use a `CompositeDisposable` to clean up all resources with a single call.
- **Key Command**: `Disposables.composite()`

### 3. The Greedy Subscriber
Instead of letting the publisher push data as fast as possible, you will implement a `BaseSubscriber` that manually requests items one by one. This is the foundation of flow control.
- **Key Command**: `request(n)` inside `hookOnNext`

### 4. Automatic vs. Manual Lifecycle
Compare how manually calling `.dispose()` differs from using the `.take(n)` operator, which automatically handles the cancellation signal for you.

### 5. The Lifecycle Watcher
Attach side-effect hooks (`doOnSubscribe`, `doOnCancel`, etc.) to a pipeline to observe exactly how and when signals move through the stream.

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

---
**Next Lab**: [LAB-005: Error Handling & Resilience](../005-error-handling)
