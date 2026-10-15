# Koji's Pawn User Guide

Koji's Pawn is a task-management chatbot with a JavaFX chat window. It manages todos,
deadlines, and events, saves task changes between sessions, and lets you search tasks or undo your most recent change.

![Koji's Pawn JavaFX chat showing tasks and undo](Ui.png)

## Getting started

1. Install Java 25.
   - On an Apple Silicon Mac (M1 or newer), install Azul Zulu
     `25.0.3.fx-zulu`, which includes JavaFX.
   - Verify the installation with `java -version`.
2. Download `kojispawn.jar` from the [latest Koji's Pawn release](https://github.com/LINGSIHAN/ip/releases).
3. Create a new folder for Koji's Pawn and put the JAR in it.
4. Open a terminal in that folder (your IDE's terminal works too) and run:

   ```text
   java -jar kojispawn.jar
   ```

In the chat window, type a command and press Enter or select **SEND**. The conversation scrolls to the latest message.
Koji's Pawn saves tasks in `data/kojispawn.txt` inside the folder where you run the JAR.

## Command reference

Enter one command at a time. Command words must be lowercase. Uppercase words such as `DESCRIPTION` and
`TASK_NUMBER` are placeholders to replace with your own values. Extra spaces around commands are allowed.

| Command | What it does | What to enter |
|---|---|---|
| `todo` | Adds a task without a date or time. | `todo DESCRIPTION` |
| `deadline` | Adds a task that must be completed by a date. The date must use `yyyy-MM-dd`. | `deadline DESCRIPTION /by yyyy-MM-dd` |
| `event` | Adds an event with a start and end. See [Event dates and times](#event-dates-and-times) for validation rules. | `event DESCRIPTION /from START /to END` |
| `list` | Displays all tasks and their task numbers. | `list` |
| `mark` | Marks the selected task as completed. | `mark TASK_NUMBER` |
| `unmark` | Marks the selected task as incomplete again. | `unmark TASK_NUMBER` |
| `delete` | Removes the selected task from the list. | `delete TASK_NUMBER` |
| `undo` | Reverses the most recent successful add, mark, unmark, or delete. | `undo` |
| `on` | Displays deadlines occurring on a specified date. Todos and events are not included. | `on yyyy-MM-dd` |
| `find` | Displays tasks whose descriptions contain a keyword. | `find KEYWORD` |
| `bye` | Exits the chatbot. Task changes have already been saved automatically. | `bye` |

## Command examples

### Adding a todo

```text
todo make notes for CSxxxx
```

### Adding a deadline

Deadline dates must use `yyyy-MM-dd`. Although the date is entered as `2026-09-18`, it is displayed as `Sep 18 2026`.

```text
deadline canvas quiz for CSxxxx /by 2026-09-18
```

### Adding an event

```text
event webinar sharing /from Friday 2pm /to 4pm
```

You can also enter calendar dates:

```text
event webinar sharing /from 2026-09-20 2pm /to 2026-09-20 4pm
```

### Event dates and times

The `/from` and `/to` markers are required, must occur in that order, and may each appear only once. Event values
can be free-form text such as `Monday 2pm` or `4pm`. When a value begins with `yyyy-MM-dd`, Koji's Pawn checks that
it is a real calendar date. A time or other text may follow the date after a space. If both values begin with dates,
the `/from` date cannot be later than the `/to` date; events on the same date are allowed. For example, this command
is rejected:

```text
event webinar sharing /from 2026-09-21 /to 2026-09-20
```

Free-form times are not compared with each other. Events are stored as text, so `on` searches deadlines only,
even when an event contains a date.

### Listing tasks

```text
list
```

### Marking, unmarking, deleting, and undoing tasks

Use the number displayed by `list`. Task numbering starts at `1`. If you entered the todo and deadline examples above,
task `2` is the deadline. Check its current number with `list` before changing it because numbers shift after deletion.

```text
mark 2
unmark 2
delete 2
```

`undo` restores the task list to its state before the last successful change and saves that restored state. It can
undo one change at a time; another successful change replaces the previous undo opportunity. Read-only commands
such as `list` and failed commands do not replace it.

```text
todo make notes
undo
```

### Finding deadlines by date

```text
on 2026-09-18
```

If no deadline occurs on that date, Koji's Pawn reports that no dated tasks were found. The query date must be a
real date in `yyyy-MM-dd` format.

### Finding tasks by description

```text
find notes
```

The search is case-sensitive and checks only task descriptions.

### Exiting the chatbot

```text
bye
```

In the chat window, `bye` displays Koji's farewell briefly before closing.

## Input errors

If a command is rejected, Koji's Pawn explains the problem and leaves your tasks unchanged. Check for missing
descriptions or values and task numbers that do not exist. Dates used with `deadline` or `on` must use `yyyy-MM-dd`
and be real calendar dates. A leading `yyyy-MM-dd` in an event must also be real.

Repeated `/by`, `/from`, or `/to` markers are rejected. Event starts and ends must appear in that order, and a dated
event cannot end on a date earlier than it starts. Extra spaces around a command are allowed.

## Task symbols

| Symbol | Meaning |
|---|---|
| `[T]` | Todo |
| `[D]` | Deadline |
| `[E]` | Event |
| `[X]` | Completed |
| `[ ]` | Incomplete |

For example, a completed deadline is displayed as:

```text
[D][X] Canvas quiz (by: Sep 18 2026)
```

## Saving tasks

Tasks are saved automatically after an add, mark, unmark, delete, or undo command. They are loaded again when
Koji's Pawn starts. The data is stored locally at:

```text
data/kojispawn.txt
```

You do not need to edit this file manually. If it does not exist, Koji's Pawn starts with an empty list.
