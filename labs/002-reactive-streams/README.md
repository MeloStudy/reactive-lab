# LAB-002: The Reactive Streams Specification & TCK

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

## 🔍 TCK Failure & Spec Rule Guide

The TCK (Technology Compatibility Kit) runs rigorous tests. If your implementation fails, refer to this guide to understand which part of the **Reactive Streams Specification** is being violated:

| Failure Message Snippet | Rule | Explanation |
| :--- | :--- | :--- |
| `onNext must only be sent after a Subscription...` | **1.1** | You are emitting data before `onSubscribe` or without `request(n)`. |
| `must call onSubscribe on the provided Subscriber` | **1.9** | You must call `subscriber.onSubscribe` before any other signal. |
| `must signal onError if the request is <= 0` | **3.9** | `request(n)` where `n <= 0` is invalid and must terminate with error. |
| `Subscription.cancel MUST be idempotent` | **3.5** | Calling `cancel()` multiple times must not have side effects. |
| `onNext MUST be processed sequentially` | **2.13** | You cannot call `onNext` from multiple threads for the same subscriber. |

### How to use this Lab
1.  **Analyze** the reference implementation in `CustomPublisher.java`.
2.  **Verify** the comments linked to specific rules.
3.  **Execute** the tests to see the TCK in action:
    ```bash
    mvn test
    ```

---

## Technical Standard Checklist
- [x] **Java 21** configuration in `pom.xml`.
- [x] **Maven** structure: Code in `src/main/java`, Tests in `src/test/java`.
- [x] **TCK Integration**: Using `PublisherVerification` from `org.reactivestreams.tck`.
