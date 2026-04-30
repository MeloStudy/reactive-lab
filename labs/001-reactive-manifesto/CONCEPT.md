# Concept: The Reactive Manifesto & Asynchronous Evolution

## 1. The Reactive Manifesto
Reactive systems are the modern standard for building robust, scalable, and responsive software. The Manifesto defines four core pillars:

| Pillar | Description | Implementation in this Lab |
| :--- | :--- | :--- |
| **Responsiveness** | The system responds in a timely manner. | Using non-blocking RxJS streams that don't hang the main thread. |
| **Resilience** | The system stays responsive in the face of failure. | Using the `catchError` operator to intercept exceptions and provide a fallback stream, preventing process crashes. |
| **Elasticity** | The system stays responsive under varying workload. | (Introductory) Decoupling producers from consumers via streams. |
| **Message Driven** | Systems rely on asynchronous message-passing. | Observables acting as message streams between components. |

## 2. Evolution of Asynchrony

### Callbacks (The Legacy)
- **Model**: Push (Single/Multi).
- **Pros**: Simple for very small tasks.
- **Cons**: Callback Hell, difficult error handling, unhandled exceptions can crash the process.

### Promises (The Transition)
- **Model**: Push (Single value).
- **Pros**: Clean chaining (`.then()`), built-in error propagation (`.catch()`).
- **Cons**: Only handles **one** value. Not suitable for streams or progress updates.

### Observables (The Reactive Way)
- **Model**: Push (Multiple values over time).
- **Pros**: Handles streams, cancellations, advanced composition (operators), and robust error lifecycle (`onError` signal).
- **Cons**: Higher learning curve.

## 3. The Observable Anatomy
An Observable is a function that produces a stream of values to an **Observer**.

- **onNext (value)**: Pushes a new data point to the consumer.
- **onError (error)**: Pushes a terminal error signal. No more values will be emitted.
- **onComplete ()**: Pushes a terminal success signal. No more values will be emitted.

### Push vs Pull
- **Pull (Iterators)**: The consumer decides when to get the next value (e.g., `for...of`).
- **Push (Observables)**: The producer decides when to send values to the consumer (e.g., click events, network packets).
