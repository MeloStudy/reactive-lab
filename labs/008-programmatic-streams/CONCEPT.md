# CONCEPT: Programmatic Stream Generation

Project Reactor provides several ways to create `Flux` and `Mono` manually. Choosing the right one depends on whether you are generating data algorithmically or bridging with an external event source.

## 1. Flux.generate (The Pull Model)

`Flux.generate` is used for **synchronous** and **stateful** generation. 
- **The Contract**: You can only call `sink.next(T)` **exactly once** per iteration. You cannot call `next` in a loop inside the generator.
- **Why?**: It is driven by downstream demand. If the subscriber asks for 1 item, the generator runs once.
- **State**: It allows you to pass a state object (like an index or a sequence buffer) between iterations safely.

## 2. Flux.create (The Push Model)

`Flux.create` is a bridge for **asynchronous** and **multi-valued** sources, such as listeners or callbacks.
- **The Contract**: You can call `sink.next(T)` multiple times, at any time, even from different threads.
- **The Risk**: Because the source is "Pushing" data, it might emit faster than the subscriber can consume. This is why you MUST define an **`OverflowStrategy`**.
- **Strategies**:
  - `IGNORE`: Completely ignore backpressure (can lead to `IllegalStateException`).
  - `BUFFER`: Buffer all signals in memory (risk of `OutOfMemoryError`).
  - `DROP`: Drop incoming signals if the subscriber isn't ready.
  - `LATEST`: Keep only the latest signal and drop previous ones.

## 3. Sinks (The Standalone Producers)

`Sinks` are the modern replacement for the deprecated `Processor` API. They allow you to manually trigger signals into a Flux/Mono from anywhere in your code.

- **`Sinks.Many`**: For multiple emissions (Flux).
  - `multicast()`: All subscribers receive the same messages.
  - `unicast()`: Only one subscriber allowed.
- **`Sinks.One`**: For a single emission (Mono).

### Common Use Case: The Global Event Bus
A `Sinks.Many` can act as a central hub where various parts of an application "sink" their events, and other parts "listen" by subscribing to the resulting `asFlux()`.

## 4. Resource Management

When bridging with external systems, you must ensure that listeners are unregistered when the stream is cancelled.
- **`sink.onDispose(Runnable)`**: Runs when the stream completes, errors, or is cancelled.
- **`sink.onCancel(Runnable)`**: Specifically for cancellation signals.
