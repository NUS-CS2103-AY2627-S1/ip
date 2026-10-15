# Testing JassaBot

Use Java 25 for all builds and test runs.

```powershell
.\gradlew.bat test checkstyleTest
```

On macOS/Linux use `./gradlew test checkstyleTest`. To force a fresh execution when Gradle reports
that tests are up to date, add `--rerun-tasks`.

The `test` task also generates a JaCoCo HTML report at
`build/reports/jacoco/test/html/index.html` and XML at
`build/reports/jacoco/test/jacocoTestReport.xml`. JUnit results are at
`build/reports/tests/test/index.html`. JaCoCo 0.8.14 supports Java 25.

## Automated coverage

The expanded suite contains 76 JUnit tests (previously 41). Many tests exercise multiple related
boundary values while reporting the failing input. The tests cover:

- All command types, missing descriptions and markers, invalid dates, task-number bounds and overflow,
  command metadata, repeated completion changes, deletion positions, and recovery after errors.
- Persistence of every task type and completion status, Unicode and escaped text, legacy escapes,
  malformed records, invalid UTF-8, missing directories, empty saves, failed saves, rollback, and retry.
- Task-list order, defensive copying, immutable live views, immutable search snapshots, and invalid indexes.
- Date formats, leap years and centuries, month ends, time bounds, and unsupported syntax.
- Exact console startup, warning, command, exit, and end-of-input output using captured streams.
- Plain UI formatting without starting JavaFX, plus English, Chinese, and Turkish JVM locales.

Tests use isolated files. JUnit temporary directories are placed under `build/tmp/tests` so relative
storage paths work even when Windows puts the system temporary directory on another drive. Console
and locale tests restore process-wide state in `finally` blocks and declare JUnit resource locks.
The parentless storage-path test uses a unique filename and deletes it in a `finally` block.

## Measured results

Measured on Windows with Java 25.0.4:

| Measure | Before | After |
| --- | --- | --- |
| Passing JUnit tests | 41 | 76 |
| Covered lines | 442 / 483 (91.5%) | 476 / 483 (98.6%) |
| Covered branches | 160 / 189 (84.7%) | 180 / 189 (95.2%) |
| Covered methods | 89 / 97 (91.8%) | 96 / 97 (99.0%) |

Both measurements use the same exclusions: `Main`, `Launcher`, `MainWindow`, and `DialogBox`.
These four classes launch or render JavaFX; their manual checks are in [manual-test-plan.md](manual-test-plan.md).
All other production classes remain in the coverage report, including console formatting and exception classes.

Remaining uncovered lines/branches are the console `main` entry point (exercised separately by the
console plan), defensive assertion failures and unreachable branches, the filesystem-dependent
atomic-move fallback, and temporary-file cleanup failure. Tests exercise ordinary I/O failures and
rollback without mocking Java filesystem internals. A passing coverage percentage does not prove
correctness; assertions check output, state, saved bytes, warning order, and recovery behavior.

The [console UI plan](ui-test-plan.md) was reviewed; no command or output behavior changed, so its
expected results remain unchanged. All 20 cases passed with Java assertions enabled in fresh
processes, comparing each response and the specified saved files. The local run's complete
`INPUT>` / `OUTPUT>` transcript is in `../_temp/ui-test-transcript.txt` (ignored generated output).

Manual GUI and additional OS/display checks are documented but have not been performed for this
change. JVM locale coverage does not establish native font, IME, or OS-language compatibility.
