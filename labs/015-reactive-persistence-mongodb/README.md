# Lab 015: Reactive Persistence with MongoDB

This lab demonstrates how to implement advanced reactive patterns using MongoDB, focusing on real-time data streaming and non-blocking operations.

## Scenarios

### 1. Tailable Cursors (System Logs)
We use a capped collection and the `@Tailable` annotation to create an infinite stream of logs.
- **Endpoint**: `GET /api/mongo/logs/stream`
- **Mechanism**: The cursor remains open, waiting for new documents.

### 2. Change Streams (Product Notifications)
We watch for changes in the `products` collection and broadcast them using a Reactive Sink.
- **Endpoint**: `GET /api/mongo/products/watch`
- **Mechanism**: MongoDB sends notifications on any write operation (Insert/Update/Delete).

### 3. GridFS (Binary Data)
Non-blocking storage and retrieval of large files.
- **Service**: `MongoService.uploadFile`
- **Mechanism**: `ReactiveGridFsTemplate` splits files into chunks and streams them reactively.

### 4. Aggregations (Sales Analytics)
Running complex processing pipelines without blocking the event loop.
- **Endpoint**: `GET /api/mongo/analytics/categories`

## Running Locally

### Prerequisites
- Docker Desktop with active containers.

### Deployment
1. Start the MongoDB Replica Set:
   ```bash
   docker-compose up -d mongodb
   ```
2. Initialize the Replica Set (if not already done):
   ```bash
   docker exec -it mongodb mongosh --eval "rs.initiate()"
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
