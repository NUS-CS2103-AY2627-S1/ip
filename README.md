# Adrian User Guide

Adrian is a desktop task manager with a space-mission personality. It helps Rocky track todos,
deadlines, events, and unscheduled tasks with fixed durations.

## Requirements

- Java 25
- Windows, macOS, or Linux with a graphical desktop

## Launching Adrian

### Using only the JAR file

Place `Adrian.jar` in any folder, open a terminal in that folder, and run:

```text
java -jar Adrian.jar
```

Adrian opens in a window titled `Adrian // Mission Control`. Task data is stored automatically in
`data/adrian.txt`, relative to the folder from which the command is run.

### Building the JAR from source

On Windows PowerShell:

```powershell
.\gradlew.bat clean shadowJar
java -jar build\libs\Adrian.jar
```

On macOS or Linux:

```bash
./gradlew clean shadowJar
java -jar build/libs/Adrian.jar
```

The build creates the standalone application at `build/libs/Adrian.jar`.

### Running from IntelliJ IDEA

1. Open the repository folder in IntelliJ IDEA.
2. Configure the project to use JDK 25.
3. Open `src/main/java/adrian/Launcher.java`.
4. Run `Launcher.main()`.

### Running directly with Gradle

On Windows PowerShell:

```powershell
.\gradlew.bat run
```

On macOS or Linux:

```bash
./gradlew run
```

## Command list

Enter commands in the text field and press **Enter** or select **Transmit**.

| Command | Example | Description |
| --- | --- | --- |
| `todo DESCRIPTION` | `todo read chapter 3` | Adds a task without a date or time. |
| `deadline DESCRIPTION /by DATE_TIME` | `deadline submit report /by 2026-10-20 1800` | Adds a task that must be completed by a specific time. |
| `event DESCRIPTION /from DATE_TIME /to DATE_TIME` | `event project meeting /from 2026-10-21 1400 /to 2026-10-21 1600` | Adds an event with a start and end time. |
| `duration DESCRIPTION /for MINUTES` | `duration read sales report /for 120` | Adds an unscheduled task requiring a fixed number of minutes. |
| `list` | `list` | Displays every task and its task number. |
| `find KEYWORD` | `find report` | Displays tasks whose descriptions contain the keyword. |
| `mark TASK_NUMBER` | `mark 2` | Marks a task as completed. |
| `unmark TASK_NUMBER` | `unmark 2` | Marks a completed task as incomplete. |
| `delete TASK_NUMBER` | `delete 2` | Deletes a task. |
| `bye` | `bye` | Closes Adrian. Previous successful changes have already been saved. |

## Date and time format

Deadlines and events use the 24-hour format:

```text
yyyy-MM-dd HHmm
```

For example, `2026-10-20 1830` means October 20, 2026 at 6:30 PM. An event's end time must be later
than its start time.

## Task symbols

| Symbol | Meaning |
| --- | --- |
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[F]` | Fixed-duration task |
| `[ ]` | Incomplete |
| `[X]` | Completed |

## Saved data

Adrian saves tasks after every successful add, mark, unmark, or delete command. The data file is
`data/adrian.txt` relative to the launch folder.

- If the file does not exist, Adrian starts with an empty task list and creates it when needed.
- If the file cannot be read or contains malformed data, Adrian displays a loading error and starts
  safely with an empty task list.
- If a change cannot be saved, Adrian displays an error and restores the task list to its previous
  state.

Avoid editing the data file manually while Adrian is running.

## Command errors

Invalid commands are displayed in a highlighted error card. Adrian explains issues such as:

- missing task descriptions;
- missing `/by`, `/from`, `/to`, or `/for` parameters;
- invalid date and time formats;
- event end times that are not later than their start times;
- non-positive or non-numeric durations; and
- task numbers that do not exist.

Correct the command using the formats in the command list and try again.

## Quick example

```text
todo check fuel reserves
deadline submit science report /by 2026-10-20 1800
event crew briefing /from 2026-10-21 1400 /to 2026-10-21 1500
duration analyze samples /for 90
list
mark 1
find report
bye
```
