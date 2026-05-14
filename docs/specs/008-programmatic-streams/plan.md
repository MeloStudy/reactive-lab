# Implementation Plan: LAB-008: Programmatic Stream Generation & Hot/Cold

**Branch**: `008-programmatic-streams` | **Date**: 2026-05-03
**Input**: Specification from `/docs/specs/008-programmatic-streams/spec.md`

## Phase 1: Project Setup
1. Create `labs/008-programmatic-streams`.
2. Package: `com.reactivelab.generation`.

## Phase 2: Generating & Bridging
1. **Scenario 1 (generate)**: Implement `SequenceGenerator` (Fibonacci).
2. **Scenario 2 (create)**: Implement `ChatBridge` (Callback wrap).

## Phase 3: Hot vs Cold Mechanics (New)
1. **Scenario 3 (Hot/Cold)**: Implement `Broadcaster`.
2. **Comparison**:
   - Create a Cold `Flux.interval`.
   - Create a Hot `Flux` using `publish().autoConnect()`.
3. **Test**: Use `VirtualTime` and two subscribers with a delay to prove data loss in Hot Flux and data restart in Cold Flux.

## Phase 4: Connection Lifecycle Management (New)
1. **Scenario 4 (refCount)**: Implement `OnDemandResource`.
2. **Implementation**: Use `publish().refCount(2)` to require two subscribers.
3. **Test**: Verify the source only starts when the second subscriber arrives.

## Phase 5: Sinks & Caching
1. **Scenario 5 (Sinks)**: Implement `NotificationBus`.
2. **Scenario 6 (Cache)**: Implement `ResultCache`.

## Phase 6: Documentation & Assessment
1. **CONCEPT.md**: Explain the "Movie vs Concert" analogy for Cold/Hot.
2. **README.md**: 
   - Command Dissection for `publish`, `refCount`, and `Sinks`.
   - Updated **Knowledge Check** section.

## Phase 7: Refinement (Constitution Audit v0.2.7)
1. **Logging**: Add `@Slf4j` and use `.log()` for signal visibility.
2. **Deep Theory**: Expand `CONCEPT.md` to cover Virtual Threads and Sinks thread-safety.
3. **Traceable Implementation**: Update `README.md` with relative links (from root) to:
    - [SequenceGenerator.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/SequenceGenerator.java) / [SequenceGeneratorTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/SequenceGeneratorTest.java)
    - [ChatBridge.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/ChatBridge.java) / [ChatBridgeTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/ChatBridgeTest.java)
    - [Broadcaster.java](/labs/008-programmatic-streams/src/main/java/com/reactivelab/generation/Broadcaster.java) / [BroadcasterTest.java](/labs/008-programmatic-streams/src/test/java/com/reactivelab/generation/BroadcasterTest.java)
4. **Lifecycle Validation**: Enhance tests for `ChatBridge` to verify `unregister` logic.
5. **Advanced Sinks**: Implement `SinksDeepDiveTest.java` to explore multicasting race conditions.
6. **Final Audit**: Move status to `AUDITED`.

## Constitution Compliance Check
- [x] Java 21+ syntax.
- [x] `StepVerifier` for everything.
- [x] Self-Assessment quiz included.
- [x] SLF4J Logging implemented.
- [x] No over-simplified concepts in documentation.
