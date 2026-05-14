# CONCEPT: Testing & Debugging Matrix

Debugging a reactive application is notoriously difficult because of the disconnect between where code is written and where it is executed. This "stack trace gap" requires a specialized set of tools and a mental model shift.

## 1. The Assembly vs. Execution Divide

In imperative Java, a stack trace is a snapshot of the current thread's state. In Project Reactor:
- **Assembly Time**: This is when you define your pipeline (`flux.map(...).filter(...)`). This usually happens on the `main` thread.
- **Execution Time**: This is when signals (`onNext`, `onError`) actually flow through the pipeline. This often happens on background threads (Schedulers).

When an error occurs, the standard JVM stack trace only shows the **Execution** stack. It has no idea about the **Assembly** stack (where the operator was actually declared in your code).

### 🔍 Solving the Gap
- **`Hooks.onOperatorDebug()`**: Reactor "records" the assembly-time stack trace for every operator. When an error occurs, it staples this recorded information to the original exception. 
    - *Note*: This is memory-intensive and should only be used in local development or CI.
- **`checkpoint("label")`**: A lightweight alternative. It manually adds a label to the pipeline's stack trace, pinpointing exactly which segment failed without the overhead of capturing the full stack.

## 2. Testing Time: The Virtual Time Warp

Testing time-based operators (like `interval`, `delayElements`, or `timeout`) can make tests slow and non-deterministic. Reactor's `VirtualTimeScheduler` hijacks the system clock, allowing you to advance time manually.

- **The Supplier Rule**: To use virtual time, you must wrap the *creation* of your `Flux` or `Mono` inside a `Supplier` passed to `StepVerifier.withVirtualTime(() -> myFlux)`. 
- **Why?**: This ensures that when the operators are instantiated (Assembly Time), they already "see" the virtual scheduler instead of the real-time one.

## 3. Reactor Context: Thread-Local for the Async World

In a multi-threaded asynchronous pipeline, `ThreadLocal` variables are dangerous because execution jumps between threads constantly. 

**Reactor Context** is an immutable key-value store that:
1.  **Travels Upstream**: Unlike data signals, Context travels from the `Subscriber` up to the `Publisher` during the subscription phase.
2.  **Is Scope-Limited**: It belongs to a specific `Subscription` instance, not a global thread.
3.  **Persistence**: It allows you to carry metadata (like Trace IDs or Security Tokens) through every thread hop in the pipeline.

## 4. Non-Blocking Enforcement: BlockHound

The **Event Loop** threads (Netty, Parallel) must never be blocked. A single `Thread.sleep()` or blocking I/O call can freeze the entire application.

**BlockHound** is a Java agent that:
- Instruments the standard Java library (e.g., `Thread.sleep`, blocking I/O).
- Detects if these calls are made on threads marked with the `NonBlocking` marker interface.
- Throws a `BlockingOperationError` immediately, failing the test and pointing to the offending line.

## 5. Debugging in the Virtual Thread Era

With the introduction of **Virtual Threads** (Java 21), some debugging challenges change:
- **Simplified Stacks**: Virtual threads can produce cleaner stack traces for blocking code compared to deeply nested reactive callbacks.
- **Context is still King**: Even with Virtual Threads, **Reactive Context** remains essential when using reactive libraries. It ensures that metadata (like Trace IDs) survives the hop between the reactive event loop and virtual thread executors.
- **BlockHound & Loom**: BlockHound is primarily designed to protect "Event Loop" threads (which must never block). Virtual Threads, by design, are meant to be blocked. You shouldn't (and usually can't) use BlockHound to prevent blocking on a Virtual Thread.

## 6. The Scannable API: Runtime Inspection

Sometimes you need to inspect a pipeline's state while it's running (or stalled). The `Scannable` interface allows you to:
- **Find Parents/Children**: Traverse the operator chain.
- **Inspect Capacity**: Check buffer sizes or current demand (`REQUESTED_FROM_UPSTREAM`).
- **Scan for Tags**: Find custom metadata tags attached to operators.

Usage: `Scannable.from(flux).scan(Scannable.Attr.TERMINATED)` returns a boolean indicating if the stream has finished.
