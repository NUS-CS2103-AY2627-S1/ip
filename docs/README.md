# Arrodes User Guide

Arrodes is a personal task manager that remembers your todos, deadlines, and events. You can use it through the graphical user interface (GUI) or the command-line interface (CLI).

![Arrodes user interface](Ui.png)

## Getting started

1. Open the [Arrodes Releases page](https://github.com/qqnoodle/ip/releases).
2. Open the latest release and download `arrodes.jar` from its **Assets** section.
3. Open a terminal in the folder containing the downloaded JAR file.

Run Arrodes in GUI mode with:

```powershell
java -jar arrodes.jar
```

To use the CLI instead, run:

```powershell
java -jar arrodes.jar --cli
```

Enter one command at a time. Arrodes saves changes automatically, so your tasks are available the next time you start the application. Type `help` at any time to display the command summary.

## Command summary

| Command | Syntax | Purpose |
| --- | --- | --- |
| `todo` | `todo <description>` | Adds a todo task. |
| `deadline` | `deadline <description> /by <date or date-time>` | Adds a task with a deadline. |
| `event` | `event <description> /from <date or date-time> /to <date or date-time>` | Adds an event. |
| `list` | `list` | Displays all tasks. |
| `find` | `find <keyword>` | Finds tasks whose descriptions contain the keyword. |
| `upcoming` | `upcoming /on <date or date-time>` | Displays relevant deadlines and events. |
| `mark` | `mark <task number>` | Marks a task as complete. |
| `unmark` | `unmark <task number>` | Marks a task as incomplete. |
| `tag` | `tag <task number> /with <tag>` | Adds or replaces a task tag. |
| `delete` | `delete <task number>` | Deletes a task. |
| `help` | `help` | Displays the command guide. |
| `bye` | `bye` | Exits Arrodes. |

## Features

### Add tasks

Add a todo when no date is involved:

```text
todo buy milk
```

Add a deadline using a date or a date and time:

```text
deadline submit report /by 2026-10-15
deadline attend review /by 2026-10-15T17:00
```

Add an event with a start and end date, or with a start and end date-time:

```text
event team meeting /from 2026-10-15 /to 2026-10-15
event project meeting /from 2026-10-15T14:00 /to 2026-10-15T15:00
```

Use the date format `yyyy-MM-dd` and the date-time format `yyyy-MM-ddTHH:mm`. Event end times must be later than their start times.

### View tasks

Display every task in the order it was added:

```text
list
```

Search task descriptions with a case-insensitive keyword:

```text
find report
```

Show deadlines and events relevant to a date or date-time:

```text
upcoming /on 2026-10-15
upcoming /on 2026-10-15T14:30
```

A date query includes deadlines due on or before that date and events that overlap the date. A date-time query includes deadlines due by that time and events that include that exact time.

### Manage tasks

Tasks are numbered from `1` in the output of `list`. Use the number to mark a task as complete, reopen it, or remove it:

```text
mark 1
unmark 1
delete 1
```

Add or replace a task's tag with a short name containing letters, numbers, hyphens, or underscores:

```text
tag 1 /with urgent
```

Tagged tasks are shown with a `#` prefix, such as `#urgent`.

### Other commands

| Command | Purpose |
| --- | --- |
| `help` | Displays all supported commands and their syntax. |
| `bye` | Saves your tasks and exits Arrodes. |

## Tips

- Keep the command syntax exactly as shown, including spaces around parameters such as `/by` and `/from`.
- Use the task number currently shown by `list`; numbers can change after a task is deleted.
- Arrodes rejects duplicate tasks and invalid dates, and displays an error explaining what needs to be corrected.
