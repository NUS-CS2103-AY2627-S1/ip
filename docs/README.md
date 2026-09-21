# Echo User Guide

**Echo** is a task-management chatbot that helps you keep track of todos, deadlines, and events.
Type a command in the command box, then press **Enter** or select **Send**.

## Before you start

- Commands and task numbers are shown in `monospace`, such as `list` and `mark 2`.
- Use the task numbers shown by `list` or `find` when marking, unmarking, or deleting a task.
- Enter dates and times as `yyyy-MM-dd HHmm`, for example `2026-09-20 1800`.
- Echo saves changes automatically in `data/echo.txt`; no save command is needed.
- `[T]`, `[D]`, and `[E]` mean todo, deadline, and event. `[X]` means completed, while `[ ]` means active.

## Features

### Adding a todo: `todo`

Adds a task without a date or time.

Format: `todo DESCRIPTION`

Example: `todo read chapter 3`

### Adding a deadline: `deadline`

Adds a task that must be completed by a particular date and time.

Format: `deadline DESCRIPTION /by DATE_TIME`

Example: `deadline submit report /by 2026-09-20 1800`

### Adding an event: `event`

Adds a task with a start and end time. The start time must be earlier than the end time.

Format: `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME`

Example: `event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600`

### Listing all tasks: `list`

Shows every task and its task number.

Format: `list`

Example: `list`

### Finding tasks: `find`

Finds tasks whose descriptions contain the given text. The search is case-sensitive, so `book` does not
match `Book`. Results keep their original task numbers from the full list.

Format: `find SEARCH_TEXT`

Example: `find book`

### Marking a task as completed: `mark`

Marks the selected task as completed.

Format: `mark TASK_NUMBER`

Example: `mark 2`

### Marking a task as active: `unmark`

Marks the selected task as not completed.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Deleting a task: `delete`

Permanently removes the selected task. Use `list` first if you are unsure of its number.

Format: `delete TASK_NUMBER`

Example: `delete 3`

### Viewing progress: `stats`

Shows the number of active and completed tasks, including completion totals by task type. It also shows
tasks completed during the current week, which begins on Monday.

Format: `stats`

Example: `stats`

### Exiting Echo: `bye`

Closes Echo.

Format: `bye`

Example: `bye`

## Command summary

| Action | Command format | Example |
| --- | --- | --- |
| Add a todo | `todo DESCRIPTION` | `todo read chapter 3` |
| Add a deadline | `deadline DESCRIPTION /by DATE_TIME` | `deadline submit report /by 2026-09-20 1800` |
| Add an event | `event DESCRIPTION /from START_DATE_TIME /to END_DATE_TIME` | `event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600` |
| List tasks | `list` | `list` |
| Find tasks | `find SEARCH_TEXT` | `find book` |
| Complete a task | `mark TASK_NUMBER` | `mark 2` |
| Reopen a task | `unmark TASK_NUMBER` | `unmark 2` |
| Delete a task | `delete TASK_NUMBER` | `delete 3` |
| View progress | `stats` | `stats` |
| Exit Echo | `bye` | `bye` |
