# A-MoreTesting second-increment notes

Date: 2026-09-14

## Verification record

- Java 25.0.4 was active for the Gradle checks.
- `check` and `jacocoTestReport` passed with 267 tests and one environment-dependent skip.
- The skipped test is the existing symbolic-link status case; symbolic links are unavailable on this host.
- All six console plans passed through the `test-ui` skill, with separate session logs.
- The GUI and OS matrix remains pending because this environment has no interactive JavaFX session.
- A post-audit Gradle rerun was blocked before test compilation by a Java 25.0.4
  Windows ZIP-filesystem cleanup `AccessDeniedException` on a cached dependency;
  Checkstyle and the base console plan passed after the audit edits.

## Coverage ledger

JaCoCo reports 80.20% line coverage, 81.96% instruction coverage, and
77.45% branch coverage. These figures are diagnostic only; no percentage gate
was added.

| Area | Covered by this increment | Remaining classification |
| --- | --- | --- |
| Deterministic core | Boundary, Unicode, order, and adapter cases | Defensive or unreachable branches |
| JavaFX and platform | GUI matrix | Manual-only behavior |
| `Herta` fallbacks | Startup, EOF, invalid/restart flows | Needs an approved seam |
| Storage and recovery | Locks, sizes, paths, snapshots, cleanup | Platform or rollback seam |

No production behavior was changed in this increment. Existing duplicate-task
behavior remains executable behavior; the README contradiction is deferred to
a separate documentation/behavior decision.
