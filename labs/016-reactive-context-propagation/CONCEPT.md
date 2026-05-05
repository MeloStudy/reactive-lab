# Concept: Reactive Context & Tracing Propagation

## The ThreadLocal Problem

In traditional imperative programming (e.g., Spring MVC with Tomcat), a single thread handles a request from start to finish. This allows us to use `ThreadLocal` storage to hold metadata like the current user, security tokens, or correlation IDs. Logging frameworks like SLF4J leverage this via **MDC (Mapped Diagnostic Context)**.

In **Reactive Programming** (Project Reactor / WebFlux), this model breaks:
1. **One thread handles many requests**: A single Event Loop thread might process interleaved signals from hundreds of concurrent streams.
2. **One request spans many threads**: A single pipeline might start on an HTTP NIO thread, move to a `parallel` scheduler for processing, and end on a different NIO thread for the response.

If we use `ThreadLocal` in a reactive pipeline, metadata will either "leak" to unrelated requests or be "lost" when the execution jumps to a different thread.

## The Solution: Reactor Context

Project Reactor provides a specialized feature called `Context`. It is a key-value store tied to a **Subscription**, not a thread.

### Key Characteristics:

1. **Immutable**: Like the streams themselves, the `Context` is immutable. Every time you add a value (using `contextWrite`), a new `Context` instance is created.
2. **Tied to Subscription**: The context is available to all operators in the chain that are "upstream" from where the context was defined.
3. **Upward Propagation**: This is the most counter-intuitive part. `contextWrite` is applied from the **Subscriber** towards the **Source**.
    - If you write `mono.contextWrite(ctx)`, the operators *above* that line in the code (upstream) will see the context.
4. **ContextView**: To read the context, we use `deferContextual(ctx -> ...)` or `transformDeferredContextual`.

## Bridging to MDC

Since most logging frameworks still rely on `ThreadLocal` MDC, we must manually bridge the Reactor `Context` to the MDC at the point of logging.

### The Manual Bridge Pattern:

```java
Mono.deferContextual(ctx -> {
    String cid = ctx.get("correlationId");
    try (MDC.MDCCloseable ignored = MDC.putCloseable("correlationId", cid)) {
        log.info("My log message");
        return Mono.just(data);
    }
});
```

### Modern Alternative: Micrometer Context Propagation

In Spring Boot 3 and modern Reactor versions, the `io.micrometer:context-propagation` library can automate this. It allows "snapshots" of thread locals to be captured and restored automatically when switching threads in a reactive chain. However, understanding the manual `Context` mechanics is essential for debugging and handling custom metadata.

## Best Practices

- **Don't use Context for Business Data**: Context is for infrastructure/cross-cutting concerns (Tracing, Security, Localization). Business data should be passed via the stream signals.
- **Write at the Bottom**: Since context flows upward, you typically call `.contextWrite()` at the very end of your pipeline (e.g., in a WebFilter or just before subscribing).
- **Use Class Keys**: To avoid key collisions, use Class types as keys (e.g., `Context.of(User.class, myUser)`).
