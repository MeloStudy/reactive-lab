# Implementation Plan: LAB-002: The Reactive Streams Specification & TCK

## Phase 1: Environment & Project Setup
1. Create a Maven `pom.xml` in the lab directory.
2. Dependencies: `junit-jupiter`, `reactive-streams`, `reactive-streams-tck`, `assertj-core`.
3. Configure `maven-compiler-plugin` for Java 21.

## Phase 2: Scenario 1 - Implementing the Contract
1. Scaffold `CustomPublisher.java`: A simple iterative publisher.
2. Scaffold `CustomSubscriber.java`: A subscriber that allows manual demand control.
3. **Logic**: Implement the "Subscription" inner class to handle `request(n)` and `cancel()`.
4. Test: `ManualSpecTest.java` verifying the handshake sequence.

## Phase 3: Scenario 2 - The TCK Validation
1. Create `PublisherTCKTest.java` extending `PublisherVerification<Integer>`.
2. Map the learner's publisher to the TCK test harness.
3. Identify common failures (e.g., sending nulls, signaling after error) and document them as learning moments.

## Phase 4: Documentation & "Dissection"
1. Draft `CONCEPT.md`: Deep dive into the 4 interfaces. Explain the "Rules" of the spec (e.g., Rule 1.1: `onNext` only after `request`).
2. Draft `README.md`: Step-by-step guide on building a compliant publisher.
3. Apply **Command Dissection** for `mvn test` and the TCK test runner.

## Decisions Made
- **Library Selection**: Use `org.reactivestreams` (Standard) as it's the native interface for Project Reactor and most reactive libraries. `java.util.concurrent.Flow` will be mentioned as the JDK equivalent for completeness.
