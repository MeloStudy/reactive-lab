# CONCEPT: Programmatic Streams & Connection Lifecycle

In Project Reactor, creating streams manually requires choosing the right tool for the job. This choice depends on whether the data source is synchronous (pull), asynchronous (push), or an external event-driven system.

## 1. Programmatic Generation: Pull vs. Push

### `Flux.generate` (Synchronous Pull)
`Flux.generate` is designed for **stateful, synchronous** emission. It follows a "pull" model where the subscriber's demand drives the production of data one item at a time.
- **State Management**: It provides a built-in state supplier and a generator function that receives the current state.
- **Strict Contract**: You can only call `sink.next()` **at most once** per iteration. Calling it multiple times or calling `sink.complete()` after `next()` in the same block will result in an error.
- **Best For**: Deterministic sequences like Fibonacci, range generators, or reading lines from a blocking `BufferedReader`.

### `Flux.create` (Asynchronous Push)
`Flux.create` is the bridge between the imperative, listener-based world and the reactive world. It supports **asynchronous, multi-valued** emission.
- **Flexibility**: Unlike `generate`, you can push multiple items (`sink.next(T)`) or even complete the stream from a different thread or a callback.
- **Backpressure Handling**: Since the producer might be faster than the subscriber, `create` allows you to specify a `FluxSink.OverflowStrategy` (e.g., `BUFFER`, `DROP`, `LATEST`).
- **Lifecycle Management**: It is critical to use `sink.onDispose(Disposable)` or `sink.onCancel(Disposable)` to unregister listeners and prevent memory leaks.

### `Sinks` (Manual Management)
`Sinks` are the modern replacement for `Processor`. They allow you to manually push data into a Flux from anywhere in your application, completely outside the subscription context.
- **Thread Safety**: Sinks provide `tryEmitNext(T)` which returns a `Sinks.EmitResult`. This is crucial for concurrent emissions. Sinks handle serialized access, but you must handle results like `FAIL_NON_SERIALIZED` (concurrent emission attempt) or `FAIL_OVERFLOW`.
- **Emission Strategy**: Prefer `tryEmitNext` over `emitNext` in high-throughput scenarios where you want to explicitly handle failures without throwing exceptions.
- **Types**: `Sinks.One` (for Mono-like behavior), `Sinks.Many` (for Flux-like behavior), and various buffering/multicasting strategies.

## 2. Cold vs. Hot Publishers

The subscription lifecycle determines how data is shared and when production starts.

### Cold Publishers (The Movie 🎬)
- **Nature**: Lazy and independent.
- **Behavior**: The data source is created fresh for **every subscriber**. If subscriber A starts at time 0 and subscriber B starts at time 5, both will receive the full sequence from the start.
- **Examples**: HTTP requests, Database queries, `Flux.range()`.

### Hot Publishers (The Concert 🎸)
- **Nature**: Eager and shared.
- **Behavior**: The data source exists independently of subscribers. Late joiners **miss data** emitted before they joined. They only "hear" what is currently being broadcast.
- **Examples**: UI events (mouse clicks), real-time price feeds, message brokers.

## 3. Connection Management (Multicasting)

Multicasting allows you to turn a Cold publisher into a Hot one, sharing a single upstream subscription among multiple downstream subscribers.

### The Mechanics: `ConnectableFlux`
When you call `.publish()`, you get a `ConnectableFlux`. Internally:
1. It maintains a **single subscription** to the upstream source.
2. It uses a specialized `Subscriber` that multicasts signals (`onNext`, `onError`, `onComplete`) to all current downstream subscribers.
3. The upstream only starts when `.connect()` is triggered (manually or via `autoConnect`/`refCount`).

- **`publish()`**: Returns a `ConnectableFlux`. Decouples upstream from downstream.
- **`share()`**: A shortcut for `publish().refCount(1)`. Starts when the first subscriber joins and stops when the last one leaves.
- **`autoConnect(n)`**: Starts when `n` subscribers arrive. It **does not stop** when they leave.
- **`refCount(n, duration)`**: Starts when `n` subscribers join and **cancels the upstream** when the count drops below `n`. Ideal for resource cleanup.

## 4. Replaying & Caching

### `cache(n)`
`cache(n)` is a specialized hot operator that **remembers** the last `n` emitted items. 
- When a new subscriber joins a "live" (hot) cached stream, they first receive the `n` cached items in a burst, and then they continue receiving live items.
- This is perfect for scenarios where you want to share an expensive resource but ensure new joiners have the latest context immediately (e.g., the last 5 chat messages or the current configuration state).

## 5. Modern JVM Evolution: Virtual Threads

With **Virtual Threads (Project Loom)** in Java 21, the landscape of concurrency is evolving, but reactive patterns remain essential for **composition** and **flow control**.

- **Virtual Threads + `Flux.generate`**: If you wrap a blocking source (like a DB cursor) inside `Flux.generate`, running the subscription on a Virtual Thread scheduler allows the carrier thread to be released during blocking I/O.
- **The Backpressure Invariant**: Virtual threads don't solve the "fast producer, slow consumer" problem. Even with a million virtual threads, you still need the Reactive Streams protocol to prevent overwhelming downstream systems.
- **Context Switching**: Reactive operators are highly optimized for minimal context switching. In high-density pipelines, the overhead of virtual thread management can still exceed the efficiency of a well-tuned event loop.
