---
description: Orchestrates the entire execution phase from coding to final pedagogical certification.
version: 1.0.0
---

# Workflow: Lab Master Build

This is a macro-workflow that combines **Builder** and **Auditor** phases. Use this when you have a `READY` lab and want to deliver the final product (code + docs + quality gate) in one session.

## Steps:

1. **Phase 1: Implementation**:
   - Run the logic of `/lab-builder`.
   - Implement the reactive infrastructure, stream seeding, and TDD-based validation.
   - Generate all documentation (`README.md`, `CONCEPT.md`).
   - Set status to **`IMPLEMENTED`**.

2. **Phase 2: Quality Audit**:
   - Run the logic of `/lab-auditor`.
   - Perform a gap audit to ensure theory matches practice.
   - Verify instructions clarity and technical precision.
   - Set status to **`AUDITED`** and mark as `Completed` in Syllabus.

3. **Outcome**:
   - A fully implemented, tested, and pedagogically certified laboratory.
