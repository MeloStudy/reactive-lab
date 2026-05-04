# CONCEPT: Reactive Web Architecture (Spring WebFlux)

## The Shift: Servlet vs. Netty

Traditionally, Java web applications relied on the **Servlet Stack** (e.g., Tomcat, Jetty). This model follows a **thread-per-request** architecture.

### 1. Thread-per-Request (Servlet)
- Each incoming HTTP request is assigned a dedicated thread from a pool.
- If the application needs to call a database or an external API, the thread **blocks** and waits for the I/O to complete.
- **Problem**: High concurrency requires thousands of threads, which consumes massive memory (stack space) and causes high context-switching overhead.

### 2. Event Loop (Netty / WebFlux)
- Spring WebFlux runs on an **Event Loop** model (defaulting to Netty).
- A small, fixed number of threads (usually equal to CPU cores) handle all requests.
- When an I/O operation starts, the thread doesn't wait; it registers a callback and moves on to handle other requests.
- **Result**: Massive scalability with very low memory footprint.

## Why `Mono<T>` and `Flux<T>`?

In WebFlux, you **cannot return plain objects** if you want to remain non-blocking.
- **`Mono<StockQuote>`**: Represents a single quote that will be available in the future.
- **`Flux<StockQuote>`**: Represents a stream of quotes (SSE or NDJSON) that can push data as it becomes available.

Returning these types tells the framework: *"Here is a recipe for the data. Subscribe to it when you are ready to write to the HTTP response buffer."*

## Annotated vs. Functional Models

WebFlux provides two ways to define endpoints:

| Feature | Annotated (@RestController) | Functional (Router/Handler) |
| :--- | :--- | :--- |
| **Paradigm** | Imperative/Declarative (Spring MVC style) | Functional/Explicit |
| **Routing** | Hidden in annotations | Explicit in code |
| **State** | Class-based (Controller) | Method/Lambda based |
| **Customization** | Via AOP / Interceptors | Via `HandlerFilterFunction` |

### Key takeaway
Use **Annotated Controllers** for standard REST APIs where familiarity and Spring MVC consistency are prioritized.
Use **Functional Endpoints** for more programmatic control, better performance (no annotation scanning), and a more "functional" codebase.

## Streaming Data

WebFlux makes streaming incredibly easy via:
1. **Server-Sent Events (SSE)**: `text/event-stream`. Perfect for push notifications or live updates (one-way).
2. **NDJSON**: `application/x-ndjson`. Best for bulk data transfers where each JSON object is separated by a newline, allowing the client to process elements one by one without waiting for the whole array.
