# Feature finalization review

Reviewed on 16 September 2026 using Java 25.0.3.fx-zulu.

## Findings and corrections

- Search previously numbered its filtered results from 1. Indexed commands
  still addressed the full list, so a displayed search number could select
  another task. Search now retains full-list numbers. JUnit and console
  regression cases verify that marking a search result updates that task.
- Unreadable saved data previously became an empty list without a warning.
  A subsequent change could replace the original file. Loading now reports
  unreadable or malformed data in both interfaces and disables saving for
  that session. Valid adjacent records remain usable. Tests verify damaged
  bytes and malformed records survive attempted saves, and a successful
  reload after repair allows saving again.

## Checks completed

| Area | Evidence |
| --- | --- |
| Empty, unknown, and incomplete commands | Parser, command-boundary, and exact-output console tests |
| Case, whitespace, and command aliases | JUnit and console sessions |
| Invalid, overflowing, and out-of-range task numbers | Parser and command-boundary tests |
| Missing/repeated date parameters and invalid event ranges | Parser tests and console sessions |
| Duplicate tasks and records | Command-boundary and storage tests |
| Completion timestamps and seven-day boundaries | Task and statistics tests |
| Missing file and parent directory creation | Storage tests and fresh console sessions |
| Damaged records, invalid UTF-8, and directory at file path | Storage tests; damaged-record console session |
| Write failure and later recovery | Tests verify in-memory changes persist and the next save restores the full list |
| Product name | Source and resource review confirms Duchess in the title, header, greetings, help, and speaker labels |
| Build and packaging | `./gradlew check shadowJar` passes |
| Console behavior | All 22 exact-output sessions pass |
| JUnit | All 106 tests pass |
| Core coverage | 98.40% lines, 93.87% branches, 100% methods; excludes JavaFX controllers |
| Style and patch whitespace | Checkstyle and `git diff --check` pass |

## Verification limits

The packaged GUI launches on this Mac outside the execution sandbox. The
Java process is not exposed in the UI automation inventory, so rendered
layout, keyboard focus, scrolling, shortcuts, and exit presentation have
not been verified interactively. Follow `test/manual-test-plan.md` for those
checks and Windows/Linux and display-scaling checks. JavaFX emits a Java 25
native-access warning during launch; it does not prevent this smoke launch.

The README credits the existing dependencies and this Codex-assisted work.
Repository inspection cannot establish whether those libraries have course
approval, whether earlier work used other external sources or AI tools, or
whether every historical reuse has been credited. The author must confirm
those records before submission. No new dependency was introduced.

The persistence design assumes one running Duchess instance owns a data file.
Concurrent instances or external edits during a session can still replace
each other's changes; file locking and conflict detection are not implemented.
