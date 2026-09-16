# JUnit testing

Tests follow the [course's JUnit tutorial](https://se-education.org/guides/tutorials/junit.html):
`src/test/java` is the test source root, packages mirror the production packages, and test classes use
the `ClassNameTest` suffix. Method names use `methodUnderTest_scenario_expectedBehavior` where practical.

## Target

The baseline guidance prioritizes the highest-value 50% of methods. For this project we use the requested
stretch target instead: JUnit tests for all non-trivial public methods of all application classes.
Add or update tests after each code change to maintain this target. This is a behavioral coverage map,
not a claim of measured 100% line or branch coverage; no coverage instrumentation is configured.

## Coverage map

| Production class | Methods / behavior tested | Important cases |
| --- | --- | --- |
| `DialogBox` | `getUserDialog`, `getAnacondaDialog`, image fading | Avatar-free command prompt, empty input, long-command wrapping with CSS, multiline replies, real Anaconda image, fading only the image |
| `MainWindow` | `initialize`, `setAnaconda`, command submission through FXML | Scroll binding, command/reply ordering, clearing the input, successful and invalid commands, goodbye image fade, temporary storage |
| `Parser` | `parse`, `parseTask`, `parseTaskNumber`, `parseKeyword`, `parseDateFilter`, `isExitCommand` | All command types, aliases, whitespace, required fields, invalid markers, both date formats, leap dates, integer boundaries/overflow, search keywords, sharp modifiers, helpful errors |
| `TaskList` | Constructors, `add`, `delete`, `mark`, `clear`, `size`, `asList`, `find`, `filterByDate` | Defensive collection copy, ordering, first/last indices, invalid indices, repeated status changes, empty lists, unmodifiable snapshots, case-insensitive description search, inclusive/exact dates, event end dates, ToDo exclusion |
| `Storage` | `loadTasks`, `saveTasks` | Missing file/folder, empty file, independently specified input/output formats, all task types/statuses, dates, overwrite/truncate, Unicode, independent loads, file I/O failures |
| `Task` | Constructor, getters including `getEndDate`, `markAsDone`, `markAsUndone`, `toString` | Initial state, repeated transitions, description preservation, absent end date, status markers |
| `ToDo` | `toString` | Type marker and both completion states |
| `Deadline` | Date getters, `toString` | Date retention, polymorphic end date, leading zero, both completion states, leap day, English month under a different locale |
| `Event` | Date getters, `toString` | Distinct endpoints, polymorphic end date, year boundary, same-day event, both completion states, English month under a different locale |
| `Ui` | All public methods, including `readCommand` and `close` | Exact messages, banner, list/search headers and numbering, empty lists, counts, clear success message, input trimming, reader closure |
| `Anaconda` | Constructor, `run`, `getResponse`, `main` | Console/GUI command dispatch, saved state after restart, invalid-command recovery, immediate clear and undo, description search, date filters, load/save errors, selected task updates, no success response after failed saves, relative default path in a child JVM |

`Command` contains only enum constants. `AnacondaException` carries a message and reason; application tests
exercise invalid-input, unknown-command, and storage-error reasons through response statuses. The parser's
nested records and `Anaconda.CommandResponse` are covered through their returned values.

`Anaconda.getCommandResponse` tests verify success, recognized but invalid input, unknown and blank commands,
mixed case and slash aliases, immediate clears, and save-failure rollback. `getResponse`
keeps the same text-only API and its existing regression tests. GUI submission tests apply the actual CSS
and check dark green, yellow, and red command rows with matching reply backgrounds, including recovery
from an error to a successful command.
Command rows also show text badges (`OK`, `Check input`, `Error`) so status does not rely on color.
GUI tests check the badges, long-command wrapping without badge overlap, and the retained 99 px avatar.
Input-bar checks cover the command placeholder, aligned controls, readable fonts, Enter and Send submission,
and separation from the conversation when the window is resized, including a short window.

Event commands reject start dates later than end dates before changing tasks or storage. Tests cover both
date formats, equal dates, leap-day and year boundaries, yellow GUI warning status, console recovery,
and preservation of saved tasks and undo history after invalid input. Validation applies to new commands;
loading existing saved events is unchanged. Storage escaping remains outside the implemented feature set.
Impossible calendar dates are rejected for deadlines, both event endpoints, and both date filters.
Regression cases include February 30 in leap and non-leap years, February 29 in non-leap years (including
2100), and valid leap days in 2000 and 2024, in both accepted formats. Invalid inputs produce a yellow
warning with calendar-date guidance and preserve saved tasks and undo history.

Duplicate additions are saved and remain undoable. `TaskList.hasDuplicate` compares type, description
(ignoring case), and all dates, regardless of completion status. Tests cover all three task types,
different descriptions/types/dates, empty and cleared lists, completed and loaded tasks, equivalent
date formats, console warnings, undo/redo, and save-failure rollback without a misleading notice.
GUI tests verify purple command rows, a `Duplicate` badge, matching pale lavender replies, and undo recovery.

Invalid arguments for `todo`, `deadline`, `event`, `mark`, `unmark`, `delete`, `find`, `/by`, and `/from`
include the command format and an example in both GUI and console responses. Date hints explain both
accepted formats and the optional `sharp` modifier; task-number hints refer to `list`. Tests cover missing
fields, invalid dates and event order, invalid/overflowing/nonexistent task numbers, aliases, and extra
filter arguments. Failures preserve storage and undo history. Simple commands, successful additions,
duplicates, unknown commands, and storage errors do not receive these argument hints.

Unknown or blank input includes `Type help to see the available commands.` in the red response.
GUI and console tests check the hint; response tests verify two lines without the full command list.
Storage errors retain their focused error message without unrelated command suggestions.

The `help` command displays the full command list with success status, including itself. Tests cover
case and surrounding whitespace, rejection of extra arguments, console output, unchanged storage,
and preservation of undo history.

Load failures, including malformed task types, field counts, completion flags, and dates, discard the
entire loaded list and report an empty-list restart in the console and GUI. Tests verify the red startup
notice, no partial loading, unchanged files at startup, and successful saving/reloading of new tasks.

Personality wording is checked through exact UI output, parser errors, console/GUI command responses,
and the goodbye avatar fade. Task rows, counts, command guidance, and status colors retain their existing
formatting and behavior. Empty search/date-filter results say `Nothing. No matching tasks.`

The GUI opens with a single green Anaconda greeting before any commands. GUI tests verify its text,
avatar, command/reply ordering, and preservation of the red load-error notice after the greeting.

Task descriptions reject `|` before changing tasks or saving, preventing user input from corrupting the
storage format. Tests cover all task types, pipes with and without spaces, leading/trailing pipes,
quoted pipes, yellow guidance, console recovery, unchanged storage, reloads, and undo history.

## Running the tests

Undo coverage includes parser syntax, the UI success message, reusable task snapshots, all seven mutation
commands, repeated undo, restored task types/order/dates/statuses, empty history, no-op mutations,
non-mutating and invalid commands, malformed clears, session restart, and retry after save failures.
`undo undo` coverage includes reversing all seven mutation types, repeated and alternating undo/redo,
chain interruption by other commands and invalid input, new mutations, immediate clear, empty and
exhausted history, restart, case/whitespace handling, invalid arguments, and rollback/retry after save failure.
Both console and GUI command entry points are exercised. See [undo behavior](../docs/undo.md).
Undo/redo responses also verify the displayed current list, including empty lists, restored task order,
dates and statuses, and continuation of the undo chain after the automatic display.

Use Java 25. From the repository root in PowerShell:

```powershell
.\gradlew.bat test
.\gradlew.bat test --tests anaconda.parser.ParserTest
```

On macOS/Linux use `./gradlew` instead. If Gradle reports the test task as up-to-date and you want to force
another run, use `test --rerun-tasks`. The HTML report is `build/reports/tests/test/index.html`.
The existing Gradle JUnit dependencies are sufficient; no additional framework or mocking dependency is used.
GUI tests initialize JavaFX and construct scenes without opening windows; a graphical desktop environment
is required for the JavaFX toolkit (or a virtual display on Linux).

Storage/application tests use JUnit `@TempDir`, never the project's `data/anaconda.txt`.
Console tests restore `System.in`/`System.out` in try-with-resources and share a resource lock;
locale tests restore the original formatting locale in `finally`.

Gradle explicitly enables Java assertions for tests. The [assertion rationale](../docs/assertions.md)
documents each internal assumption and its regression coverage, including faulty completion overrides,
unsupported storage task types, event-marker boundaries, and task-index boundaries. The child JVM in
`AnacondaTest.main_newWorkingDirectory_usesRelativeDefaultDataPath` retains Java's default disabled
assertions to check normal task creation and persistence in that mode as well.

List-rendering tests also verify that full lists, date-filter results, and description-search subsets
restart numbering at one and preserve the order of the displayed tasks.

Date-filter tests reject non-filter commands and null directions at the parser and task-list APIs,
including empty lists and sharp matching. Valid BY/FROM behavior retains its existing boundary coverage.

Stream-isolation tests cover explicitly supplied UI input/output, caller ownership of the output stream,
and continued console use after successful and invalid GUI commands. Unicode responses retain UTF-8 text.

Stream-refactoring tests cover duplicate search matches, task identity and order, unmodifiable empty
search results, mutable and independent loaded lists, and preservation of the saved file if task
formatting fails. Existing cases cover date boundaries, empty lists, snapshots, and exact storage formats.

The separate console regression plan in `test/ui-test-plan.md` remains useful alongside these JUnit tests.
Its session transcript presents each input immediately followed by its output.
