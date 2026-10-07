# JUnit test selection

Target: focus on approximately the highest-value 50% of implemented methods,
not an exact line-coverage percentage. The pre-GUI selection targets 26 of roughly
48 implemented non-constructor methods. The GUI increment also prioritizes shared
session execution/loading, output routing and GUI submission/rendering instead of
trivial layout factories or accessors. This keeps the focus around the highest-value
half as the code grows; constructors and collaborators are also exercised indirectly.
No coverage percentage is inferred from test counts.

| Area | Primary methods | Why |
| --- | --- | --- |
| Parser | `parse`, `parseTask`, private `parseTodo`, `parseDeadline`, `parseEvent` through `parseTask` | Dispatch, required arguments, date validation |
| Storage | `load`, `save`, private `parseRecord` and `validateFields` via `load` | Data integrity, round-trips, corrupted data, I/O errors |
| Task (6) | `markAsDone`, `markAsNotDone`, `setDone`, `parseDate`, `parseDateTime`, `formatDateTime` via display | Completion state and date semantics |
| TaskList (3) | `add`, `remove`, `getTasks` | Ordering, index boundaries, ownership of the collection |
| Task subtypes (5) | `Todo.toString`, `Deadline.toString`, `Deadline.toStorageRecord`, `Event.toString`, `Event.toStorageRecord` | Polymorphic display and persistence contracts |
| Commands (4) | `AddCommand.execute`, `DeleteCommand.execute`, `MarkCommand.execute`, `OnCommand.execute` | State changes, persistence, errors, inclusive date filtering |
| Search (3) | `Task.matchesDescription`, `TaskList.find`, `FindCommand.execute` | Literal matching, locale independence, stable results, input validation, no writes |
| Shared session | `Mary.execute`, constructor/loading; `Ui.showMessage`, `showLine` via commands | One output destination, restart persistence, validation and storage errors, no writes after bye |
| GUI interaction | `ChatWindow.submit`, `addMessage`, suggestion action through controls | Send/Enter, errors, close lifecycle, startup errors, draft preservation, resizing/scrolling |
| Grouped replies | `Ui.showMessages` | Zero, one, or multiple messages; order, formatting, Unicode and existing array inputs |
| Sorting | `TaskList.sortChronologically`, private `getScheduledTime`, `SortCommand.execute`, parser dispatch | Stable chronology, type-specific dates, renumbering, persistence and failures |

Test files mirror source packages under `src/test/java/mary`, with names such
as `ParserTest.java` and `StorageTest.java`. Test names use
`feature_scenario_expectedBehavior`. Private record parsing is tested through
the public storage API, without reflection. No additional mocking library is
needed. JUnit `@TempDir` isolates every storage test. Console tests restore
`System.out` after each test and rely on JUnit's default sequential execution.
Gradle sets the test JVM's language to English for stable month-name assertions.

Run `gradlew.bat test` with Java 25. See `build/reports/tests/test/index.html`
and `build/test-results/test/TEST-*.xml`. Run the project's `test-ui` workflow
separately: the UI suite verifies the existing end-to-end scenarios and does
not replace unit tests for boundaries and failure paths.

Review and update JUnit tests after every code change to maintain the prioritized
50% target. Leave trivial enums, basic getters, and console decoration to lower
priority. The GUI refactor removes the unused duplicate task parser in `Mary`;
the active parser remains tested in `ParserTest`.
An existing defect should be reported with its failing regression test rather
than changing an assertion to bless incorrect behavior.

## Latest execution

2026-10-04, working-folder confinement: 96 unit tests and 10 GUI tests passed
on Java 25, with zero failures or skips. Checkstyle, JAR verification and the
Windows packaged GUI smoke test passed. Four new subprocess tests prioritize
`LocalPaths.validate` and `Launcher.configureLocalCaches` within the ~50%
highest-value method selection. Tests cover traversal, outside absolute paths,
local nested paths, Windows junction escapes, cache overrides, and blocked caches.
Fixture temporary directories now live under `build/tmp`, so production storage
checks remain active in tests. No containment bypass or configurable alternate root
was added. JVM warnings for intentionally absent temp directories are captured
separately; an initial harness-only failure caused by merged output was corrected.
Unix symlink behavior is covered by the test's Unix branch but was not run on Windows.

### Earlier error-handling execution

2026-10-04, error-handling hardening: 92 unit tests and 10 JavaFX tests passed
on Java 25 with assertions enabled; no skipped tests. Checkstyle and packaged
JAR verification/Windows GUI smoke launch passed. The prioritized ~50% selection
now includes exact command dispatch, repeated markers, event range validation,
duplicate detail comparison, transactional mutations, and safe persistence.

Added coverage for whitespace/tabs, missing arguments, wrong command boundaries,
control characters/pipes, numeric overflow/signs, invalid dates, equal/reversed
event ranges, duplicate details across task types and completion states, missing
folders, malformed UTF-8, invalid paths/parents, write failures, and recovery.
The GUI corrupt-file test now attempts an addition and confirms saving is blocked.
Existing sorting failure expectations intentionally now require the original order,
instead of documenting the former in-memory/disk divergence.

Pre-existing defects fixed: prefix-based dispatch, invalid event ranges, repeated
markers, descriptions that corrupt saved records, overwrite after a failed load,
and mutation despite failed persistence. No regressions were observed.
Actual disk exhaustion, platform ACL denial, and unsupported atomic-move filesystems
were not reproduced; their I/O exceptions are handled by the save/load error paths.
Continue to run one application instance at a time; concurrent writers are not supported.

### Earlier cat-personality execution

2026-10-04, cat-themed MARY: 83 unit tests and 10 JavaFX tests passed on Java 25.
Updated identity, accessible cat-emblem, ASCII banner, and reply assertions;
the prioritized ~50% method selection and all persistence checks are unchanged.
Checkstyle, JAR verification, and Windows packaged GUI launch passed. All 11
console cases passed in 13 sessions; see the cat-themed records in `ui-test-plan.md`.

### Earlier woodland personality execution

2026-10-04, Mosswick personality: Java 25 checks passed 83 unit tests and
10 JavaFX tests, including a new identity/accessibility regression. Updated
reply assertions and the exact ASCII welcome expectation without changing
command, persistence, or validation behavior. The prioritized ~50% method
selection remains unchanged. Checkstyle, archive verification, and the Windows
packaged GUI launch passed. Console cases R1-R2, P1-P4, F1-F2 and S1-S3 passed
in 13 isolated sessions; complete input/output is in `ui-test-plan.md`.
Checkstyle exposed line wrapping and import-spacing issues during development;
the final code follows the repository's configured import grouping.

### Earlier sorting execution

2026-10-04, chronological sorting: Java 25 `check guiTest` passed 83 unit tests
and nine GUI tests, plus Checkstyle and JAR verification. Added coverage for mixed
types, date/time/year boundaries, maximum dates, stable ties/duplicates, completion
state, empty/single/undated lists, repeated sorting, invalid options, save failure,
reload order and subsequent mark/delete numbering. A GUI test verifies submission,
display and saved order. These core methods extend the prioritized ~50% selection.
All 11 UI cases passed in 13 isolated sessions with assertions enabled; full
console records are in `ui-test-plan.md`.

### Earlier stream-search execution

2026-10-03, stream-based search: Java 25 `check guiTest` passed 75 unit tests,
eight GUI tests, Checkstyle and JAR verification. Added a mixed-task-type test
for ordered filtering and an independently mutable result snapshot. Existing
duplicate, empty-result, literal matching and locale tests remain unchanged.
The prioritized method selection is unchanged: `TaskList.find` was already
selected. All eight UI cases passed in nine processes with assertions enabled;
see the stream-search console records in `ui-test-plan.md`.

### Earlier storage validation execution

2026-10-03, storage validation: Java 25 `check guiTest` passed 74 unit tests,
eight GUI tests, Checkstyle and JAR verification. Expanded malformed-record
coverage for missing/extra fields and unknown types, and added a test preserving
the precedence of shape, status and description errors. The selected persistence
methods now include `validateFields`, tested through `load`. An initial switch
indentation violation was fixed before the successful rerun. All eight UI cases
passed in nine isolated sessions with assertions enabled; full console records
are in `ui-test-plan.md`.

### Earlier parser extraction execution

2026-10-03, parser extraction: Java 25 `check guiTest` passed 73 unit tests,
eight GUI tests, Checkstyle and JAR verification. Added a padded-argument/Unicode
regression for dated tasks; existing malformed-input tests still exercise each
private parser through the public API. The highest-value method selection now
includes the extracted parsing helpers. All eight UI cases passed (nine sessions)
with assertions enabled; see the parser-extraction record in `ui-test-plan.md`.

### Earlier assertion execution

2026-10-03, internal assertions: Java 25 `check guiTest jarGuiSmoke` passed
72 unit tests and eight GUI tests, with zero failures, errors, skipped tests, or
Checkstyle violations. Test JVMs explicitly enable Java assertions. Added tests
for physical file-line numbering after blank lines and rejected off-FX-thread
submission. Existing dispatch, corrupt-file, all-task-type round-trip and GUI
tests exercise the other assertions through normal entry points, without
reflection or artificial production hooks. The prioritized method selection is
unchanged. All eight console cases passed both with and without `-ea` (18 process
sessions); full shared input/output records appear in `ui-test-plan.md`.

### Earlier cross-platform packaging execution

2026-10-03, cross-platform packaging: Java 25 Gradle
`check guiTest jarGuiSmoke --rerun-tasks` passed all 71 unit tests and seven
GUI tests, with no failures or skipped tests and zero Checkstyle violations.
No business logic changed, so the prioritized JUnit method selection is unchanged.
Added `PackagedGuiSmoke`, a standalone test helper run by `jarGuiSmoke`, to render
the real GUI against the fat JAR without development runtime dependencies.
`verifyJar` checks platform classes, native libraries and launch manifest as part
of `check`. The helper passed on Windows and Linux/WSLg with Java 25. macOS was
not executed. See `ui-test-plan.md` for limitations and full console records.

### Earlier varargs execution

2026-10-02, varargs: Java 25 Gradle `check guiTest shadowJar` passed 71 unit
tests and seven GUI tests, with no failures, errors, skipped tests, or Checkstyle
violations. Four new tests cover the grouped-message helper, supplementing the
existing high-value method selection. Console cases R1, R2, P1–P4, F1 and F2
passed in nine sessions with unchanged expected output; the full record is in
`ui-test-plan.md`. The fat JAR was rebuilt.

### Earlier JavaFX execution

GUI validation uses `gradlew.bat check guiTest shadowJar` with Java 25.
`test` covers the 62 existing tests and five shared-session tests without starting
JavaFX. `guiTest` runs seven additional tests tagged `gui` on a graphical desktop,
using temporary files and bounded FX-thread waits; it stops on the first failure.
The GUI suite is deliberately separate from `check` so headless CI can still run
the unit suite. Java sources for both test suites are checked by `checkstyleTest`.
2026-10-02 result: 67 unit tests and seven GUI tests passed, with zero skipped
tests and zero Checkstyle violations. The rendered scene was visually reviewed;
the packaged GUI launched and closed normally in a separate smoke test.
See the JavaFX session record in `ui-test-plan.md` for executed results.

### Earlier Checkstyle execution

2026-10-02, Checkstyle setup: Java 25 Gradle `check shadowJar --rerun-tasks`
passed all 62 tests across 14 test classes (zero failures, errors, or skipped tests).
Both Checkstyle tasks passed with zero violations. Reviewed the prioritized
method selection; it is unchanged because production behavior is unchanged.
Test changes retain all existing checks and expected values while replacing
wildcard imports, correcting wrapping/indentation, and encapsulating fixtures.
Parser dispatch uses individual assertions instead of `assertAll`; a failed
assertion now stops that test method rather than collecting its remaining failures.
UI cases R1, R2, P1–P4, F1 and F2 passed in nine sessions; full input/output is
recorded in `ui-test-plan.md`.

### Earlier keyword-search execution

2026-09-27, keyword search: Java 25 Gradle `test shadowJar --rerun-tasks`
passed all 62 tests. Search tests cover parser boundaries, blank keywords,
case-insensitive literal matching, phrases, punctuation, non-ASCII descriptions,
locale independence, metadata exclusion, duplicate matches, result ownership,
numbering, completion state, and unchanged saved data. UI cases R1, R2, P1–P4,
F1 and F2 also passed (nine sessions); see `ui-test-plan.md` for full records.

### Earlier ASCII banner execution

2026-09-27, ASCII cat banner: Java 25 Gradle `test shadowJar --rerun-tasks`
passed all 53 tests. Added `UiTest.showWelcome_catBanner_printsExactAsciiGreeting`
to verify exact artwork, greeting, separators, and ASCII-only output. The existing
high-value method selection remains unchanged; this focused UI regression test
supplements it. See `ui-test-plan.md` for the end-to-end console records.

### Earlier production-fix execution

2026-09-27 after the production fixes: Gradle `test shadowJar` with Java 25.0.4.1
succeeded. **52 tests passed, 0 failed, 0 skipped** across 11 test classes.
The four formerly failing regression tests remain enabled and now pass.
Mark syntax is validated before slicing, todo record field counts are checked,
and both date parsers use strict resolution. Expanded tests also cover malformed
mark prefixes, extra empty record fields, non-leap-century dates, and hour 24.
The UI suite passed six cases/seven sessions, including the new bug regressions;
complete console records are in `ui-test-plan.md`.

## Initial execution (before fixes)

2026-09-27: Gradle 9.6.1 with Temurin Java 25.0.4.1 compiled and executed
51 tests across 11 test classes: **47 passed, 4 failed, 0 skipped**.
The application source was not modified. The four failing assertions expose
pre-existing defects and remain enabled as regression tests:

| Test | Expected | Actual |
| --- | --- | --- |
| `MarkCommandTest.execute_missingNumber_reportsUsageInsteadOfCrashing` | `mark` without a number prints usage | `StringIndexOutOfBoundsException` before the error handler |
| `StorageTest.load_extraTodoFields_rejectsCorruptRecord` | Reject `T \| 0 \| read \| unexpected` with `MaryException` | Extra field silently accepted |
| `TaskTest.parseDate_impossibleCalendarDate_rejectsRatherThanChangingDate` | Reject `29/2/2023` | No exception; default SMART date resolution accepts it |
| `TaskTest.parseDateTime_impossibleCalendarDate_rejectsRatherThanChangingDate` | Reject `31/4/2024 1800` | No exception; default SMART date resolution accepts it |

At that point `gradlew.bat test` exited with failure. These tests must
not be disabled or changed to expect the defects simply to obtain a green build.
The production fixes described in the latest execution section resolve these
failures without disabling the tests or weakening their expectations.
