# Eli User Guide

Eli is a friendly bilingual task companion that responds in English and
Chinese. It helps you manage todos, deadlines, and events through short text
commands.

![Eli's graphical interface](Ui.png)

## Getting started

Type a command in the box at the bottom of the window, then press **Enter** or
select **Send**. Commands are written in lowercase in this guide. Replace words
in `UPPER_CASE` with your own information; do not type the underscores or
brackets shown in command formats.

Eli uses these task symbols:

- `[T]` — todo
- `[D]` — deadline
- `[E]` — event
- `[ ]` — incomplete task
- `[X]` — completed task

Task numbers can change after deleting or sorting tasks, so use `list` before a
numbered command when you are unsure.

## Features

### Adding a todo

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example:

```text
todo Read CS2103T notes
```

Eli adds the todo and shows the updated number of tasks.

### Adding a deadline

Adds a task that must be completed by a particular date or time.

Format: `deadline DESCRIPTION /by DATE`

Examples:

```text
deadline Submit iP /by 2026-09-18
deadline Submit iP /by 2026-09-18 2359
deadline Call home /by next Friday
```

Use `yyyy-MM-dd` for a date or `yyyy-MM-dd HHmm` for a date and 24-hour time.
Eli formats supported dates for display. Other date descriptions, such as
`next Friday`, are kept as entered.

### Adding an event

Adds a task with a start and end time.

Format: `event DESCRIPTION /from START /to END`

Example:

```text
event Team meeting /from 2026-09-17 1400 /to 2026-09-17 1600
```

For chronological validation and sorting, use `yyyy-MM-dd HHmm`. The end time
must be later than the start time.

### Listing tasks

Shows every task with its current number and completion status.

Format and example: `list`

If there are no tasks, Eli tells you that the task list is clear.

### Marking a task as complete

Marks the task with the given number as completed.

Format: `mark TASK_NUMBER`

Example: `mark 2`

The task's status changes from `[ ]` to `[X]`.

### Marking a task as incomplete

Returns a completed task to the incomplete state.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

The task's status changes from `[X]` to `[ ]`.

### Deleting a task

Permanently removes the task with the given number.

Format: `delete TASK_NUMBER`

Example: `delete 3`

Eli shows the removed task and the new task count.

### Finding tasks

Finds tasks whose descriptions contain a keyword. The search is not
case-sensitive.

Format: `find KEYWORD`

Example: `find notes`

Eli shows matching tasks with their original task numbers, or reports that no
matches were found.

### Sorting tasks

Groups and reorders the task list.

Format and example: `sort`

Deadlines are ordered by their `/by` dates, events by their `/from` dates, and
todos alphabetically. Values Eli cannot interpret as dates appear after dated
tasks and are ordered alphabetically. The sorted order is saved.

### Exiting Eli

Shows a farewell and closes the application.

Format and example: `bye`

The Chinese command `再见` works too.

## Input errors and saved data

Eli explains malformed commands instead of closing unexpectedly. It rejects
missing descriptions or parameters, impossible calendar dates, invalid task
numbers, repeated parameters, duplicate tasks, and events that do not end after
they start.

Tasks are saved automatically after each successful change and restored the
next time Eli starts. If the task data cannot be read or written, Eli displays
an error and avoids accepting an unsaved change.
