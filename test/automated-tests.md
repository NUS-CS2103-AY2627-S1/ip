# Automated test coverage

Run tests and coverage using Java 25:

```sh
./gradlew test jacocoTestReport checkstyleMain checkstyleTest
```

On macOS, select `sdk use java 25.0.3.fx-zulu` first if needed.
The first coverage run downloads JaCoCo from Maven Central. JaCoCo 0.8.14 is
pinned because it [supports Java 25](https://www.jacoco.org/jacoco/trunk/doc/changes.html).
The report uses the [Gradle JaCoCo plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html).

Open `build/reports/tests/test/index.html` for individual JUnit results and
`build/reports/jacoco/test/html/index.html` for coverage. Machine-readable
coverage is in `build/reports/jacoco/test/jacocoTestReport.xml`.
These generated files are ignored by Git.

## Coverage snapshot — 2026-09-11

All 97 JUnit tests passed, with no skipped tests, using Java 25.0.3 on macOS.
The previous suite contained 51 tests.

| Metric | Before | After |
| --- | --- | --- |
| Lines | 650/748 (86.9%) | 734/748 (98.1%) |
| Branches | 275/347 (79.3%) | 320/347 (92.2%) |

The report excludes only `FxMain`, `Launcher`, `ui/fx/**`, and `FxPrinter`.
It includes the console UI and the application logic shared with JavaFX.
Coverage measures execution; assertions additionally check results and
unchanged state after rejected operations.

## Behaviors covered

- Parser: supported commands, whitespace, missing/repeated/unsupported fields,
  complete-token matching, task-number boundaries, immutable edit requests,
  partial edits, invalid edits, and preservation of unedited details.
- Dates and times: each documented syntax, slash-date precedence, leap-year
  rules, midnight/noon, invalid clock/calendar values, English display under
  another default locale, and lossless ISO storage including fractional seconds.
- Tasks and lists: all task constructors, description validation, independent
  status copies, idempotent marking, duplicate identity, inclusive event date
  boundaries, invalid task numbers, membership snapshots, deletion, and searches.
- Storage: round trips, Unicode, CRLF, malformed records and physical line numbers,
  corrupt encoding, duplicates, missing/blocked paths, denied access, symlinks,
  external file creation/edit/deletion, empty saves, and recovery after repair.
- Application: successful add/mark/unmark/edit/delete sequences, exact saved data
  after each mutation, restart restoration, search numbering, failed saves,
  external modification, loading errors, reply flushing, invalid commands, and exit.
- Console: indentation, buffering, repeated flushes, blank lines, EOF, recovery
  after errors, and termination before commands following `bye`.

Every persistence test uses JUnit temporary directories. The real console entry
point is also tested in fresh JVMs with a temporary working directory and the
same Java installation as the test runner. These subprocesses are not JaCoCo
instrumented, so four entry-point/constructor lines remain red in the report
although their behavior is checked by JUnit.

POSIX permission and symlink tests are skipped on filesystems that do not support
POSIX attributes. Permission tests also skip if the test account bypasses the
permissions being exercised. Tests that change standard streams or the default
locale restore them in `finally` or lifecycle cleanup; the suite uses JUnit's
default sequential execution.

## Remaining gaps

The remaining missed lines/branches are defensive programmer-error paths,
redundant empty-description checks after validation, impossible assertion-failure
branches under valid use, and filesystem cleanup/ancestor edge cases that cannot
be reproduced reliably on the normal local filesystem. No production code was
changed or excluded just to raise coverage. Disk exhaustion, power loss during
saving, and precisely timed external writes are not simulated by this suite.

JavaFX rendering and interaction require the checks in [gui-test-plan.md](gui-test-plan.md).
The independent fail-fast console acceptance session remains documented in
[ui-test-plan.md](ui-test-plan.md), with input/output saved to
`_temp/ui-test-session.txt`.
