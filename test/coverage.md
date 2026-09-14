# Automated test coverage

Run with Java 25 **with JavaFX**. On this Mac, select it with
`sdk use java 25.0.3.fx-zulu` before running:

```sh
./gradlew test checkstyleMain checkstyleTest jacocoTestCoverageVerification shadowJar
python3 test/run-isolated-ui-tests.py
```

On Linux without a display, prefix the Gradle command with `xvfb-run -a`.
The CI workflow does this automatically; macOS and Windows retain their existing jobs.
GUI tests require a working JavaFX display and fail rather than silently skip when one is unavailable.

The JUnit suite has 133 executed cases, including parameterized cases and four
JavaFX scenarios. Storage, command workflows, and GUI scenarios run in child Java
processes with temporary working directories. Each child receives the complete
test classpath, assertions, its own JaCoCo output file, and a 30-second timeout.
JavaFX operations run on the application thread with bounded waits and toolkit
shutdown. No scenario reads or changes the project's real `data` directory.

The UI wrapper invokes the test-ui skill's recorded-plan runner twice: 12 normal
command cases followed by one malformed-storage startup case. Each suite gets a
fresh temporary repository. It stops on the first failure and records the full
input/output in `_temp/ui-test-session.txt`. The skill runner must be installed
at `.agents/skills/test-ui/scripts/run_ui_tests.py`.

## Results and coverage gates

- JUnit: **133 passed**, none failed or skipped.
- Recorded console plans: **13 passed**.
- Line coverage: **473 / 483 = 97.93%**.
- Branch coverage: **166 / 171 = 97.08%**.
- HTML: `build/reports/jacoco/test/html/index.html`.
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`.
- JUnit details: `build/reports/tests/test/index.html`.

JaCoCo 0.8.14 combines the main test process with every isolated process. Child
coverage is cleared whenever the test task executes, preventing older runs from
inflating the result. Gradle tracks the child coverage directory as a test output.
The `check` task enforces at least 95% line coverage and 90% branch coverage.
No production classes are excluded, including the launcher and defensive paths.
CI uploads coverage and test reports for each platform.

## Remaining gaps

| Code | Uncovered behavior and reason |
| --- | --- |
| `Launcher` | Three lines: its implicit constructor and the thin call to `Application.launch`. The GUI tests exercise `Main.start` and real windows directly so they can control shutdown; the separate launcher lifecycle is not instrumented. |
| `DialogBox` | Two lines wrapping an FXML-loading `IOException`. Real packaged resources load successfully; the tests do not corrupt or replace packaged resources. |
| `Groot.executeCommand` | One defensive default line and three branches checking null/unknown commands or falling through the command switch. The parser rejects these states before dispatch; reaching them requires bypassing the public command API. |
| `Storage.saveTasks` | One line handling a second failure while deleting a temporary file after a save failure. Ordinary write/replacement failures and successful cleanup are tested; deterministic cleanup failure would require filesystem fault injection. |
| `Storage.loadTasks` | One branch where file existence cannot be determined, or changes between the existence checks. Missing files and existing non-regular paths are tested; the remaining branch depends on permissions or filesystem races. |
| `Storage.replaceDataFile` | Two lines falling back when atomic moves are unsupported. The test filesystem supports atomic moves; simulating lack of support would require a filesystem abstraction. |
| `Storage.parseTask` | One default line and one switch branch for an unknown task type after validation. Validation already rejects that type; malformed-type tests cover the reachable error path. |

The gaps remain in the report denominator. Assertions check user-visible results,
persisted bytes, rollback, collection ownership, and applied GUI properties, rather
than invoking private methods merely to raise coverage.

## Bug fixed

Invalid saved deadline dates threw `DateTimeParseException` past the storage error
handler. Storage now catches that exception alongside `IllegalArgumentException`
and reports the existing error with the physical line number. Regression tests
cover malformed dates and invalid calendar dates, console startup, GUI startup,
and preservation of the original saved file. The public API and save format do
not change.

## Windows CI output encoding

The Windows run [34808158429](https://github.com/wtvlol/ip/actions/runs/34808158429)
failed `StorageTest.saveTasks_allTypesAndEscapes_roundTripsAndOverwrites`: the child
process printed `管道` as `??`. The escaped pipes and backslashes were intact, and
all parser tests passed. The data-file round trip succeeded inside the child;
information was lost when its default Windows console encoding produced stdout.

`IsolatedProcess` explicitly sets both stdout and stderr to UTF-8, matching the
parent's `Files.readString` decoding. `IsolatedProcessTest` checks the actual stream
charsets and Unicode/delimiter output in a child JVM. The fix has been verified
locally; a new Windows Actions run is needed after these changes are pushed.
