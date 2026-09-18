# Computa

Computa is a command-line task manager written in Java. It stores tasks in
memory for the duration of a session and supports todos, deadlines, events,
completion status, deletion, and friendly handling of invalid input.

## Features

- Todos without date or time information.
- Deadlines with free-form or ISO due dates/times.
- Events with free-form or ISO start and end dates/times.
- Human-readable formatting and date queries for parsed ISO dates.
- List tasks with their type and completion status.
- Sort dated tasks chronologically.
- Mark and unmark tasks.
- Delete tasks by their list number.
- Handle malformed or unknown commands without crashing.

Tasks are written to `data/computa.txt` whenever the task list changes and
loaded when Computa starts. Missing folders/files are created automatically.
Malformed records are skipped so corrupted data does not prevent startup.

## Commands

| Command | Example | Purpose |
| --- | --- | --- |
| `todo <description>` | `todo borrow book` | Add a todo. |
| `deadline <description> /by <date/time>` | `deadline submit report /by Friday` | Add a deadline. |
| `event <description> /from <start> /to <end>` | `event project meeting /from Mon 2pm /to 4pm` | Add an event. |
| `list` | `list` | Display all tasks. |
| `sort` | `sort` | Display dated tasks in chronological order. |
| `on <yyyy-mm-dd>` | `on 2019-10-15` | Display deadlines and events occurring on a date. |
| `mark <number>` | `mark 1` | Mark a task as completed. |
| `unmark <number>` | `unmark 1` | Mark a task as incomplete. |
| `delete <number>` | `delete 2` | Remove a task from the list. |
| `bye` | `bye` | Exit Computa. |

ISO dates (`yyyy-mm-dd`) and date-times (`yyyy-mm-dd HHmm` or ISO date-time
format) are stored as `java.time.LocalDateTime` values. The earlier
`d/M/yyyy HHmm` notation is also accepted. Parsed values are displayed as, for
example, `Oct 15 2019` or `Oct 15 2019 1800`. Free-form values such as `Sunday`
or `Mon 2pm` remain supported as text. The `on` command finds matching
deadlines and events; events match every date from their start through end date.

## Running in IntelliJ IDEA

Use JDK 25 and open this repository as a Gradle project. To launch the chat
window, run `computa.Launcher`. To use the terminal interface, run
`computa.Computa` instead.

## Running from a terminal

From the repository root, compile and run with Java 25:

```powershell
New-Item -ItemType Directory -Force _temp\classes | Out-Null
javac -encoding UTF-8 -d _temp\classes src\main\java\computa\*.java src\main\java\computa\command\*.java src\main\java\computa\exception\*.java src\main\java\computa\storage\*.java src\main\java\computa\task\*.java src\main\java\computa\ui\*.java src\main\java\computa\util\*.java
java "-Dstdout.encoding=UTF-8" "-Dstderr.encoding=UTF-8" -cp _temp\classes computa.Computa
```

To launch the JavaFX chat window from PowerShell, run:

```powershell
.\run-gui.ps1
```

## UI tests

The documented end-to-end test cases are in
[`test/ui-test-plan.md`](test/ui-test-plan.md). Run them with the project-specific
`test-ui` skill:

```powershell
python .codex\skills\test-ui\scripts\run_ui_tests.py --plan test\ui-test-plan.md
```

The runner starts a fresh session for each case, prints the console transcript,
compares actual output with the expected output, and stops at the first failure.

## Building and running the executable JAR

The Shadow plugin packages Computa and its runtime dependencies into one
executable fat JAR. With JDK 25 configured, run this from the project root:

```powershell
.\gradlew.bat shadowJar
```

The generated file is `build/libs/computa.jar`. Copy that file into an empty
folder, open a command window in the folder, and run:

```powershell
java -jar "computa.jar"
```

The normal build also creates the JAR because `build` depends on `shadowJar`:

```powershell
.\gradlew.bat build
```

The JAR is a generated binary and is intentionally excluded from Git by the
existing `build/` ignore rule. For distribution, attach
`build/libs/computa.jar` to a GitHub release rather than committing it.

If Gradle reports `invalid source release: 25`, install JDK 25 and make sure
Gradle can find it. The build declares a Java 25 toolchain, so an installed JDK
25 is selected automatically. In IntelliJ IDEA, set both the project SDK and
Gradle JVM to JDK 25 under **File → Project Structure** and **Settings → Build,
Execution, Deployment → Build Tools → Gradle**.

The JUnit test task is configured by the Gradle support, and the highest-value
approximately 50% of the codebase is covered by JUnit tests. Update those tests
after each code change and run them with:

```powershell
.\gradlew.bat test
```

## Project structure

- `src/main/java/computa/Computa.java` — command loop and task management.
- `src/main/java/computa/task/` — base and concrete task classes.
- `src/main/java/computa/ui/Ui.java` — console output and user-facing messages.
- `src/main/java/computa/command/` — executable commands and command parsing.
- `src/main/java/computa/storage/Storage.java` — writes and loads the task list.
- `src/main/java/computa/util/DateTimeParser.java` — parses and formats dates.
- `src/main/java/computa/exception/ComputaException.java` — user-input error type.
- `build.gradle` — Gradle build and application configuration.
- `gradlew` / `gradlew.bat` — reproducible Gradle wrapper scripts.
- `test/ui-test-plan.md` — UI test cases and expected transcripts.
