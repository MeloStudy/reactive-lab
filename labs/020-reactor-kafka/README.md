# LAB-020: Reactive Messaging with Apache Kafka

Welcome to LAB-020! In this laboratory, you will build highly scalable, non-blocking event-driven pipelines using **Reactor Kafka**. 

Before starting the exercises, ensure you have read [CONCEPT.md](CONCEPT.md) to understand backpressure, Micro-batching, DLQ patterns, and the new KRaft architecture.

## Infrastructure Setup

We will use the official Apache Kafka image in **KRaft mode**, eliminating the need for Zookeeper.

**Execution**: Start the Kafka cluster natively in detached mode.

```bash
docker-compose up -d
# If using Podman, use the equivalent:
# podman-compose up -d
```

### Command Dissection

| Command / Flag | Explanation |
| :--- | :--- |
| `KAFKA_PROCESS_ROLES: broker,controller` | Configures the node to act as both a data broker and a metadata controller (KRaft mode). |
| `KAFKA_CONTROLLER_QUORUM_VOTERS` | Defines the quorum nodes for KRaft consensus. |

---

## Scenario 1: Non-Blocking Message Production

We use `KafkaSender` to publish a stream of events.

**Implementation**: [src/main/java/com/reactivelab/reactor/kafka/ReactiveProducer.java](src/main/java/com/reactivelab/reactor/kafka/ReactiveProducer.java)
**Test Validation**: [src/test/java/com/reactivelab/reactor/kafka/KafkaTest.java](src/test/java/com/reactivelab/reactor/kafka/KafkaTest.java)

**Execution**:
```bash
mvn test -Dtest=KafkaTest#testScenario1_NonBlockingMessageProduction
```

---

## Scenario 2 & 3: Batching, Backpressure, and DLQ

In these scenarios, we use `KafkaReceiver` to ingest messages. We simulate a processing failure, trigger retries, and route persistent failures to a Dead Letter Queue (DLQ). We also utilize `commitBatchSize` to optimize offset commits.

**Implementation**: [src/main/java/com/reactivelab/reactor/kafka/ReactiveConsumer.java](src/main/java/com/reactivelab/reactor/kafka/ReactiveConsumer.java)

**Execution**:
```bash
mvn test -Dtest=KafkaTest#testScenario2and3_ConsumerBatchingAndDLQ
```

### Code Dissection

| Operator / Concept | Explanation |
| :--- | :--- |
| `commitBatchSize(2)` | Configured in `ReceiverOptions`. Batches `acknowledge()` calls and commits to the broker every 2 messages. |
| `retryWhen(Retry.backoff(...))` | Automatically resubscribes to the upstream if an error occurs, applying exponential backoff between attempts. |
| `onErrorResume()` | Catches errors after retries are exhausted. We use it to route the failed record to the DLQ `KafkaSender`. |

---

## Scenario 4: Complex Backpressure & Prefetching

In this scenario, we enforce strict backpressure using `.limitRate()` to configure how many items the reactive stream pulls from the Kafka polling thread at a time. We also simulate a slow consumer to observe how Reactor Kafka pauses the broker fetching (`KafkaConsumer.pause()`).

**Implementation**: [src/main/java/com/reactivelab/reactor/kafka/ReactiveConsumer.java](src/main/java/com/reactivelab/reactor/kafka/ReactiveConsumer.java)

**Execution**:
```bash
mvn test -Dtest=KafkaTest#testScenario4_PrefetchAndBackpressure
```

### Code Dissection

| Operator / Concept | Explanation |
| :--- | :--- |
| `limitRate(prefetch)` | Overrides the default request amount sent upstream. If set to 1, the stream processes strictly 1 item at a time. |
| `delayElements(Duration)` | Delays the emission of each element, simulating slow downstream processing. |

---

## Self-Assessment

<details>
<summary>1. What happens if you do not call <code>acknowledge()</code> on the <code>ReceiverOffset</code>?</summary>
If you do not acknowledge the offset, Reactor Kafka will not include it in the batch commit. If the consumer restarts, it will consume the same messages again.
</details>

<details>
<summary>2. Why must we use <code>onErrorResume</code> when routing to a DLQ?</summary>
In Project Reactor, an unhandled error terminates the sequence. If a message fails processing and throws an exception, the entire <code>Flux</code> (and thus the Kafka consumer) would die. <code>onErrorResume</code> catches the error, allows us to execute the DLQ send logic, and keeps the stream alive for the next messages.
</details>

---

## Atomic Cleanup

Once you are done with the lab, shut down the infrastructure.

```bash
docker-compose down -v --remove-orphans
```
