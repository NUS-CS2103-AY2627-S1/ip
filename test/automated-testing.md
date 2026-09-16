# Automated testing

Run the suite from the repository root with Java 25:

```shell
./gradlew check
```

On Windows, use `gradlew.bat check`. For a fresh run even if Gradle considers results up to date,
use `./gradlew test --rerun-tasks`. To run only persistence tests, use
`./gradlew test --tests kibo.storage.StorageTest`.

## Reports

- JUnit results: `build/reports/tests/test/index.html`
- JaCoCo line and branch coverage: `build/reports/jacoco/test/html/index.html`
- Machine-readable coverage: `build/reports/jacoco/test/jacocoTestReport.xml`
- Checkstyle: `build/reports/checkstyle/main.html` and `test.html`

The coverage report runs after the tests and includes all production classes, including GUI
classes. Coverage measures executed code; assertions, boundary cases, and failure-path tests
are still needed to establish correctness. No coverage exclusions are used to inflate the report.

## Test coverage

| Area | Behaviors checked |
| --- | --- |
| Commands and parser | Every command keyword, whitespace boundaries, unknown commands, missing arguments, out-of-range/overflowing task numbers, leap dates, event markers and free-form event compatibility |
| Tasks and lists | Completion transitions, repeated mark/unmark, insertion/deletion order, invalid indices, defensive list copies, read-only iterators, search and schedule results |
| Storage | All task types and statuses, UTF-8 text, exact serialization, reload, replacement and empty saves, missing folders/files, invalid records, physical error line numbers, reserved delimiters, read/write failures |
| Kibo | All command handlers, read-only queries, errors and recovery, automatic saving, rollback of every mutation after save failure, exit and end-of-input, startup errors |
| UI text | Numbering, task counts and singular/plural wording, status display, console formatting, dates under a Chinese default locale |
| JavaFX | Error labels and colors, recovery to normal replies, welcome styling, disabled input after exit or startup failure |

## Isolation

Gradle sets a dedicated working directory at `build/test-work`. Storage tests verify this directory
before creating or deleting fixtures, and restore an empty test-data state after each case.
They fail safely if launched directly without the configured Gradle test workspace; in IntelliJ,
choose Gradle for running tests. Tests do not use the repository's real `data/duke.txt`.

Tests that change `System.in`, `System.out`, or the default locale restore those values in
`finally` blocks. JUnit parallel execution is disabled because these values and Kibo's fixed
storage path are process-wide. Do not run multiple Gradle test invocations simultaneously in
the same checkout.

File-system failure tests create a file where a directory is expected, or a directory where
a file is expected. This avoids relying on administrator privileges or OS-specific permission bits.

## Remaining limits and manual checks

- The fallback for a filesystem without atomic moves is not forced automatically. It requires
  a suitable filesystem or a future injectable file-operation abstraction.
- The unused public `Storage` constructor and defensive unreachable command-dispatch paths are
  not exercised just to raise coverage.
- Missing/corrupt packaged FXML resources and native window startup are not fully covered by JUnit.
  Use UI-009 in the UI test plan for the packaged application.
- Locale tests temporarily change Java's default locale; they do not constitute a full test of an
  operating system configured in Chinese.
- The existing GitHub Actions workflow runs checks on Linux, macOS, and Windows. A local passing
  run does not establish that those remote jobs have passed.

Before release, manually check the packaged JAR on the OSes available to you, at the minimum
window size and an enlarged size, with long descriptions, many messages, keyboard input,
and English/Chinese text. Check wrapping, scrolling, readability, and error visibility. Record
the OS, display settings, and results when performed; these checks are not claimed by JUnit.
