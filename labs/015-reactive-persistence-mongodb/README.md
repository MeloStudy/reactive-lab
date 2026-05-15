# Lab 015: Reactive Persistence with MongoDB

This lab demonstrates how to implement advanced reactive patterns using MongoDB, focusing on real-time data streaming and non-blocking operations.

## Scenarios

### 1. Tailable Cursors (System Logs)
We use a capped collection and the `@Tailable` annotation to create an infinite stream of logs.
- **Endpoint**: `GET /api/mongo/logs/stream`
- **Mechanism**: The cursor remains open, waiting for new documents.
🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/mongodb/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/mongodb/PersistenceIntegrationTest.java)

### 2. Change Streams (Product Notifications)
We watch for changes in the `products` collection and broadcast them using a Reactive Sink.
- **Endpoint**: `GET /api/mongo/products/watch`
- **Mechanism**: MongoDB sends notifications on any write operation (Insert/Update/Delete).
🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/mongodb/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/mongodb/PersistenceIntegrationTest.java)

### 3. GridFS (Binary Data)
Non-blocking storage and retrieval of large files.
- **Service**: `MongoService.uploadFile`
- **Mechanism**: `ReactiveGridFsTemplate` splits files into chunks and streams them reactively.
🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/mongodb/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/mongodb/PersistenceIntegrationTest.java)

### 4. Aggregations (Sales Analytics)
Running complex processing pipelines without blocking the event loop.
- **Endpoint**: `GET /api/mongo/analytics/categories`
🔗 **Traceable Implementation**: [PersistenceLogic.java](src/main/java/com/reactivelab/mongodb/PersistenceLogic.java) | [Test Suite](src/test/java/com/reactivelab/mongodb/PersistenceIntegrationTest.java)

## 🧠 Self-Assessment
<details>
<summary>1. What is the fundamental difference between a regular query and a Tailable Cursor?</summary>
A regular query returns a finite result set and closes the connection. A Tailable Cursor remains open after returning initial results, passively waiting for and pushing new documents as they are inserted.
</details>

<details>
<summary>2. Why are Virtual Threads insufficient for replicating MongoDB Change Streams without blocking?</summary>
Virtual Threads are designed for cheap blocking. To replicate a "push" notification (Change Stream) using a standard blocking driver and Virtual Threads, you would have to write an active polling <code>while(true)</code> loop, which is fundamentally inefficient compared to a true reactive push model where the database actively signals the application.
</details>

<details>
<summary>3. What is a prerequisite for using Tailable Cursors in MongoDB?</summary>
The collection must be configured as a <strong>Capped Collection</strong> (a fixed-size collection that overwrites oldest documents).
</details>

## Running Locally

### Prerequisites
- **Docker Desktop** or **Podman** with active container support.

### Deployment
1. Start the MongoDB Replica Set:
   ```bash
   # Using Docker
   docker-compose up -d mongodb

   # Using Podman
   podman-compose up -d mongodb
   ```
2. Initialize the Replica Set (if not already done):
   ```bash
   # Using Docker
   docker exec -it mongodb mongosh --eval "rs.initiate()"

   # Using Podman
   podman exec -it mongodb mongosh --eval "rs.initiate()"
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run -pl labs/015-reactive-persistence-mongodb
   ```

## Command Dissection

### Creating a Capped Collection Programmatically
```java
mongoTemplate.createCollection(collectionName,
    CollectionOptions.empty().capped().size(1024 * 1024).maxDocuments(1000));
```
- `capped()`: Marks the collection as fixed-size.
- `size()`: Total size in bytes.
- `maxDocuments()`: Maximum document count.

### Watching a Collection
```java
mongoTemplate.changeStream(Product.class)
    .watchCollection("products")
    .listen()
    .subscribe(event -> ...);
```
- `listen()`: Returns a `Flux<ChangeStreamEvent<T>>`.
- `event.getBody()`: Returns the actual document changed.
