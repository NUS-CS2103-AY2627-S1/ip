# Automated testing

Use Java 25. On macOS with the project's SDKMAN installation:

```sh
sdk use java 25.0.3.fx-zulu
./gradlew check
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

On Windows PowerShell, use `python` instead of `python3` for the UI test command.
The runner uses UTF-8 explicitly for its test plan and child-process streams so
Unicode commands and responses are checked consistently on every OS.

`check` runs JUnit and Checkstyle. Every Gradle `test` run also generates a
JaCoCo HTML report at `build/reports/jacoco/test/html/index.html` and an XML
report at `build/reports/jacoco/test/jacocoTestReport.xml`. The JUnit report is
at `build/reports/tests/test/index.html`. Use `./gradlew test --rerun-tasks`
when a fresh execution is needed rather than Gradle's up-to-date result.

## Scope

- `TaskTest`, `DeadlineTest`, and `EventTest`: completion, identity, search,
  validation, strict dates, leap days, display, and locale independence.
- `FreeTimeFinderTest`: invalid arguments, exact fits, minute precision,
  overlapping/nested/adjacent events, out-of-range events, and midnight.
- `BkxssTest`, `CommandResultTest`, and `ErrorHandlingTest`: complete command
  responses, parsing, successful state changes, rejection of invalid input,
  read-only commands, persistence, and rollback after failed saves.
- `StorageTest`: round trips, UTF-8, CRLF, legacy records, malformed records,
  duplicate detection, unknown task types, missing directories, save failures,
  and recovery without losing the original data.
- `BkxssMainTest`: real console startup, EOF, farewell, corrupt-data warnings,
  and persistence between separate JVM sessions.
- `BkxssGuiAssetTest`: bundled image dimensions and transparency.

JUnit fixtures use temporary directories and restore changed locales and
standard streams. Console entry-point tests launch the same Java installation
as the test JVM in temporary working directories. If JaCoCo is active, they
inherit its agent with an absolute output path to include child-process
coverage. Keep the tests sequential: some command tests temporarily capture
`System.out`, and locale tests temporarily change the JVM default locale.

The POSIX read-only-file test skips on file systems without POSIX permissions
and for users that can bypass those permissions. Portable failure tests also
use a regular file as a parent directory and a directory as the data file.

## Coverage boundaries

The report excludes only `BkxssGui` and `Launcher`, which require JavaFX
interaction. Their visual checks remain in `test/ui-test-plan.md`.
No production logic was changed for this testing increment.

The remaining uncovered core paths are the unused implicit `Bkxss`
constructor, a best-effort temporary-file cleanup failure, assertion-failure
paths, and defensive branches made unreachable by preceding checks. Avoid
changing production logic or adding reflection-only tests just to raise the
percentage. Coverage measures execution; the assertions and deliberate-bug
check below assess whether tests detect incorrect behavior.

## Checking test effectiveness

For the exact-fit scheduling mutation, temporarily change the final duration
comparison in `FreeTimeFinder.canFit` from `>= 0` to `> 0`. Run:

```sh
./gradlew test --tests bkxss.FreeTimeFinderTest
python3 .codex/skills/test-ui/scripts/run_ui_tests.py
```

Both must fail: an exact-sized gap must be accepted. The first failing UI
case is `Find the earliest free time`; it expects the two-hour gap from
10:00 to 12:00, but the mutation reports no two-hour slot. Restore the
comparison immediately, then rerun the full checks. Never commit the
deliberate bug.

## Portability

The existing CI workflow runs `check` on macOS, Windows, and Linux. Local
execution on one platform does not establish results for the other two.
`test/ui-test-plan.md` records the remaining manual screen, scaling, and
English/Chinese OS-language checks.

To rerun JUnit with a Chinese JVM default locale on macOS/Linux:

```sh
JAVA_TOOL_OPTIONS='-Duser.language=zh -Duser.country=CN' \
  ./gradlew --no-daemon test --rerun-tasks
```

This verifies JVM locale behavior, not GUI rendering or OS input methods.

## A-MoreTesting verification

Local verification on macOS with Java 25.0.3.fx-zulu:

- 84 JUnit tests pass, with no failures or skips, under both the default
  English JVM locale and the Chinese JVM locale.
- All 20 console UI cases and both Checkstyle tasks pass.
- Core coverage: 383/387 lines (98.97%), 229/234 branches (97.86%), and
  66/67 methods (98.51%), with the exclusions described above.
- The exact-fit mutation is caught by seven scheduling JUnit tests and the
  console plan. The production source is restored and the suites pass again.
- The CI workflow runs Gradle checks, fat-JAR packaging, and the console UI
  plan on Windows, macOS, and Linux. The manual GUI environment matrix has not
  been completed locally for this increment.
