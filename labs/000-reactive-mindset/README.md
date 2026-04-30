# LAB-000: The Reactive Mindset & Foundational Analogies

Welcome to your first step into Reactive Programming! Before we write complex code, we need to shift our perspective from **Imperative (How)** to **Reactive (What)**.

## 🎯 Learning Objectives
- Internalize the **Push** vs **Pull** model.
- Understand the cost of **Blocking I/O**.
- Learn why **Errors** are just another type of signal.
- Master the **Excel** and **Restaurant** analogies.

## 🧠 Theory First
Read the [CONCEPT.md](./CONCEPT.md) file for a deep dive into the philosophy of this paradigm and the comparison with the traditional Java "Thread-per-request" model.

## 🛠️ Hands-on: Push vs. Pull

In this lab, we have a simple demonstration comparing a standard variable (Pull) with a Reactive Stream (Push).

### 1. Install Dependencies
```bash
npm install
```

### 2. Run the Comparison
```bash
node src/push-vs-pull.js
```

### 3. Run Validation Tests
```bash
npm test
```

## 🔍 Command Dissection: `npm test`
- **npm**: The Node Package Manager.
- **test**: A script defined in `package.json` that runs `jest`.
- **jest**: Our testing framework that validates if your mental model matches the actual behavior of the streams.

---
**Next Step**: Once you've internalized the mindset, move to [LAB-001: The Reactive Manifesto](../001-reactive-manifesto/README.md).
