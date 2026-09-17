# Botavius User Guide

Botavius is a Java task manager with a JavaFX graphical interface. It supports
to-do, deadline, event, and do-after tasks and saves tasks to `save.txt`.

## Installation

Install JDK 25 and Git. Verify Java with:

```bash
java -version
```

Clone the repository:

```bash
git clone <repository-url>
cd ip
```

The included Gradle Wrapper means that Gradle does not need to be installed
separately.

## Running the application

From the project directory, run:

```bash
./gradlew run
```

On Windows:

```powershell
gradlew.bat run
```

The application opens a window containing the task list, command field, and
task controls. Use `EXIT` or enter `bye` to save tasks and close the app.

To build a JAR, run `./gradlew shadowJar` (or `gradlew.bat shadowJar` on
Windows). The output is `build/libs/botavius.jar`.

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
| `find` | `find book` | Finds tasks containing the text |
| `mark` | `mark 1` | Marks task 1 as done |
| `unmark` | `unmark 1` | Marks task 1 as not done |
| `delete` | `delete 1` | Deletes task 1 |
| `bye` | `bye` | Saves and exits |

Dates use `dd-MM-yyyy` with an optional 24-hour time `HH:mm`. A date
without a time defaults to midnight. For an event, `/from` must be earlier
than or equal to `/to`; otherwise Botavius reports:

```text
event start time must not be after event end time.
```

The `NEW TASK` dialog offers form-based task creation. The calendar view
filters scheduled tasks by an inclusive date range.

## Saving and testing

Tasks are loaded from and saved to `save.txt` in the project directory.
Run the JUnit tests with:

```bash
./gradlew test
```

On Windows, use `gradlew.bat test`.

## Project structure

- `src/main/java/botavius` — application logic, parser, storage, UI, and tasks
- `src/main/java/Main.java` — JavaFX interface
- `src/main/java/Launcher.java` — application launcher
- `src/test/java` — JUnit tests
- `docs/README.md` — documentation copy of this guide
