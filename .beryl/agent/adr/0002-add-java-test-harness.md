# ADR 0002: Add a standard-library Java test harness

## Status

Accepted

## Context

The project needs deterministic behavior tests for valid and invalid console
commands but has no configured build tool or external test framework.

## Decision

Use a Java assertion harness in `tests/OdysseusTest.java`, launched by
`bash tests/run-tests.sh`. Configure the affected-test gate to run that script
for source and test changes.

## Consequences

* **Benefit:** Tests run without downloading dependencies and cover console
  flows that interleave invalid and valid commands.
* **Tradeoff:** Assertions and test discovery are maintained manually until a
  build tool is introduced.
* **Follow-up:** Replace the harness only when the project adopts a build tool
  and a test framework.
