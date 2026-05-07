# CONCEPT: The Reactive Subscription Lifecycle

In Reactive Programming, data does not flow until a **Subscriber** is attached to a **Publisher**. This connection creates a **Subscription**, which is the core object governing the lifecycle of the stream.

## 1. The Subscription Contract

The relationship between a Publisher and a Subscriber is defined by the `Subscription` interface. It has two primary methods:
- `request(long n)`: The Subscriber uses this to signal demand.
- `cancel()`: The Subscriber uses this to stop receiving data.

When you call `.subscribe()` on a Flux or Mono, several things happen internally:
1. The Subscriber calls `publisher.subscribe(subscriber)`.
2. The Publisher calls `subscriber.onSubscribe(subscription)`.
3. The Subscriber is now "active" and can begin requesting data.

## 2. The Subscription State Machine

A Subscription is not just a link; it is a stateful object. Understanding its transitions is key to debugging reactive leaks:

1.  **UNSUBSCRIBED**: The initial state. No data flows.
2.  **SUBSCRIBED**: Triggered by `.subscribe()`. The `onSubscribe` signal is sent. The stream is now ready but "dormant" until demand is requested.
3.  **REQUESTING**: The Subscriber calls `request(n)`. The Publisher begins pushing items.
4.  **TERMINATED**: The Publisher sends `onComplete` or `onError`. The Subscription is now dead and cannot be reused.
5.  **CANCELLED**: The Subscriber calls `cancel()` (or `dispose()`). Signals stop flowing immediately, and the Publisher should release resources.

## 3. Manual Cancellation (The Disposable)

In Project Reactor, the `.subscribe()` method returns a `Disposable` object. This is your "handle" to the subscription.

```java
Disposable handle = flux.subscribe();
// ... later
handle.dispose(); // Stops the stream
```

### Why Manual Cancellation?
- **Resource Management**: Infinite streams (like clock intervals or socket listeners) will never stop on their own. They will cause memory leaks if not explicitly cancelled.
- **Interruption**: You might want to stop a long-running process if the user navigates away from a page or a timeout occurs.

## 4. Grouped Lifecycles (CompositeDisposable)

If your component manages multiple subscriptions, it's tedious to track each `Disposable` individually. `Disposables.composite()` allows you to group them:

```java
Disposable.Composite group = Disposables.composite();
group.add(flux1.subscribe());
group.add(flux2.subscribe());

group.dispose(); // Cancels flux1 and flux2 simultaneously
```

## 5. Manual Demand (BaseSubscriber)

While lambda-based subscriptions (`flux.subscribe(value -> ...)`) are convenient, they automatically request "unbounded" demand (`Long.MAX_VALUE`). To control the flow precisely, you should extend `BaseSubscriber`.

### The Manual Request Loop
By overriding `hookOnSubscribe` and `hookOnNext`, you can implement a manual request loop:
1. `hookOnSubscribe`: Call `request(1)` to get the first item.
2. `hookOnNext`: Process the item, then call `request(1)` to get the next one.

This ensures you never receive more data than you are currently ready to process.

## 6. Automatic Lifecycle (The 'take' operator)

Operators like `.take(n)` or `.takeUntil(predicate)` manage the lifecycle for you. When the condition is met, the operator sends a `cancel()` signal upstream to the source and an `onComplete()` signal downstream to the subscriber.

## 7. Signal Peekers (Side Effects)
 
Reactor provides "doOn" operators to peek into the lifecycle without modifying the data. These are known as **Side Effects** or **Signal Peekers**.
 
| Operator | Signal Triggered | Common Use Case |
| :--- | :--- | :--- |
| `doOnSubscribe` | `onSubscribe` | Initializing resources or metrics. |
| `doOnNext` | `onNext` | Logging, auditing, or non-intrusive caching. |
| `doOnComplete` | `onComplete` | Finalizing success logs. |
| `doOnError` | `onError` | Metric reporting or error logging. |
| `doOnCancel` | Manual `.dispose()` | Resource cleanup on interruption. |
| `doFinally` | Any Termination | Global cleanup (Complete, Error, or Cancel). |
 
### Observation vs. Transformation
 
- **Transformation (`map`, `flatMap`)**: Changes the item itself or the pipeline type. It is part of the business logic.
- **Observation (`doOn...`)**: Does **not** change the stream. It receives the signal, performs a side effect (like logging or updating a counter), and passes the signal through unchanged.
 
> [!IMPORTANT]
> Always prefer `doFinally` for resource cleanup, as it is guaranteed to run regardless of how the stream terminated.
