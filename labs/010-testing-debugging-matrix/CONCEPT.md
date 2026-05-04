# Testing & Debugging Matrix

In reactive programming, the separation between **Assembly Time** (building the pipeline) and **Execution Time** (running the signals) makes traditional debugging tools like stack traces and `Thread.sleep` ineffective.

This lab covers the essential matrix of tools for verifying and diagnosing Reactor applications.

## 🧱 Assembly vs. Execution

A standard Java stack trace shows the call stack at the moment an Exception occurs. In Reactor, an exception often occurs on a thread pool (e.g., `parallel-1`) long after the main thread has finished assembling the pipeline. This results in "truncated" stack traces.

### 🔍 Debugging Tools

1.  **Hooks.onOperatorDebug()**: Enables a global hook that captures assembly-time information for every operator. It provides the "Assembly Stacktrace" which tells you exactly where the pipeline was defined.
2.  **checkpoint()**: A lightweight version of `onOperatorDebug` that you can place at specific points in a pipeline to mark them for better error reporting.
3.  **log()**: Peeks into all signals (`onNext`, `onError`, `onComplete`, `request`) passing through a specific point.

## 🧪 Advanced Testing

### Virtual Time
Testing streams that emit elements over long periods (days, months) is impossible with `Thread.sleep`. `StepVerifier.withVirtualTime` (or manual `VirtualTimeScheduler`) allows you to "fast-forward" time instantly.

### PublisherProbes
Sometimes you need to verify that a fallback branch (e.g., in `switchIfEmpty`) was executed, even if it doesn't emit any data that you can assert. `PublisherProbe` allows you to assert `wasSubscribed()`, `wasCancelled()`, etc.

## 👮 BlockHound
Reactive Event Loop threads (like those in Netty or `Schedulers.parallel()`) must NEVER be blocked. A single blocking call (I/O, `Thread.sleep`, `Mono.block()`) can freeze the entire application.
**BlockHound** is a Java agent that instruments the JVM to detect and throw an error if a blocking call is made on a thread marked as non-blocking.

## 📥 Reactor Context
Since reactive streams can jump between threads, `ThreadLocal` is unreliable. Reactor provides a `Context` that travels alongside the signals, allowing you to propagate metadata (like Security tokens or Correlation IDs) without modifying your method signatures.
