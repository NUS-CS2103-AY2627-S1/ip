# Testing Ubis

## Run automated checks

Use Java 25. On macOS with SDKMAN:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew check
```

The Gradle test task also produces a JaCoCo coverage report. Open:

- `build/reports/tests/test/index.html` for JUnit results.
- `build/reports/jacoco/test/html/index.html` for coverage.
- `build/reports/checkstyle/main.html` and `test.html` for coding-style results.

Run a single test class while developing, for example:

```sh
./gradlew test --tests ubis.ParserTest
```

A filtered run measures only those tests. Run `./gradlew clean check` to
regenerate a complete report. In an IDE, delegate test execution to Gradle:
console tests use the classpath and coverage settings supplied by the build.

## Automated coverage

The expanded suite has 165 passing test cases, including parameterized cases.
The full report records 448 of 474 non-GUI lines (94.5%) and 189 of 201
branches (94.0%). These numbers describe the full suite, including the
pre-existing tests, and will change as the application evolves.

| Test area | Behavior checked |
| --- | --- |
| Tasks and task types | Null/blank descriptions, parameter boundaries, missing and repeated parameters, real calendar dates and leap years, completion states, display and storage formats, stored fields, and symbol lookup |
| Task list | Empty and ordered listings, null additions, first/middle/last deletion and renumbering, repeated mark/unmark, invalid indices, and case-sensitive substring search without mutation |
| Parser | All command routes, whitespace, unknown commands, forbidden braces, missing/malformed/overflowing numbers, successful mutations, rejected mutations, save warnings, scanner input and cleanup, and unexpected-error recovery |
| Storage | Mixed task round trips, Unicode, completion/order preservation, missing paths, replacement with shorter/empty contents, malformed records, warning counts, invalid encoding, line endings, and invalid parent paths |
| Chatbot sessions | Independent save paths, reload after commands, startup warnings, and changes retained in memory after save failure |
| Console | Welcome and message formatting, EOF, valid/invalid exit commands, continued processing after rejected commands, and exactly one goodbye |

Storage tests use JUnit temporary directories. Chatbot and parser tests supply
an isolated save path. Tests of default paths and `System.exit()` run in child
Java processes with temporary working directories; they do not modify the
repository's `data/data.txt`. Child processes have timeouts and redirect
output to files to avoid blocked output pipes. Their coverage is combined
with the main JUnit process's coverage, and stale child coverage is removed
before each test run.

Tests use an English (US) locale so date assertions are repeatable. Production
date formatting continues to use the user's default locale.

## Remaining coverage gaps

Only `Main`, `DialogBox`, and `Launcher` are excluded from the report because
they construct or launch JavaFX UI. Other unexecuted lines remain visible:

- Filesystem-specific failures during file creation, writing, replacement,
  or temporary-file cleanup, and the non-atomic move fallback. Portable
  tests cover accessible failure cases without relying on disk exhaustion,
  permission differences, or filesystem races.
- Unreachable default cases and redundant field-count checks in `Task`.
  Earlier validation rejects those inputs before they can reach the checks.
- Assertion-failure paths for internal invariants in `TaskList`, and the
  always-true `index > 0` condition in its one-based listing loop.
- The parser's generic initialization-error fallback: every concrete task
  sets a specific error before returning null.
- Implicit constructors of static utility classes.
- Lines at and after `System.exit()` and calls that never return. JaCoCo can
  leave these lines uncovered because execution ends before a later probe;
  the child-process tests still assert exit status, output, and saved data.

Do not weaken assertions or expose private methods just to reach 100%.
Test new behavior through the public operations and review the report for
newly missed paths.

## Manual GUI review

These checks are pending manual review; JUnit does not verify visual layout
or JavaFX interaction. Use disposable task data for the review.

| Action | Expected result |
| --- | --- |
| Launch the GUI | The welcome greeting is visible and the input and Send button are usable |
| Enter `todo read book` with Enter, then `list` with Send | Each command produces one user bubble and one response; input clears |
| Send a long task description and resize the window | Text wraps, controls remain reachable, and messages are not clipped |
| Add enough messages to require scrolling | The newest response scrolls into view; older messages remain accessible |
| Submit blank input | No empty message bubbles appear |
| Enter `BYE` and `bye now` | An error appears and the window stays open |
| Enter `bye` | The application closes |
| Reopen after adding/marking a task | The saved task and completion state are restored |
| Start with a malformed record alongside a valid task in disposable data | A startup warning is visible and `list` still shows the valid task |

The console equivalent of the exit behavior is covered automatically in
`ConsoleTest`; this checklist verifies that the GUI is wired consistently.
