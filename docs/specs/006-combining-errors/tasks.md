# Tasks: LAB-006 Refinement

## 1. Preparation & Refactoring
- [x] Rename/Refactor existing classes to fix scenario numbering in Javadocs.
- [x] Ensure `ReliableService` is properly integrated into the project structure.

## 2. Implementation Phase
- [x] Create `CollectionProcessor.java` (Scenario 9).
- [x] Create `EventGrouper.java` (Scenario 10).
- [x] Create `ResourceSafetyService.java` (Scenario 11).

## 3. Documentation Phase
- [x] Rewrite `CONCEPT.md`
  - [x] Fix section numbering.
  - [x] Add "Grouping & Collections" section.
  - [x] Add "Resource Safety" section.
- [x] Update `README.md`
  - [x] Add Categories (Orchestration, Aggregation, etc.).
  - [x] Link all 11 scenarios to code.
  - [x] Update Command Dissection with new operators.

## 4. Validation Phase
- [x] Create `CollectionProcessorTest.java`.
- [x] Create `EventGrouperTest.java`.
- [x] Update `ResourceSafetyTest.java`.
- [x] Run `mvn test -pl labs/006-combining-errors`.
- [x] Perform final audit against `constitution.md`.
