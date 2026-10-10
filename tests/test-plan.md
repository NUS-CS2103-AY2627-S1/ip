# Final regression and smoke-test plan

Build with Java 25: `./gradlew clean check shadowJar` (Windows: `gradlew.bat`).
Copy `build/libs/zsiggy.jar` into a fresh folder and launch with `java -jar zsiggy.jar`.
Never use your only copy of saved tasks for testing.

## Automated coverage

JUnit checks task status, indices, assertions, search, the todo alias, CLI restart,
malformed and missing save files, Unicode persistence, invalid dates and event syntax,
duplicates, whitespace, pipe/newline rejection, more than 100 tasks, failed-save rollback,
and full-list indices in search results. All test data uses temporary directories.

## GUI smoke test

| Action | Expected result |
| --- | --- |
| Start in an empty folder | Zsiggy window opens without a saved-data error. |
| Press Enter on blank input repeatedly | No empty bubbles or extra messages. |
| `t read book` | Adds `[T][ ] read book`. |
| `deadline assignment /by 2026-09-20` | Adds a deadline. |
| `event tutorial /from 2026-09-20 /to 2026-09-21` | Adds an event. |
| `mark 2`, `unmark 2` | Updates the deadline status. |
| `find tutorial` | Shows the event with full-list index 3. |
| Repeat the same event | Red duplicate error; no extra task. |
| `event sleep /to 2004-01-01 /from 2004-01-01` | Red usage error; no exception. |
| `deadline test /by 2026-02-30` | Red date error. |
| `todo read | book` | Red reserved-character error; file stays readable. |
| `mark 0`, `delete 99`, `mark abc` | Red task-number errors; no changes. |
| Resize the window and enter a long description | Text wraps and input remains usable. |
| `bye` | Window closes and process exits. |
| Restart from the same folder, then `list` | Saved tasks and completion states remain. |
| Corrupt a disposable save file, then launch | Warning shown; app opens; writes are blocked. |

Run the updated JAR on Windows x64, Linux x64, Intel Mac, and Apple Silicon Mac
using plain Java 25 (without separately installed JavaFX). Including the correct native
libraries is a packaging check, not a substitute for launching on each operating system.

## Validation on 18 September 2026

- Java 25 clean build, JUnit, and Checkstyle passed.
- Fresh-folder Windows JAR startup produced a Zsiggy window without a startup exception.
- Desktop-control approval timed out, so interactive GUI checks remain pending.
- Existing `docs/Ui.png` is a full-window screenshot with the correct product name;
  it predates the new task spacing and error styling.
- The new JAR requires fresh Mac/Linux smoke tests; reports in forum issue 443 tested v1.0.
