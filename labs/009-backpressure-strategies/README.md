# LAB-009: Backpressure Strategies & Rate Limiting 🛡️⚡

In a high-performance reactive system, "speed mismatches" are inevitable. This laboratory focuses on the critical engineering skill of flow control: protecting your system from being overwhelmed by fast producers while maintaining high throughput.

## 🎯 Learning Objectives
- **Visualize** and reproduce a `BackpressureOverflowException`.
- **Apply Overflow Strategies**: `onBackpressureBuffer`, `onBackpressureDrop`, and `onBackpressureLatest`.
- **Master Buffer Strategies**: Configure `DROP_OLDEST` and `DROP_LATEST` behaviors.
- **Implement Rate Limiting**: Use `limitRate` to control prefetch and replenishment thresholds.
- **Enforce Quotas**: Use `limitRequest` to cap total stream consumption.

---

## 🛠️ Command Dissection

### 1. `onBackpressureBuffer(size, overflowStrategy)`
Protects the downstream by queuing items.
- `size`: The maximum number of items to hold in memory.
- `overflowStrategy`: What to do when the buffer is full (e.g., `BufferOverflowStrategy.DROP_OLDEST`).

### 2. `limitRate(n)`
Tells the upstream exactly how many items it is allowed to send.
- `n`: The prefetch amount.
- **Replenishment**: By default, it requests more data when **75%** of the current batch is consumed.

---

## 🧪 Scenarios

### Scenario 1: The Overflowing Producer
A fast producer (`Sinks.Many`) pushes events regardless of consumer demand.
- **Task**: Reproduce the `OverflowException` and fix it by adding a buffer.
- **Key Operator**: `onBackpressureBuffer(10)`.
- **Source**: [FastProducer.java](src/main/java/com/reactivelab/backpressure/FastProducer.java)
- **Test**: [BackpressureTest.java](src/test/java/com/reactivelab/backpressure/BackpressureTest.java)

### Scenario 2: High-Priority Real-Time Data
You are receiving sensor data where only the most recent value is useful for the dashboard.
- **Task**: Discard stale data using `onBackpressureLatest()`.
- **Key Operator**: `onBackpressureLatest()`.
- **Test**: [BackpressureTest.java](src/test/java/com/reactivelab/backpressure/BackpressureTest.java)

### Scenario 3: The API Quota (Rate Limiting)
Your downstream service has a strict rate limit. You must regulate how much you request from your high-volume source.
- **Task**: Implement `ThrottledRequester` using `limitRate(10)`.
- **Key Operator**: `limitRate(10)`.
- **Source**: [ThrottledRequester.java](src/main/java/com/reactivelab/backpressure/ThrottledRequester.java)
- **Test**: [ThrottledRequesterTest.java](src/test/java/com/reactivelab/backpressure/ThrottledRequesterTest.java)

### Scenario 4: The Subscription Cap
You want to allow a user to consume exactly `N` items from a stream and then terminate the connection.
- **Task**: Implement `QuotaEnforcer` using `limitRequest(5)`.
- **Key Operator**: `limitRequest(5)`.
- **Source**: [QuotaEnforcer.java](src/main/java/com/reactivelab/backpressure/QuotaEnforcer.java)
- **Test**: [QuotaEnforcerTest.java](src/test/java/com/reactivelab/backpressure/QuotaEnforcerTest.java)

---

## 🧠 Self-Assessment Quiz

1. **Why is `onBackpressureBuffer()` risky for infinite streams?**
   <details>
   <summary>💡 View Answer</summary>
   Without a size limit or a drop strategy, the buffer can grow until it causes an `OutOfMemoryError`.
   </details>

2. **What is the default "replenishment threshold" for `limitRate(100)`?**
   <details>
   <summary>💡 View Answer</summary>
   75 items (75% of the prefetch amount).
   </details>

3. **How does `onBackpressureLatest` differ from `onBackpressureDrop`?**
   <details>
   <summary>💡 View Answer</summary>
   `onBackpressureDrop` discards everything when demand is zero. `onBackpressureLatest` discards previous items but always keeps the single most recent emission ready for the next request.
   </details>

4. **Is `limitRate` a push or pull mechanism?**
   <details>
   <summary>💡 View Answer</summary>
   It is a pull mechanism; it explicitly manipulates the `request(n)` signal sent upstream.
   </details>

5. **What happens to the stream after `limitRequest(n)` reaches its limit?**
   <details>
   <summary>💡 View Answer</summary>
   It emits an `onComplete` signal and cancels the upstream subscription.
   </details>

6. **Do Virtual Threads (Java 21+) replace the need for backpressure?**
   <details>
   <summary>💡 View Answer</summary>
   **No**. Virtual threads allow for more concurrent blockers, but they do not protect your memory or downstream systems (DBs, APIs) from being overwhelmed by a fast producer. Backpressure is still required for system stability.
   </details>

---

## ⚠️ Troubleshooting

### `BackpressureOverflowException`
- **Cause**: A producer is emitting data faster than the consumer can process, and the `onBackpressureBuffer` size limit has been reached.
- **Fix**: Increase the buffer size, use a drop strategy (`DROP_OLDEST`), or optimize the downstream processing logic.

### Upstream is too slow
- **Cause**: If you use `limitRate(n)`, the operator will only request `n` items. If your source only emits when requested, ensure your `n` is large enough for your throughput needs.
- **Check**: Use `.log()` to see the `request(n)` signals moving upstream.

### Virtual Threads and Stalling
- **Note**: If you are using Virtual Threads and see stalling, ensure you aren't pinning the carrier thread with `synchronized` blocks or native calls while processing reactive signals.

---

## 🚀 How to Run
```bash
# Run all flow control validation tests
mvn test -pl labs/009-backpressure-strategies
```

## 🧹 Cleanup
```bash
mvn clean -pl labs/009-backpressure-strategies
```

---
**Next Lab**: [LAB-010: Testing & Debugging Matrix](../010-testing-debugging-matrix/README.md)
