# Botavius User Guide

Botavius is a Java task manager with a JavaFX graphical interface. It supports
to-do, deadline, event, and do-after tasks and saves tasks to `save.txt`.

## Installation

Install JDK 25 and Git. Verify Java with `java -version`, then clone the
repository:

```bash
git clone <repository-url>
cd ip
```

The included Gradle Wrapper means that Gradle does not need to be installed
separately.

## Running the application

Run `./gradlew run` from the project directory. On Windows, run
`gradlew.bat run`. The application opens a window containing the task list,
command field, and task controls. Use `EXIT` or enter `bye` to save tasks
and close the app.

Build a JAR with `./gradlew shadowJar` or `gradlew.bat shadowJar` on
Windows. The output is `build/libs/botavius.jar`.

## Commands

Enter commands in the command field and click `EXECUTE`. Task numbers refer
to the numbers shown by `list`.

| Command | Example | Purpose |
| --- | --- | --- |
| `todo` | `todo buy milk` | Adds a to-do task |
| `deadline` | `deadline return book /by 31-12-2026 23:59` | Adds a deadline |
| `event` | `event project meeting /from 31-12-2026 14:00 /to 31-12-2026 16:00` | Adds an event |
| `doafter` | `doafter call client /after 31-12-2026 14:00` | Adds a do-after task |
| `list` | `list` | Displays all tasks |
| `find` | `find book` | Finds matching tasks |
| `mark` | `mark 1` | Marks task 1 as done |
| `unmark` | `unmark 1` | Marks task 1 as not done |
| `delete` | `delete 1` | Deletes task 1 |
| `bye` | `bye` | Saves and exits |

Dates use `dd-MM-yyyy` with an optional 24-hour time `HH:mm`; date-only
values default to midnight. Event `/from` must be earlier than or equal to
`/to`. Otherwise the application reports:

```text
event start time must not be after event end time.
```

The `NEW TASK` dialog provides form-based creation, and the calendar view
filters scheduled tasks by an inclusive date range.

## Saving and testing

Tasks are loaded from and saved to `save.txt` in the project directory.
Run `./gradlew test`, or `gradlew.bat test` on Windows, to execute the
JUnit tests.
