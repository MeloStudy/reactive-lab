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
2. **Push vs Pull**: In an Excel spreadsheet, when you change a cell, does the dependent formula "pull" the data or is it "pushed" to it?
3. **The Signal**: What are the three core signals a Reactive Stream can emit? Which one is optional?
4. **The Waiter**: In the Restaurant analogy, who represents the "Event Loop" and who represents the "Callback/Signal"?

---
**Next Step**: Once you've internalized the mindset, move to [LAB-001: The Reactive Manifesto](../001-reactive-manifesto/README.md).
