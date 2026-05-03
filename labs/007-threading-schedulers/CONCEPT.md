# CONCEPT: Threading Models & Schedulers

In a reactive system, "who" executes the code is as important as "what" the code does. Unlike traditional imperative programming, where code usually runs on the caller's thread, Project Reactor gives you declarative control over execution contexts.

## 1. The Default: Current Thread
By default, Reactor is **unihilo** (single-threaded). The work is done on the thread that calls `subscribe()`. If that thread is the Event Loop (Netty), and you perform a blocking operation, you freeze the entire application.

## 2. Schedulers: The Thread Pools
Reactor uses `Schedulers` to manage pools of threads:
- **`Schedulers.immediate()`**: Current thread.
- **`Schedulers.single()`**: A single, reusable thread.
- **`Schedulers.parallel()`**: Optimized for CPU-intensive work (N threads = N cores).
- **`Schedulers.boundedElastic()`**: Optimized for I/O-intensive work (grows as needed, then shrinks). **Use this for blocking legacy code.**

## 3. The Operators

### `publishOn(Scheduler)`
- **Impact**: Downstream.
- **Behavior**: All operators *following* `publishOn` will run on the specified scheduler.
- **Analogy**: "From here on, everyone get on this bus."

### `subscribeOn(Scheduler)`
- **Impact**: Upstream (The Source).
- **Behavior**: Influences the thread where the `subscribe()` signal is processed and where the source starts emitting.
- **Analogy**: "Tell the factory to start producing on this specific floor."
- **Rule**: If you have multiple `subscribeOn`, only the first one (closest to the source) wins.

## 4. The Context Bridge

When you switch threads, `ThreadLocal` variables (like Spring Security context or MDC correlation IDs) are **lost**. This is because they are tied to a specific physical thread.

### Reactor `Context`
Reactor provides a `Context` which is a key-value store tied to the **Subscription**, not the Thread.
- It travels **upstream** (from the subscriber to the publisher).
- It is immutable.
- Use `contextWrite()` to add data and `deferContextual()` to read it.
- It survives thread hops because it follows the signal path, not the thread stack.
