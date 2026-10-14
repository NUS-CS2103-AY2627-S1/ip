# Milo User Guide

Milo is the task companion in NUS BroSpot. It helps you manage todos, deadlines, and events through a simple chat interface. Milo saves changes automatically and provides a summary of your progress.

![Milo's graphical interface](Ui.png)

## Quick start

1. Install [Java 25](https://www.oracle.com/java/technologies/downloads/).
1. Download `Milo.jar` from the [latest GitHub release](https://github.com/duynguyen21007/ip/releases/latest).
1. Put `Milo.jar` in an empty folder.
1. Open a terminal in that folder and run:

   ```text
   java -jar Milo.jar
   ```

The **NUS BroSpot - Milo** window should open. Type a command in the message box, then press **Enter** or select the arrow button.

Milo stores tasks in `data/duke.txt`, relative to the folder from which you run the JAR. Run Milo from the same folder each time to keep using the same task list.

## Command reference

Command words are lowercase. Replace words in `UPPER_CASE` with your own values.

| Command | Purpose | Example |
| --- | --- | --- |
| `todo DESCRIPTION` | Adds a task without a date. | `todo read chapter 5` |
| `deadline DESCRIPTION /by DATE` | Adds a task due on a date. | `deadline submit report /by 2026-09-18` |
| `event DESCRIPTION /from START /to END` | Adds an event spanning two dates. | `event project meeting /from 2026-09-18 /to 2026-09-19` |
| `list` | Shows every task and its number. | `list` |
| `find KEYWORD` | Finds tasks whose descriptions contain the keyword. | `find report` |
| `mark NUMBER` | Marks a task as completed. | `mark 2` |
| `unmark NUMBER` | Marks a task as pending. | `unmark 2` |
| `delete NUMBER` | Permanently removes a task. | `delete 2` |
| `stats` | Shows task and completion statistics. | `stats` |
| `help` | Shows the command reference inside Milo. | `help` |
| `bye` | Saves your work and closes Milo. | `bye` |

Dates must use the `yyyy-MM-dd` format, such as `2026-09-18`.

## Adding tasks

### Adding a todo

Use `todo` for a task without a specific date:

```text
todo read chapter 5
```

Milo responds with the new task and the updated task count:

```text
Got it. I've added this task:
  [T][ ] read chapter 5
Now you have 1 tasks in the list.
```

### Adding a deadline

Use `deadline` followed by `/by` and the due date:

```text
deadline submit report /by 2026-09-18
```

The task appears as:

```text
[D][ ] submit report (by: Sep 18 2026)
```

### Adding an event

Use `event` followed by `/from` and `/to`. The end date must be later than the start date:

```text
event project meeting /from 2026-09-18 /to 2026-09-19
```

The task appears as:

```text
[E][ ] project meeting (from: Sep 18 2026 to: Sep 19 2026)
```

Milo rejects missing, malformed, impossible, same-day, and reversed date ranges without changing your task list.

## Understanding task symbols

Each task begins with its type and status:

* `[T]` means todo.
* `[D]` means deadline.
* `[E]` means event.
* `[ ]` means pending.
* `[X]` means completed.

## Viewing and finding tasks

Enter `list` to show every task with its current number:

```text
1.[T][ ] read chapter 5
2.[D][ ] submit report (by: Sep 18 2026)
```

Enter `find KEYWORD` to search task descriptions. Matching is case-insensitive and supports multiple words:

```text
find project meeting
```

Search-result numbers refer only to the displayed results. Run `list` before using `mark`, `unmark`, or `delete` to confirm the task's number in the full list.

## Updating tasks

Use the number shown by `list`:

```text
mark 2
unmark 2
delete 2
```

* `mark` changes the task status to `[X]`.
* `unmark` changes the task status to `[ ]`.
* `delete` permanently removes the task and renumbers the remaining tasks.

If a task number is missing, non-numeric, zero, or outside the list, Milo reports an error and leaves the task list unchanged.

## Viewing statistics

Enter `stats` to summarize the full task list:

```text
Here are your task statistics:
Total: 3
Completed: 1
Pending: 2
Completion: 33.3%
Todos: 1
Deadlines: 1
Events: 1
```

Statistics include both pending and completed tasks. They always cover the full list, even after `find`. An empty list shows `0.0%` completion.

## Getting help

Enter `help` at any time to display every supported command and its expected syntax. This command does not change or save your tasks.

## Saving and recovering tasks

Milo saves automatically after adding, marking, unmarking, or deleting a task. You do not need a separate save command.

If Milo cannot save a change, it displays an error and restores the task list to its previous state. The underlying task file is left unchanged.

Do not edit `data/duke.txt` manually while Milo is running. To start with an empty list, close Milo and move that file to a safe backup location.

## Exiting Milo

Enter `bye` to close the application:

```text
bye
```

Your saved tasks will be available the next time you run Milo from the same folder.

## Troubleshooting

### The application does not open

Run the JAR from a terminal instead of double-clicking it:

```text
java -jar Milo.jar
```

If the terminal reports a Java version error, run `java -version` and confirm that Java 25 is active.

### My saved tasks are missing

Milo saves tasks relative to the terminal's current folder. Return `Milo.jar` to the folder you used previously, or run it while your terminal is in that folder.

### Milo rejects my date

Use four digits for the year and two digits each for the month and day:

```text
2026-09-18
```

For events, ensure the `/to` date is later than the `/from` date.
