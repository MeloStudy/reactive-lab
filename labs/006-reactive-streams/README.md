# LAB-006: The Reactive Streams Specification & TCK

Welcome to the foundation of modern reactive programming on the JVM. In this lab, you will step away from high-level libraries like Project Reactor and build your own **Publisher** and **Subscriber** from scratch using the raw Reactive Streams interfaces.

## Learning Objectives

1.  **Implement** the core `Publisher`, `Subscriber`, and `Subscription` interfaces.
2.  **Master** the "Handshake" sequence: Subscription -> Request -> Next.
3.  **Validate** compliance using the official Technology Compatibility Kit (TCK).
4.  **Trace** implementation failures to specific specification rules.

## Reference Documentation
- [Official Reactive Streams Website](https://www.reactive-streams.org/)
- [GitHub: Reactive Streams JVM Specification](https://github.com/reactive-streams/reactive-streams-jvm)
- [Project Reactor: Reactive Streams Documentation](https://projectreactor.io/docs/core/release/reference/#reactive-streams)

---

## Step 1: Implementation of the "Raw" Contract

Your first task is to implement both a `Publisher` and a `Subscriber`. 

### The Engine: CustomSubscription
The `CustomSubscription` class handles the state. You must ensure:
- `request(long n)`: Increments demand safely.
- `cancel()`: Stops all future signals.
- **Rule 3.9**: If `n <= 0`, you must call `subscriber.onError` with an `IllegalArgumentException`.

## Step 2: Running the Handshake Validation

Execute the manual JUnit tests to verify the core flow.

```bash
mvn test -Dtest=HandshakeTest
```

## Step 3: The TCK Challenge

The TCK (Technology Compatibility Kit) is the ultimate judge of your implementation. It will run dozens of concurrent tests to ensure you follow every detail of the specification.

### Execution

```bash
mvn test -Dtest=PublisherTCKTest
```

### Analyzing TCK Failures
When a TCK test fails, it will provide a specific rule reference. For example:
> `streams.PublisherVerification$1 - onNext must only be sent after a Subscription exists and demand (request) has been signaled.`

This corresponds to **Rule 1.1**. To fix this, ensure your `drain()` loop checks `demand.get() > 0` before calling `onNext`.

---

## Technical Standard Checklist
- [x] **Java 21** configuration in `pom.xml`.
- [x] **Maven** structure: Code in `src/main/java`, Tests in `src/test/java`.
- [x] **TCK Integration**: Using `PublisherVerification` from `org.reactivestreams.tck`.
