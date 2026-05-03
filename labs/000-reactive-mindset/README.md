# LAB-000: The Reactive Mindset & Foundational Analogies

Welcome to your first step into Reactive Programming! Before we write complex code, we need to shift our perspective from **Imperative (How)** to **Reactive (What)**.

## 🎯 Learning Objectives
- Internalize the **Push** vs **Pull** model.
- Understand the cost of **Blocking I/O** in the JVM (Thread-per-request).
- Learn why **Errors** are just another type of signal.
- Master the **Excel** and **Restaurant** analogies.

## 🧠 Theory & Mindset
Read the [CONCEPT.md](./CONCEPT.md) file for a deep dive into the philosophy of this paradigm and the comparison with the traditional Java "Thread-per-request" model.

This lab is purely conceptual to ensure you have the right mindset before we dive into Project Reactor code in the following modules.

## 📝 Mindset Check (Self-Assessment)

Reflect on the following questions based on the concepts in `CONCEPT.md`:

1. **The Bottleneck**: If a thread in a standard Java Servlet container calls `Thread.sleep(1000)`, is that thread doing useful work? How does this relate to "Thread Exhaustion"?
   <details>
   <summary>💡 View Answer</summary>
   No, the thread is **blocked**. It is not performing useful work but still consumes system resources (like stack memory). If many threads block simultaneously, the server runs out of available threads for new requests, known as **Thread Exhaustion**.
   </details>

2. **Push vs Pull**: In an Excel spreadsheet, when you change a cell, does the dependent formula "pull" the data or is it "pushed" to it?
   <details>
   <summary>💡 View Answer</summary>
   It is **Push**. You don't have to ask the dependent cell to update; the change in the original cell triggers a signal that "pushes" the new value to all connected formulas.
   </details>

3. **The Signal**: What are the three core signals a Reactive Stream can emit? Which one is optional?
   <details>
   <summary>💡 View Answer</summary>
   The signals are: `onNext` (Data), `onError` (Error), and `onComplete` (Completion). Technically, `onNext` is optional (a stream can be empty), and there can be at most one terminal signal (`onError` or `onComplete`).
   </details>

4. **The Waiter**: In the Restaurant analogy, who represents the "Event Loop" and who represents the "Callback/Signal"?
   <details>
   <summary>💡 View Answer</summary>
   The **Waiter** represents the **Event Loop** (always available to take new orders). The **Bell** from the kitchen (or the notification that food is ready) represents the **Callback** or the **Signal** indicating that the asynchronous task is finished.
   </details>

---
**Next Step**: Once you've internalized the mindset, move to [LAB-001: The Reactive Manifesto](../001-reactive-manifesto/README.md).
