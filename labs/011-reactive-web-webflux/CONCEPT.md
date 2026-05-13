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

## Backpressure in the Web Layer

How does a web server handle a slow client? In WebFlux, backpressure is propagated from the **HTTP Response Buffer** down to the **Publisher**:
1. When the TCP/IP stack buffer is full, Netty signals that the channel is **not writable**.
2. The WebFlux runtime receives this signal and **stops requesting elements** from the upstream Publisher (Flux).
3. Once the client consumes data and the buffer clears, Netty signals `isWritable=true`, and the flow resumes.

This ensures the server never overflows its memory trying to push data to a client that can't keep up.

## Modern Context: Assimilating Virtual Threads (Loom)

With **Java 21**, Virtual Threads (Project Loom) provide a way to write blocking code that is as scalable as reactive code. So why use WebFlux?

| Perspective | Spring WebFlux (Event Loop) | Project Loom (Virtual Threads) |
| :--- | :--- | :--- |
| **Resource Usage** | Extremely low. Fixed thread pool. | Low per thread, but context switching exists. |
| **Programming Model** | Functional / Declarative (Pipelines). | Imperative (Standard Java). |
| **Flow Control** | Native Backpressure Support. | No native backpressure (needs manual sync). |
| **Ecosystem** | Mature support for R2DBC, Kafka, SSE. | Evolving; many libs still rely on `synchronized`. |

**Conclusion**: WebFlux is not just about concurrency; it's about **data flow orchestration**. While Loom simplifies standard CRUD, WebFlux excels in complex streaming, orchestration of multiple services, and scenarios where explicit flow control is critical.

---

## 🔬 Architecture Deep Dive: Under the Hood

### 1. The Core Engine (Dispatchers)
*   **Spring MVC (`DispatcherServlet`)**: Built on the **Thread-per-Request** model. Each request is assigned a dedicated thread from a large pool (e.g., Tomcat). If the thread blocks on I/O, it remains occupied and idle.
*   **Spring WebFlux (`DispatcherHandler`)**: Built on the **Event Loop** model (e.g., Netty). A small, fixed number of threads handle thousands of concurrent requests by never blocking; they register callbacks and move to the next task.

### 2. Request/Response Contracts
*   **Servlet API (`HttpServletRequest/Response`)**: Rely on `InputStream` and `OutputStream`. These are **blocking** by nature; reading a large request body or writing a large response can freeze the execution thread.
*   **Reactive API (`ServerWebExchange`)**: Exposes the body as a **`Flux<DataBuffer>`**. Data is processed as an asynchronous stream. Jackson serializes objects into "chunks" as they become available, enabling efficient memory usage.

### 3. The Handling Pipeline
To process a request, the Dispatcher relies on two key collaborators:
*   **`HandlerMapping`**: The "Map". It identifies which Controller and method should handle the incoming URL. In WebFlux, this lookup is non-blocking.
*   **`HandlerAdapter`**: The "Bridge". It invokes the selected handler. Crucially, in WebFlux, it understands reactive return types (`Mono`/`Flux`) and ensures the result is correctly subscribed to by the framework.

### 4. The Flow Control Engine (Response Buffer)
The **HTTP Response Buffer** acts as the system's "pressure sensor":
1.  **Network Congestion**: If the client is slow, the TCP/IP response buffer fills up.
2.  **Writability Signal**: Netty marks the channel as "not writable".
3.  **Backpressure**: WebFlux detects this and stops requesting data from the `Publisher` (e.g., Database).
4.  **Efficiency**: This ensures the server only produces data as fast as the network can consume it, preventing memory overflow.

