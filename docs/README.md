# Panda User Guide

Panda is a desktop task manager controlled with short text commands. It keeps track of to-dos,
deadlines, and events, and saves changes automatically.

## Quick start

1. Install Java 25.
2. Place `panda.jar` in a folder of your choice.
3. Open a terminal in that folder and run `java -jar panda.jar`.
4. Type a command in Panda's input box, then press Enter or the Send button.

## Command format

- Words in `UPPER_CASE` are values you provide. For example, replace `DESCRIPTION` with a task name.
- Items in `[square brackets]` are optional.
- Dates use `yyyy-MM-dd`; date and time values use `yyyy-MM-dd HH:mm`.
- `INDEX` is the task number displayed by `list` or `find` and must be a positive integer.

## Features

### Add a to-do: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo read chapter 3`

### Add a deadline: `deadline`

Adds a task that must be completed by a specific date and time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Example: `deadline submit report /by 2026-09-20 17:00`

### Add an event: `event`

Adds a task with a start and end date and time.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from 2026-09-20 14:00 /to 2026-09-20 16:00`

### List tasks: `list`

Shows every task, or only deadlines and events occurring on a specified date. A filtered list keeps
the tasks' original indexes.

Format: `list [DATE]`

Examples: `list` or `list 2026-09-20`

### Find tasks: `find`

Finds tasks whose descriptions contain the given text. Matching is case-insensitive and results keep
their original task indexes.

Format: `find KEYWORD`

Example: `find report`

### Mark and unmark tasks

Changes whether a task is completed.

Formats: `mark INDEX` or `unmark INDEX`

Examples: `mark 2` or `unmark 2`

### Update a task: `update`

Changes a task's description, timing, or both. Updates preserve its type, completion status, index,
and position in the task list.

Formats:

```text
update INDEX /name NEW_DESCRIPTION
update INDEX /by DATE_TIME
update INDEX /from START /to END
update INDEX /by DATE_TIME /name NEW_DESCRIPTION
update INDEX /from START /to END /name NEW_DESCRIPTION
```

Examples:

```text
update 2 /name submit final report
update 2 /by 2026-09-21 17:00 /name submit final report
update 3 /from 2026-09-22 14:00 /to 2026-09-22 16:00
```

`/by` applies only to deadlines. `/from` and `/to` apply only to events and must be used together.
For a combined update, `/name` must be the final field.

### Delete a task: `delete`

Removes the task at the specified index.

Format: `delete INDEX`

Example: `delete 2`

### Exit Panda: `bye`

Displays Panda's farewell and closes the application.

Format: `bye`

## Saving and errors

Panda saves changes automatically to `data/info.txt`, relative to the folder from which it
was launched. On the first run, Panda starts with an empty list and creates the file and missing
folders when the task list first changes.

If a stored line is malformed, Panda reports and skips that line while loading the remaining valid
tasks. Invalid commands, indexes, and dates produce a readable error instead of closing the app. In
the GUI, the rejected command stays in the input box so it can be corrected. See the
[error catalogue](errors.md) for detailed error messages.

## Command summary

| Action | Format |
| --- | --- |
| Add a to-do | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` |
| Add an event | `event DESCRIPTION /from START /to END` |
| List tasks | `list [DATE]` |
| Find tasks | `find KEYWORD` |
| Mark completed | `mark INDEX` |
| Mark unfinished | `unmark INDEX` |
| Update a task | `update INDEX /name NEW_DESCRIPTION` or a timing format above |
| Delete a task | `delete INDEX` |
| Exit Panda | `bye` |
