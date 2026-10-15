# Testing Jonk

## Automated tests (A-MoreTesting)

Use Java 25. On macOS with SDKMAN:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew clean check
```

`check` runs the full JUnit suite and enforces at least 95% line coverage and 90% branch coverage for
non-GUI code. JaCoCo 0.8.14 supports Java 25. Every test run also generates these reports:

- JUnit results: `build/reports/tests/test/index.html`
- Coverage with highlighted source lines: `build/reports/jacoco/test/html/index.html`
- Machine-readable coverage: `build/reports/jacoco/test/jacocoTestReport.xml`

To run just the tests and refresh the report, use `./gradlew test jacocoTestReport`.

| Test class | Behavior covered |
| --- | --- |
| `TaskTest` | Every task type, repeated status changes, display and escaped storage formats, leap days, invalid dates |
| `ParserTest` | Commands, whitespace, multiword keywords, integer boundaries, missing/repeated/wrong markers, date errors |
| `TaskListTest` | Number boundaries, insertion/deletion order, rollback, defensive copies, immutable snapshots, search |
| `StorageTest` | All task types/statuses, Unicode and escapes, CRLF, blank/missing/corrupt files, I/O failures, cleanup |
| `JonkTest` | Command responses, validation without mutation, restart persistence, save rollback and successful retries |
| `UiTest` | Exact response text, numbering, task status, singular/plural mission counts |
| `JonkConsoleTest` | Console input/output, startup errors, command loop, exit, real entry point in an isolated process |

Tests use JUnit temporary directories. The entry-point smoke test uses a child process in a temporary working
directory, so it cannot overwrite `data/jonk.txt`. Console tests restore standard streams in a `finally` block.
Save failures are induced using a non-empty directory at the file path, avoiding OS-dependent permission tests.
English date-display expectations use an explicitly configured English test locale. File-format tests cover
Unicode and both LF and CRLF; generated line endings are normalized only when needed for cross-platform assertions.

### Coverage boundaries

The report excludes only the JavaFX presentation and launch classes: `Main`, `MainWindow`, `DialogBox`, and
`Launcher`. Shared response formatting in `Ui` remains included and tested.

Some remaining uncovered lines are defensive or environment-dependent: a filesystem rejecting atomic moves,
failure to delete an already failed save's temporary file, and validation guards that earlier checks make
unreachable. Tests exercise public behavior without reflection or production-only testing hooks.
The child-process entry-point test is not instrumented by the parent JVM's JaCoCo agent; its execution is
therefore not reflected in the coverage percentage. The default constructor is also simple path wiring.

## Console and GUI validation

Follow [the CLI plan](ui-test-plan.md) in order in one fresh interactive process. Compile with
`./gradlew classes` first, then run `java -ea -cp build/classes/java/main jonk.Jonk` with Java 25.
Use an isolated working directory with the specified `data/jonk.txt` fixture, or back up and restore the real
data file. Compare each response and specified file contents before entering the next command; stop at the
first mismatch and retain the full transcript. New A-MoreTesting cases cover integer overflow, extra
arguments, Unicode search, escaped persistence, and repeated marking.

Use [the GUI plan](gui-test-plan.md) for JavaFX interaction and appearance. JUnit does not verify visual
layout. Passing the automated suite does not claim that GUI checks, other operating systems, screen
resolutions, or OS language configurations were manually tested.
