# LAB-015: Reactive Persistence with MongoDB

## Introduction
This lab explores advanced reactive persistence patterns using MongoDB. Unlike relational databases, MongoDB provides native support for push-based notifications and infinite streams through Tailable Cursors and Change Streams.

## Core Concepts

### 1. The Reactive MongoDB Hierarchy
Unlike relational databases where you usually interact directly with an ORM, Spring Data Reactive MongoDB provides three levels of abstraction:
1. **`MongoClient` (Reactive Streams Driver)**: The lowest-level driver from `com.mongodb.reactivestreams.client`. It handles native BSON connections and raw network bytes over Netty. You rarely use this directly in Spring applications, but it's the foundation of all reactive MongoDB operations.
2. **`ReactiveMongoTemplate`**: The mid-level abstraction. Provides granular control for programmatic operations like creating specific collections (e.g., Capped Collections), running aggregations, or watching Change Streams.
3. **`ReactiveMongoRepository`**: The highest-level abstraction. Perfect for standard CRUD and simple `@Tailable` queries, heavily reducing boilerplate.

### 2. Tailable Cursors
A Tailable Cursor is conceptually similar to the `tail -f` command. It allows you to keep a cursor open after the initial data has been consumed, waiting for new documents to be inserted into a **Capped Collection**.
- **Capped Collection**: A fixed-size collection that automatically overwrites oldest documents when it reaches its limit. Required for tailable cursors.
- **UseCase**: System logs, real-time metrics, message queues.

### 3. Change Streams
Change Streams allow applications to access real-time data changes without the complexity and risk of tailing the oplog. They provide a high-level API to watch for insertions, updates, and deletions at the collection, database, or cluster level.
- **Replica Set Required**: Change Streams depend on the replication log (oplog), so a Replica Set (even single-node) is mandatory.
- **UseCase**: Cache invalidation, notification systems, data synchronization.

### 4. GridFS
GridFS is a specification for storing and retrieving files that exceed the BSON-document size limit of 16 MB. In a reactive environment, GridFS allows for non-blocking file streaming.

### 5. Backpressure in Database Streams
In a "Pull" model (e.g., Virtual Threads with blocking JDBC), the app asks the database for data. If the app is slow, it simply doesn't pull.
In a "Push" model (e.g., Reactive MongoDB Tailable Cursors), the database actively pushes bytes over the TCP connection as soon as a document is inserted. If the client is slow to process these logs, the Netty network buffers will fill up. 
To prevent memory exhaustion, you **must** handle backpressure explicitly using operators like `.onBackpressureDrop()` (to discard events you can't handle) or `.onBackpressureBuffer()` (to store them up to a limit).

## Implementation Details

### ReactiveMongoTemplate vs ReactiveMongoRepository
- **Repository**: Best for standard CRUD and `@Tailable` queries.
- **Template**: Best for complex operations like manual collection creation (Capped) and Change Stream management.

### The "Reactive Watcher" Pattern
To propagate Change Stream events to multiple subscribers (e.g., via WebFlux SSE), we use a `Sinks.Many` as a bridge:
1. An `ApplicationRunner` starts a background Change Stream watcher.
2. The watcher emits events into a `Sink`.
3. The REST controller returns the `Sink.asFlux()` to clients.

## Infrastructure
This lab uses **Testcontainers** with a manual Replica Set initiation:
```java
// Enabling replica set via command
.withCommand("--replSet", "rs0", "--bind_ip_all")
// Initiation via mongosh
mongo.execInContainer("mongosh", "--eval", "rs.initiate()");
// Connection with directConnection=true
String url = "mongodb://host:port/test?replicaSet=rs0&directConnection=true";
```

## 6. Evolution: Tailable Cursors vs. Virtual Thread Polling (Java 21+)
With the rise of **Project Loom (Virtual Threads)**, the JVM can now handle millions of blocking threads cheaply. However, Virtual Threads **do not** replace the need for Reactive MongoDB when dealing with infinite streams or events.

- **The Virtual Thread limitations**: If you use a blocking MongoDB driver with a Virtual Thread to wait for a new log or event, you are forced to write a `while(true) { Thread.sleep(...) }` polling loop. Even though sleeping a Virtual Thread is cheap, polling is fundamentally inefficient and increases database load.
- **The Reactive Push Model**: Reactive MongoDB's `@Tailable` and Change Streams operate on a true **push** mechanism. When a document is inserted, the database actively pushes the byte stream over the persistent TCP connection, triggering the Reactor pipeline instantly. Virtual Threads cannot natively replicate this database-level push notification without adopting a reactive driver. Thus, for event-driven NoSQL architectures, Project Reactor remains superior.

## Exercises
1. **Scenario 1**: Implement a log stream using `@Tailable` in a capped collection.
2. **Scenario 2**: Implement real-time product notifications using Change Streams.
3. **Scenario 3**: Implement reactive file storage with GridFS (upload and download).
4. **Scenario 4**: Implement data analytics using the Aggregation Framework.
5. **Scenario 5**: Simulate backpressure handling (`onBackpressureDrop`) on a native stream.
