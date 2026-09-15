# Sumo User Guide

Sumo keeps tasks in the order in which they were added. Use `list` to display that order.

## Running the JAR

Use Java 25. Put `Sumo.jar` in a folder of your choice, open a terminal in that folder, and run:

```text
java -version
java -jar Sumo.jar
```

To build the JAR on Windows from this repository:

```powershell
$env:GRADLE_USER_HOME = "$env:USERPROFILE\.gradle"
.\gradlew.bat shadowJar
```

The output is `build/libs/Sumo.jar`. On macOS/Linux, use `./gradlew shadowJar`.
The current JAR bundles JavaFX natives for Windows, macOS, and Linux x64;
ARM-native compatibility has not been verified. Use a matching x64 Java runtime for this artifact.

Tasks are saved after each change in `data/sumo.txt`, relative to the terminal's working folder.
Restart from the same folder to load the same tasks. The folder and file are created as needed.

## Commands

Type a command and press **Enter** or click **Send**. Command words are lowercase and case-sensitive.
Leading, trailing, and repeated spaces are accepted.

| Command | Purpose |
| --- | --- |
| `todo <description>` | Add a todo. |
| `deadline <description> /by <date> [HHmm]` | Add a deadline. |
| `event <description> /from <date> [HHmm] /to <date> [HHmm]` | Add an event. |
| `list` | Show tasks in their original order. |
| `find <keyword>` | Find a substring in descriptions, ignoring capitalization. |
| `on <date>` | Show deadlines and events on a date, including both ends of an event's date range. |
| `sort` | Show a temporary chronological view. |
| `mark <number>` | Mark a task complete. |
| `unmark <number>` | Mark a task incomplete. |
| `delete <number>` | Delete a task. |
| `bye` | Show the farewell and close the GUI after five seconds. |

Use numbers from `list` for `mark`, `unmark`, and `delete`. Filtered and sorted views have their
own display numbering; run `list` again before modifying a task.
`list`, `sort`, and `bye` take no arguments. The console interface exits immediately after `bye`.
In the GUI, input is disabled during the five-second farewell; scrolling remains available.

An empty list shows `No tasks in your list yet.`; an unsuccessful search shows
`No matching tasks found.`. An empty date query shows `No tasks on <formatted date>.`.

## Adding tasks

Add a todo with:

```text
todo read a book
```

Add a deadline with a date or date and time:

```text
deadline submit report /by 2026-02-03
deadline attend review /by 2026-02-03 0915
```

Add an event with a start and end:

```text
event project meeting /from 2026-02-03 1400 /to 2026-02-03 1600
```

Dates accept `yyyy-MM-dd` or `d/M/yyyy`; times use four-digit 24-hour `HHmm`.
Dates must exist in the calendar. Past dates are allowed for recording overdue tasks.
An event must end after it starts. An omitted time means midnight, so use times for same-day events.
The same task cannot be added twice, even with different capitalization or spacing.
Task text cannot contain `|`, line breaks, or control characters because of the save-file format.

## Sorting tasks temporarily

Use `sort` to display the full task list in ascending chronological order. Incomplete tasks are
shown before completed tasks. Within each group, deadlines are ordered by `by`, events by `to`,
and events with the same `to` are ordered by `from`. Undated todos appear last, and equal values
retain their existing order.

The sorted view is temporary: it does not rewrite `data/sumo.txt` or change the order used by
`list`, `find`, `on`, `mark`, or `delete`.

```text
sort
```

The display is grouped into `Incomplete tasks` and `Completed tasks` and is labelled as a sorted
view. If the list is empty, Sumo replies:

```text
No tasks to be sorted.
```

`sort` takes no arguments. For example, `sort descending` returns
`Use: sort. This command takes no arguments.` as a command error.
