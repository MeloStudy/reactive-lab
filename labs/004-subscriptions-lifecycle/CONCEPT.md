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

## 2. Manual Cancellation (The Disposable)

In Project Reactor, the `.subscribe()` method returns a `Disposable` object. This is your "handle" to the subscription.

```java
Disposable handle = flux.subscribe();
// ... later
handle.dispose(); // Stops the stream
```

### Why Manual Cancellation?
- **Resource Management**: Infinite streams (like clock intervals or socket listeners) will never stop on their own. They will cause memory leaks if not explicitly cancelled.
- **Interruption**: You might want to stop a long-running process if the user navigates away from a page or a timeout occurs.

## 3. Grouped Lifecycles (CompositeDisposable)

If your component manages multiple subscriptions, it's tedious to track each `Disposable` individually. `Disposables.composite()` allows you to group them:

```java
Disposable.Composite group = Disposables.composite();
group.add(flux1.subscribe());
group.add(flux2.subscribe());

group.dispose(); // Cancels flux1 and flux2 simultaneously
```

## 4. Manual Demand (BaseSubscriber)

While lambda-based subscriptions (`flux.subscribe(value -> ...)`) are convenient, they automatically request "unbounded" demand (`Long.MAX_VALUE`). To control the flow precisely, you should extend `BaseSubscriber`.

### The Manual Request Loop
By overriding `hookOnSubscribe` and `hookOnNext`, you can implement a manual request loop:
1. `hookOnSubscribe`: Call `request(1)` to get the first item.
2. `hookOnNext`: Process the item, then call `request(1)` to get the next one.

This ensures you never receive more data than you are currently ready to process.

## 5. Automatic Lifecycle (The 'take' operator)

Operators like `.take(n)` or `.takeUntil(predicate)` manage the lifecycle for you. When the condition is met, the operator sends a `cancel()` signal upstream to the source and an `onComplete()` signal downstream to the subscriber.

## 6. Lifecycle Hooks (Side Effects)

Reactor provides "doOn" operators to peek into the lifecycle without modifying the data:
- `doOnSubscribe`: Executed when the subscription is established.
- `doOnNext`: Executed for every emitted item.
- `doOnComplete`: Executed when the stream finishes successfully.
- `doOnError`: Executed when the stream fails.
- `doOnCancel`: Executed when the subscriber manually cancels.
- `doFinally`: Executed regardless of the termination signal (Complete, Error, or Cancel).
