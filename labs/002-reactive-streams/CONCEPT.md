# CONCEPT: The Reactive Streams Specification

The Reactive Streams Specification (v1.0.4) is a formal standard for asynchronous stream processing with non-blocking backpressure. It provides a common language for libraries like Project Reactor, RxJava, and Akka Streams to work together without overwhelming each other.

## Official Resources
- **Website**: [reactive-streams.org](https://www.reactive-streams.org/)
- **GitHub**: [reactive-streams/reactive-streams-jvm](https://github.com/reactive-streams/reactive-streams-jvm)
- **Spec Document**: [The Full Specification Rules](https://github.com/reactive-streams/reactive-streams-jvm/blob/v1.0.4/README.md)

## The "Dynamic Push-Pull" Duality

Reactive Streams isn't just "push" (like classic Observables) or just "pull" (like Iterators). It is a hybrid:

1.  **Subscription is the Governor**: The `Subscription` object acts as a bridge.
2.  **Subscriber Pulls Demand**: Through `request(n)`, the subscriber informs the publisher of its processing capacity.
3.  **Publisher Pushes Data**: The publisher only pushes data *after* it has been requested, up to the limit of `n`.

## Deep Dive into the TCK (Technology Compatibility Kit)

The TCK is not just a "unit test suite." It is a rigorous **Compliance SPI**. It validates that your code doesn't just "work," but follows the 40+ mandatory rules of the specification.

### TCK Architecture
- **TestNG Base**: The TCK is built on TestNG (instead of JUnit) due to its superior handling of timeouts and concurrent execution.
- **PublisherVerification<T>**: An abstract class you must extend to test your Publisher.
- **Rules checked**:
    - **Rule 1.1**: Must signal `onNext` only after a Subscription and demand.
    - **Rule 3.3**: Demand must be additive (long overflow must be handled).
    - **Rule 3.9**: `request(n)` where `n <= 0` must trigger `onError`.

### Why the TCK is Hard
The TCK tests your code under heavy stress. It will simulate:
- Rapid `request` and `cancel` calls from multiple threads.
- Publishers that are too fast or too slow.
- Error propagation during active emission.

## The Handshake State Machine

A compliant implementation must handle several states:

| State | Allowed Transitions | Description |
| :--- | :--- | :--- |
| **New** | → Subscribed | Initial state before `subscribe()` is called. |
| **Subscribed** | → Requesting / Cancelled | `onSubscribe` has been called; waiting for demand. |
| **Active** | → Requesting / Cancelled / Terminated | Data is flowing based on demand. |
| **Terminated** | None | `onComplete` or `onError` has been called. |
| **Cancelled** | None | `cancel()` was called; no more signals allowed. |

### Concurrency Challenge
The `Subscription` implementation MUST be thread-safe. If one thread calls `request(n)` while another calls `cancel()`, the implementation must ensure that `onNext` signals stop immediately and demand is not updated incorrectly. This is why we use `AtomicLong` and `AtomicBoolean` in our implementation.
