# Testing Kaya

## Running automated checks

From the project root, select Java 25 and run:

```bash
sdk use java 25.0.3.fx-zulu
./gradlew check
```

This runs the JUnit tests and Checkstyle. A JaCoCo coverage report is generated
after the tests. Gradle enables Java assertions during tests, including the
task-list contract checks.

Reports are generated locally under:

- JUnit results: `build/reports/tests/test/index.html`
- Coverage: `build/reports/jacoco/test/html/index.html`
- Coverage XML: `build/reports/jacoco/test/jacocoTestReport.xml`

To run a focused group while working on a failure:

```bash
./gradlew test --tests kaya.parser.ParserTest
./gradlew test --tests kaya.ConsoleTest
```

A focused run produces coverage for that selection only. Run the complete suite
again before comparing overall coverage. To force a fresh complete run:

```bash
./gradlew clean check
```

The coverage configuration uses JaCoCo 0.8.14, which supports Java 25
([release notes](https://www.jacoco.org/jacoco/trunk/doc/changes.html)). Report
generation follows the [Gradle JaCoCo plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html).

## What the tests check

| Area | Important scenarios |
| --- | --- |
| Command recognition | Every command word, surrounding whitespace, case sensitivity, and rejection of partial command words |
| Parsing | Missing values, extra whitespace, repeated or reordered fields, leap days, impossible dates, numeric overflow, Unicode descriptions, and literal slashes |
| Task updates | Correct replacement type, preserved dates and completion status, unchanged original objects, and invalid fields or values |
| Tasks and lists | Completion changes, list ordering, defensive list copies, independent snapshots, assertion contracts, and case-insensitive search |
| Command integration | Saved changes after reloading, duplicate descriptions, deleting only the selected task, exact `list`/`bye` commands, and rollback after failed saves |
| Storage | All task types, Unicode/separators/newlines, malformed records, invalid encoding, read-only files, symbolic links, and recovery after repairing data |
| Console | Multiple commands, empty input, EOF after commands, exact `bye`, startup warnings, numbered output, and the real console entry point |
| Java locale settings | English date display under a Chinese format locale, search under a Turkish default locale, and startup in a separate process with a Chinese locale |

Tests use temporary directories instead of `data/kaya.txt` in the project.
Console and locale tests restore the process-wide settings they change. The
separate console process also uses a temporary working directory and explicit
UTF-8 input/output settings.

The existing CI workflow runs checks on Ubuntu, macOS, and Windows after a push
or pull request. POSIX permission and symbolic-link tests skip environments
without POSIX file attributes; the read-only test also skips accounts that can
override those permissions.

## Coverage scope and recorded results

Line coverage measures which executable lines ran. Branch coverage measures
which outcomes of decisions ran. Tests also assert the expected replies, task
state, and saved contents.

The report excludes only the JavaFX application/launcher and window classes:
`kaya.Main`, `kaya.Launcher`, `kaya.ui.MainWindow`, and `kaya.ui.DialogBox`.
The console UI and shared message formatting remain included.

Local results for the A-MoreTesting increment on Java 25/macOS:

| Measure | Before | After |
| --- | --- | --- |
| JUnit tests | 33 | 63 |
| Non-GUI line coverage | 348/409 (85.1%) | 400/409 (97.8%) |
| Non-GUI branch coverage | 144/178 (80.9%) | 174/178 (97.8%) |

The remaining gaps include rare filesystem cleanup failures and defensive
guards. The two-line console entry point is tested in a separate process, whose
execution is not included in the parent test JVM's coverage report.

## Manual GUI checks

Use a disposable task file for file-failure checks. These are checks to perform
when changing the GUI or preparing a release, not claims that every platform
has already been checked manually.

| Action | Expected result |
| --- | --- |
| Launch Kaya | Greeting and input controls are visible; any startup warning is visible in the chat |
| Submit commands using Enter and Send | Exactly one exchange is added; input clears and regains focus |
| Enter only spaces | Send is disabled and no exchange is added |
| Resize to the minimum window size, then enlarge it | Messages wrap within the window and controls remain usable |
| Enter long descriptions and list several tasks | Text stays readable; the latest reply scrolls into view |
| Enter `bye later`, then `bye` | The first command reports an error; only the second closes the window after its farewell |
| Start with damaged sample data | Valid tasks can be listed; a warning explains why modifications are disabled |
| Make the sample file read-only after startup and try marking a task | A save error appears and task status remains unchanged |

When those environments are available, repeat the GUI checks on Windows and
Linux, at different display scaling settings, and with English and Chinese OS
language settings. Java locale tests do not replace those visual checks.
