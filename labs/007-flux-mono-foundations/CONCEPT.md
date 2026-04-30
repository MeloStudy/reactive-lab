# Concept: Flux & Mono Foundations

## The Reactive Cardalities

In Project Reactor, we use two primary abstractions to represent streams of data. Choosing the right one is about communicating **intent** and **cardinality**.

### 1. Flux<T>
A `Flux` is a standard Reactive Streams `Publisher`, representing an asynchronous sequence of **0 to N** emitted items, optionally terminated by either a completion signal or an error.
- **Signals**: `onNext(T)` (0 to N times), `onComplete()`, or `onError(Throwable)`.
- **Use Case**: Real-time event streams, database query results, file chunks.

### 2. Mono<T>
A `Mono` is a specialized `Publisher` that emits at most **one** item and then terminates.
- **Signals**: `onNext(T)` (0 or 1 time), `onComplete()`, or `onError(Throwable)`.
- **Use Case**: HTTP responses, single record lookups, asynchronous tasks (like `Future`).

---

## The "Lazy Execution" Principle

One of the most critical concepts in Reactive Programming is that **nothing happens until you subscribe**.

### Assembly Time vs. Subscription Time
1. **Assembly Time**: When you write `Flux.just(1, 2).map(i -> i * 2)`, you are building a blueprint. You are wrapping publishers within publishers. No data is flowing yet.
2. **Subscription Time**: When someone calls `.subscribe()` (or uses `StepVerifier`), the execution engine "pulls the trigger". The chain of subscribers is linked back up to the source, and signals begin to flow down.

> [!IMPORTANT]
> If you instantiate a `Mono.fromCallable(() -> performHeavyTask())` but never subscribe to it, the `performHeavyTask()` method will **never** be executed.

---

## Pipeline Immutability

Reactor operators are **pure**. They do not modify the instance they are called on; instead, they return a new instance of a `Publisher`.

### The Common Pitfall
```java
Flux<String> flux = Flux.just("apple", "banana");
flux.map(String::toUpperCase); // WRONG: result is ignored
flux.subscribe(System.out::println); // Prints "apple", "banana" (lowercase)
```

### The Correct Way
```java
Flux<String> flux = Flux.just("apple", "banana");
flux = flux.map(String::toUpperCase); // CORRECT: captured the new instance
// OR
Flux.just("apple", "banana")
    .map(String::toUpperCase)
    .subscribe(System.out::println); // CORRECT: chained
```

---

## Core Factory Methods

Reactor provides static methods to bridge imperative code or constants into the reactive world:

| Method | Type | Description |
| :--- | :--- | :--- |
| `Flux.just(T...)` | Flux | Emits a fixed set of items. |
| `Flux.fromIterable(Iterable)` | Flux | Emits items from a Java Collection. |
| `Mono.fromCallable(Callable)` | Mono | Executes a blocking task lazily and emits the result. |
| `Mono.empty()` | Mono | Emits only the completion signal. |
| `Mono.error(Throwable)` | Mono/Flux | Emits an error signal immediately upon subscription. |

---

## Testing with StepVerifier

Testing reactive streams is difficult because they are asynchronous and signal-based. `StepVerifier` allows you to define a "script" of expectations against a Publisher.

- `expectNext(T)`: Asserts that the next signal is `onNext` with the expected value.
- `expectComplete()`: Asserts that the stream terminates successfully.
- `expectError(Class)`: Asserts that the stream terminates with an exception.
- `verify()`: Triggers the subscription and executes the assertions.
