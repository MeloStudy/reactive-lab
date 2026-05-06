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
- **`Schedulers.fromExecutor(Executor)`**: Allows you to use any custom pool, including Java 21's Virtual Threads.

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

## 5. Anatomy of a Scheduler (Under the Hood)

A Scheduler is more than just a Thread Pool; it is an asynchronous event manager.

### The "Timer Wheel"
When you use `delayElement` or time-based operations, Reactor does not "sleep" a thread.
1.  It registers a task in a data structure called a **Timer Wheel**.
2.  It **releases the thread** immediately so it can process other signals.
3.  A single lightweight control thread rotates the "wheel," and when the time expires, it places the resumption task into the Scheduler's work queue.

### The Work Queue
Each thread in the Scheduler (Worker) consumes from a queue. If a Worker is busy, the signal waits in the queue. This ensures the CPU is always occupied with real work, not waiting for I/O.

---

## 6. The Event Loop Bridge (Netty)

In Java, the reactive engine is typically **Netty**. Unlike Node.js (which has a single Event Loop), Java utilizes an **`EventLoopGroup`**.

1.  **Thread Affinity**: By default, there is one Event Loop per CPU core.
2.  **Non-Blocking**: The Event Loop uses OS mechanisms (`epoll`, `kqueue`) to delegate network waiting to the Kernel.
3.  **Notification**: When the OS Kernel detects data on the Network Interface Card (NIC), it notifies the Event Loop, which then places the continuation task on the corresponding Scheduler.

> **Important**: Reactive efficiency comes from keeping data on the same Event Loop for as long as possible to avoid expensive **Context Switches** (jumping between threads).

## 7. The Scheduler Trap (Immutable Upstream)

A common mistake is thinking that `subscribeOn` works like `publishOn`.
- `publishOn` switches threads **downstream** (forward in the pipeline).
- `subscribeOn` switches threads **upstream** (backward to the source).

**The Rule**: If you have multiple `subscribeOn` operators, only the one **closest to the source** defines the thread that performs the initial emission. All subsequent `subscribeOn` calls are effectively ignored for the purpose of emission context.

## 8. Schedulers vs. Virtual Threads (Java 21+)

With the arrival of Project Loom (Virtual Threads), do we still need Schedulers?

| Feature | Schedulers (Platform Threads) | Virtual Threads (Loom) |
| :--- | :--- | :--- |
| **Footprint** | Heavy (~1MB per thread stack). | Lightweight (Bytes per stack). |
| **Blocking** | Expensive. Ties up a kernel thread. | Cheap. Parked on the heap. |
| **Usage** | Best for CPU-parallel work. | Best for massive blocking I/O. |
| **Integration** | Native to Reactor. | Integrated via `Schedulers.fromExecutor`. |

**Verdict**: Use `Schedulers.parallel()` for CPU work. For blocking I/O, you can now choose between `boundedElastic` (stable, pooled) or `Virtual Threads` (unbounded, extremely scalable).
