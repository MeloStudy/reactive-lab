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

### Scenario 2: High-Priority Real-Time Data
You are receiving sensor data where only the most recent value is useful for the dashboard.
- **Task**: Discard stale data using `onBackpressureLatest()`.
- **Key Operator**: `onBackpressureLatest()`.

### Scenario 3: The API Quota (Rate Limiting)
Your downstream service has a strict rate limit. You must regulate how much you request from your high-volume source.
- **Task**: Implement `ThrottledRequester` using `limitRate(10)`.
- **Key Operator**: `limitRate(10)`.

### Scenario 4: The Subscription Cap
You want to allow a user to consume exactly `N` items from a stream and then terminate the connection.
- **Task**: Implement `QuotaEnforcer` using `limitRequest(5)`.
- **Key Operator**: `limitRequest(5)`.

---

## 🧠 Self-Assessment Quiz

1. **Why is `onBackpressureBuffer()` risky for infinite streams?**
   - *Answer: Without a size limit or a drop strategy, the buffer can grow until it causes an `OutOfMemoryError`.*

2. **What is the default "replenishment threshold" for `limitRate(100)`?**
   - *Answer: 75 items (75% of the prefetch amount).*

3. **How does `onBackpressureLatest` differ from `onBackpressureDrop`?**
   - *Answer: `onBackpressureDrop` discards everything when demand is zero. `onBackpressureLatest` discards previous items but always keeps the single most recent emission ready for the next request.*

4. **Is `limitRate` a push or pull mechanism?**
   - *Answer: It is a pull mechanism; it explicitly manipulates the `request(n)` signal sent upstream.*

5. **What happens to the stream after `limitRequest(n)` reaches its limit?**
   - *Answer: It emits an `onComplete` signal and cancels the upstream subscription.*

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
