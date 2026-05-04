# LAB-010: Testing & Debugging Matrix

Welcome to the Testing and Debugging Matrix. In this lab, you will learn how to verify the invisible and debug the asynchronous.

## 🎯 Learning Objectives
- Use **Virtual Time** to test long-duration streams instantly.
- Use **PublisherProbes** to verify branching logic.
- Master **Reactor Context** for state propagation.
- Configure **BlockHound** to protect the Event Loop.

## 🛠️ Command Dissection

| Feature | Operator/Method | Purpose |
| :--- | :--- | :--- |
| **Virtual Time** | `StepVerifier.withVirtualTime(() -> ...)` | Replaces the default scheduler with a virtual one. |
| **Time Travel** | `.thenAwait(Duration)` | Advances the virtual clock by the specified duration. |
| **Probing** | `PublisherProbe.of(publisher)` | Wraps a publisher to monitor its lifecycle. |
| **Debug Hooks** | `Hooks.onOperatorDebug()` | Captures assembly-time stack traces globally. |
| **Blocking Detection** | `BlockHound.install()` | Installs the agent to detect illegal blocking calls. |
| **Context** | `.contextWrite(Context)` | Adds key-value pairs to the pipeline context. |

## 🚀 Hands-On: Running the Matrix

1.  **Examine the Tests**: Open `src/test/java/com/reactivelab/testing/TestingMatrixTest.java`.
2.  **Observe Virtual Time**: Note how `scenario1` tests a 24-hour stream in milliseconds.
3.  **Trigger BlockHound**: In `scenario4`, notice how a `Thread.sleep` on a parallel scheduler is caught and throws a `BlockingOperationError`.

### Native Execution
Run the tests using Maven:
```bash
mvn test -pl labs/010-testing-debugging-matrix
```

> [!IMPORTANT]
> BlockHound requires the JVM flag `-XX:+AllowRedefinitionToAddDeleteMethods` on JDK 13+. This is already configured in this lab's `pom.xml`.

## 🧹 Cleanup
The lab uses native JUnit and Maven. Cleanup is automatic after the JVM exits.
To clean build artifacts:
```bash
mvn clean -pl labs/010-testing-debugging-matrix
```
