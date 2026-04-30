# Lab Specification: [LAB NAME]

**Feature Branch**: `[###-lab-name]`
**Created**: [DATE]
**Status**: Draft
**Syllabus Section**: [e.g., Foundations of Asynchrony / RxJS Basics / Project Reactor Core / Threading & Flow Control / Enterprise Integration / Advanced Patterns & Testing]

## Syllabus Alignment *(mandatory)*

- **Concept**: [What concept from the syllabus is being taught here?]
- **Prerequisites**: [What other labs/concepts must be completed before this one?]
- **Learning Objectives**:
  - LO-001: [Objective 1]
  - LO-002: [Objective 2]

## Interactive Scenarios & Validation *(mandatory)*

### Scenario 1 - [Brief Title] (Priority: P1)

[Describe the interactive lab exercise for this scenario]

**Validation (Automated Test)**: [How will the system automatically verify the learner completed this correctly? e.g., "StepVerifier test expecting onNext('hello') and onComplete signals"]

**Acceptance Scenarios**:

1. **Given** [initial stream state/producer], **When** [learner applies reactive operators/subscription], **Then** [system reflects expected signals via StepVerifier/Jest]

---

### Scenario 2 - [Brief Title] (Priority: P2)

[Describe the interactive lab exercise for this scenario]

**Validation (Automated Test)**: [Describe the Validation test (e.g. Node.js Jest or Java JUnit)]

---

## Educational Requirements *(mandatory)*

### Concepts to Explain

- **EX-001**: [Concept 1, e.g., "The Observer Pattern and Reactive Streams API"]
- **EX-002**: [Concept 2, e.g., "The difference between Cold and Hot Publishers"]

### Technical Requirements

- **TR-001**: Lab infrastructure (e.g., brokers, databases) MUST be containerized strictly using **Docker / Docker Compose**.
- **TR-002**: Lab README MUST provide native orchestration and execution commands (e.g., `mvn test`, `npm test`) step-by-step. Bash scripts as wrappers are PROHIBITED.
- **TR-003**: Lab MUST include automated validation tests (Node.js/Jest for RxJS labs, or Java/JUnit for Reactor/WebFlux labs).
- **TR-004**: Reactive signals (`onNext`, `onError`, `onComplete`) MUST be explicitly validated in tests (e.g., using `StepVerifier`).
- **TR-005**: Lab README MUST provide a "Command Dissection" for any new operator or CLI flag introduced.
- **TR-006**: Theoretical context (Event Loop, Reactive Streams API) MUST be provided in a `CONCEPT.md` file.
- **TR-007**: Lab MUST explicitly instruct "Atomic Cleanup" via native commands.
- **TR-008**: An optional Makefile MAY be provided strictly as an automated shortcut container.

## Success Criteria *(measurable outcomes)*

- **SC-001**: [e.g., Learner successfully builds a reactive pipeline to process 1000 events/sec]
- **SC-002**: [e.g., All validation tests pass upon completion]

## Assumptions

- [e.g., Learner understands basic JavaScript/Java syntax and asynchronous concepts]
- [e.g., Docker Desktop and Node.js v20+ are installed]

