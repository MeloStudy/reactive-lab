# CONCEPT: Reactive Persistence with R2DBC

Relational databases have traditionally been accessed via JDBC, which is a blocking API. In a reactive system, blocking a thread while waiting for a database response is unacceptable. **R2DBC** (Reactive Relational Database Connectivity) was created to bring non-blocking database access to relational databases.

## 1. Why R2DBC? (The Blocking Problem)
Traditional relational database access via **JDBC** (Java Database Connectivity) is inherently synchronous. In a standard JDBC-based application (using Spring Data JPA or plain JDBC):
- **Thread-per-Connection**: Every database connection requires a dedicated OS thread.
- **Blocking I/O**: When a thread executes a query, it blocks and enters a "waiting" state until the database returns the result.
- **Resource Exhaustion**: To handle high concurrency, you need a large thread pool (e.g., **HikariCP**). However, context switching between thousands of threads becomes a bottleneck, and memory consumption (1MB per thread stack) limits scalability.

**R2DBC** (Reactive Relational Database Connectivity) breaks this model by implementing a non-blocking wire protocol.

### 2. The R2DBC Mechanism (Netty & TCP)
Instead of allocating a thread per database connection, R2DBC drivers (such as `r2dbc-postgresql`) are built entirely on top of **Netty** (using Reactor Netty).
- **Asynchronous TCP Write**: When a reactive application issues a query, the SQL string is serialized into PostgreSQL wire-protocol packet bytes. Netty writes these bytes directly to the socket channel buffer in a non-blocking way.
- **Event Loop Multiplexing**: A single Netty Event Loop worker thread manages multiple concurrent active connections (channels). Once the write completes, the worker thread is immediately returned to the OS to process other requests, rather than sitting in a "waiting" state.
- **Protocol Callbacks**: When the database server finishes processing and pushes result packets back over the network socket, Netty intercepts the incoming TCP bytes. The Event Loop thread parses the PostgreSQL protocol messages (e.g., `DataRow`, `CommandComplete`) and publishes them as stream signals (`onNext`, `onComplete`) directly to the subscriber.

---

## 3. R2DBC Architectural Stack: The Three Tiers

To understand how a database operation executes in a reactive application, we must look at the three distinct tiers of the R2DBC specification:

```
+-------------------------------------------------------------+
| 1. CLIENT LEVEL (Spring Data R2DBC Abstractions)            |
|    - Abstractions: ReactiveCrudRepository, DatabaseClient   |
|    - Goal: Developer productivity & type-safe mapping       |
+-------------------------------------------------------------+
                              | (Declares Reactive Streams)
                              v
+-------------------------------------------------------------+
| 2. POOL LEVEL (r2dbc-pool implementation)                   |
|    - Abstractions: ConnectionPool, Pool Configuration       |
|    - Goal: Manage active socket limits & non-blocking lease |
+-------------------------------------------------------------+
                              | (Leases Connection asynchronously)
                              v
+-------------------------------------------------------------+
| 3. SPI LEVEL (r2dbc-spi Core Interfaces & Drivers)          |
|    - Abstractions: ConnectionFactory, Connection, Statement |
|    - Goal: Database wire-protocol communication via Netty   |
+-------------------------------------------------------------+
```

### A. The SPI Level (`r2dbc-spi`)
This is the **Service Provider Interface**. It is the absolute core driver specification designed for database driver vendors (PostgreSQL, H2, MS SQL, MySQL). 
- Defined in the `io.r2dbc.spi` package.
- Contains standard interfaces like `ConnectionFactory`, `Connection`, `Statement`, `Result`, and `Row`.
- Its role is identical to the JDBC Driver API, but it enforces reactive stream contracts (`Publisher` inputs and outputs) on all database communications.

### B. The Pool Level (`r2dbc-pool`)
Just as JDBC requires connection pool managers like HikariCP to avoid opening new physical TCP connections repeatedly, R2DBC relies on `r2dbc-pool`.
- It wraps the raw SPI `ConnectionFactory` to maintain a pool of warm, active TCP connections.
- **No Thread Blocking**: Unlike HikariCP, which blocks the calling thread if the pool is full until a connection is returned, `r2dbc-pool` registers a non-blocking queue. When you request a connection, it returns a `Mono<Connection>`, yielding the socket asynchronously as soon as one becomes free.

### C. The Client Level (`spring-data-r2dbc` / Spring Core)
This is the developer-facing level. Typing raw SPI commands is verbose and complex. Spring Data R2DBC provides high-level abstractions:
- **`ReactiveCrudRepository`**: Type-safe, declarative CRUD interfaces.
- **`R2dbcEntityTemplate`**: Fluent API for dynamic criteria-based querying.
- **`DatabaseClient`**: Dynamic client exposing raw SQL executions and bindings.

---

## 4. Deep Dive: `ConnectionFactory` vs. JDBC `DataSource`

The primary interface differences highlight why R2DBC scales under high load:

| Feature | JDBC `DataSource` | R2DBC `ConnectionFactory` |
| :--- | :--- | :--- |
| **Method Signature** | `Connection getConnection() throws SQLException` | `Publisher<? extends Connection> create()` |
| **Thread Blockage** | **Yes**: The thread blocks and parks until a database socket is fully allocated. | **No**: Returns a `Publisher` immediately; connections are leased and pushed down the stream asynchronously. |
| **Transaction State** | Bound to the executing thread using `ThreadLocal`. | Bound to the reactive stream using Reactor **Context**. |
| **Wire Execution** | Blocking OS Socket write/read. | Non-blocking Event-driven TCP sockets (Netty). |
| **Pooling Provider** | HikariCP, Tomcat Pool (synchronous). | `r2dbc-pool` (fully reactive, non-blocking queue). |

---

## 5. The Death of JPA & Hibernate (The Shift to Basic ORM)
A common misconception is that you can use **JPA** or **Hibernate** with R2DBC. **You cannot.**
- **Blocking by Design**: JPA is a blocking specification. Hibernate's core mechanisms (Lazy Loading, Dirty Checking, First-level Cache) rely heavily on `ThreadLocal` and synchronous execution.
- **Lazy Loading**: If you access a lazy-loaded collection outside a transaction, Hibernate blocks to fetch it. This is impossible in a non-blocking pipeline where no thread can be held hostage.
- **The Alternative: Basic ORM**: Spring Data R2DBC is a **Basic ORM**. Unlike a full-featured ORM, it is a "Mapping-only" layer.
    - **No Session/Persistence Context**: There is no "Dirty Checking". You must explicitly save changes.
    - **No Lazy Loading**: All relationships must be handled via explicit joins or separate queries.
    - **No Proxying magic**: You work with real POJOs, not Hibernate proxies.
    - **Transparency over Magic**: You have more control over the SQL, but you lose the "automagic" features of Hibernate. This is a deliberate trade-off for high-performance, predictable reactive systems.

---

## 6. The Spring Data R2DBC Template Hierarchy
Spring Data R2DBC provides three levels of abstraction for database access, each suited for different use cases:

1. **ReactiveCrudRepository (High-level)**:
    - Best for standard CRUD and simple derived queries (e.g., `findByEmail`).
    - Minimizes boilerplate code.
    - Uses `@Table` and `@Id` annotations for mapping.

2. **R2dbcEntityTemplate (Mid-level)**:
    - Provides a programmatic, fluent API for query construction.
    - Best for dynamic queries where SQL strings are hard to maintain.
    - Conceptually similar to the JPA Criteria API but non-blocking.
    - **Example**: `template.select(Product.class).matching(query(where("price").lt(100))).all()`

3. **DatabaseClient (Low-level)**:
    - Provides maximum flexibility for raw SQL execution.
    - Best for complex joins, PostgreSQL-specific features (like `JSONB` operators), or high-performance bulk operations.
    - Requires manual row mapping (though it can use `BeanPropertyRowMapper` equivalents).
    - **Example**: `client.sql("SELECT * FROM products WHERE name ILIKE :name").bind("name", "%prod%").fetch().all()`

---

## 7. Reactive Transactions
Transactions in WebFlux are different. Since there is no `ThreadLocal` storage for the transaction state, Spring uses the **Reactor Context** to propagate the transaction across the pipeline. 
When you annotate a method with `@Transactional`, the transaction is bound to the reactive stream. If the stream is cancelled or emits an error, the transaction is automatically rolled back.

---

## 8. PostgreSQL JSONB Support
One of the advantages of using PostgreSQL with R2DBC is the ability to handle **JSONB** columns without blocking. This allows you to combine the power of relational data with the flexibility of document-based data in a single, non-blocking pipeline.

---

## 9. Observability with R2DBC Proxy
Since we don't have the traditional JDBC interceptors, we use `r2dbc-proxy`. This allows us to wrap the `ConnectionFactory` and listen to query execution events, providing visibility into the generated SQL and execution times without introducing blocking calls.

---

## 10. Evolution: R2DBC vs. JDBC + Virtual Threads (Java 21+)
With the introduction of **Project Loom (Virtual Threads)**, the debate around non-blocking database access has evolved. Since Virtual Threads make blocking calls extremely cheap (millions of threads can exist simultaneously without consuming significant RAM), many argue that standard JDBC over Virtual Threads is sufficient.

- **JDBC + Virtual Threads**: Excellent for simple request-response flows. The virtual thread blocks during the DB call, but the underlying OS carrier thread is freed. It drastically simplifies code readability.
- **When to use R2DBC**: R2DBC remains the superior choice for **streaming large datasets** directly from the database to the client via WebFlux (SSE or NDJSON). R2DBC inherently supports reactive backpressure, meaning if the HTTP client reads data slowly, R2DBC will automatically slow down the database retrieval, preventing memory `OutOfMemoryError`s. JDBC, even with Virtual Threads, pulls the entire result set into memory or blocks unpredictably during cursor iterations.
