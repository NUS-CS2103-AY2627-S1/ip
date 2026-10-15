# Automated testing and manual verification

## Running the suite

Use Java 25 from the repository root:

```powershell
.\gradlew.bat test checkstyleMain checkstyleTest
```

On macOS/Linux, use `./gradlew` instead. Gradle's HTML results are in
`build/reports/tests/test/index.html`. All file-writing tests use JUnit temporary
directories; they do not use your personal `data/clarry.txt`.

## Automated coverage

| Area | Tests and behaviour covered |
| --- | --- |
| Parsing | `ParserTest`, `ParserBoundaryTest`: every command type, case sensitivity, exact commands, whitespace, missing parameters, valid dates, leap days and index boundaries. |
| Task model | `TaskTest`: completion transitions, display and storage formats, inclusive date ranges, impossible dates, task identity and schedule differences. |
| Task collection | `TaskListTest`: defensive collection copies, snapshots, iterator behaviour, deletion order, filtering and locale-independent search. |
| Storage | `StorageTest`: all task types, status/order/Unicode round trips, empty/missing files, malformed records, duplicates, invalid UTF-8, LF/CRLF, invalid parent paths and Chinese-locale date serialization. |
| Command workflows | `ClarryWorkflowTest`: command sequences, persistence after restart, read-only queries, error-status reset, deleting the last task, load-warning delivery and recovery after restart. |
| Failure recovery | `MoreErrorHandlingTest`: repeated markers, invalid ranges/numbers/control characters, duplicate tasks, corrupted files, rollback after save failures and temporary-file cleanup. |
| Console | `ConsoleTest`: all five transcripts from `ui-test-plan.md`, explicit exit ignoring later input, EOF handling and framed storage-error messages. |

`ConsoleTest` discovers the input and expected-output blocks in the console plan.
It compares complete transcripts, normalizing only CRLF/LF line endings, and fixes
the test locale to English for the documented month names. If a case is added to
the plan, update its discovery-count assertion too. Tests that change console
streams or locale restore them in `finally` blocks and declare JUnit resource locks.

Verified on Windows with Java 25.0.4 on 2026-09-15: **61 JUnit tests passed**, none
skipped; Java assertions were enabled. Compilation and Checkstyle also passed.
These checks ran directly with the cached JUnit launcher and dependencies because
the Gradle launcher had encountered local loopback-connection problems earlier.

This is a behavioural coverage inventory, not a measured line/branch percentage.
JavaFX window launching, rendering, native file permission differences, disk-full
conditions, and the filesystem-specific atomic-move fallback need additional
environment or manual testing.

## Manual environment matrix

Follow `gui-test-plan.md` using disposable data for each environment below. These
rows are **pending**, not claims of tests performed on other operating systems.

| Environment | Focus | Status |
| --- | --- | --- |
| Windows, English, 1366x768, 100% scaling | Minimum-size window, long help reply, keyboard focus, scrolling, error cards, mascot. | Pending |
| Windows, Chinese display language, 1920x1080, 150% scaling | Enter `todo 还书`, restart, search for `还`, inspect font fallback and control clipping. | Pending |
| macOS, Java 25, Retina display | Launch packaged JAR, Enter/Send, resize, saved tasks survive restart. | Pending |
| Linux, Java 25, 1920x1080 | Launch packaged JAR, readable fonts, resize and scrolling, UTF-8 persistence. | Pending |
| Disposable restricted save directory | Deny read/write access, verify visible errors and unchanged data, restore access and retry/restart. | Pending |

For every manual run, record OS/version, Java version, display language, resolution,
scaling, commands, observed results and any screenshot paths. Chinese/Turkish
locale unit tests exercise Java logic; they do not replace full OS-language checks.
