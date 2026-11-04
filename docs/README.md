# Soar User Guide

**Soar is a task-tracking chatbot** that helps you keep track of todos, deadlines, and events. Type a command in the chat box and press Enter. Your tasks are saved automatically.

## Getting started

Start Soar, then try these commands:

```text
todo read chapter 3
deadline submit report /by 20 Sep 2026 6:00 PM
event project meeting /from 2026-09-20 14:00 /to 2026-09-20 16:00
list
```

In command formats below, `UPPER_CASE` text is a value you supply. Task numbers are the positive numbers shown by `list`.

## Features

### Add a todo: `todo`

Adds a task with no date or time.

Format: `todo DESCRIPTION`

Example: `todo buy groceries`

### Add a deadline: `deadline`

Adds a task due on a date, optionally at a time.

Format: `deadline DESCRIPTION /by DATE_OR_TIME`

Example: `deadline submit report /by 20 Sep 2026 6:00 PM`

Supported deadline and date-query formats are:

```text
yyyy-MM-dd                 e.g. 2026-09-20
d/M/yyyy                   e.g. 20/9/2026
d/M/yyyy HHmm              e.g. 20/9/2026 1800
yyyy-MM-dd HH:mm           e.g. 2026-09-20 18:00
d MMM yyyy h:mm a          e.g. 20 Sep 2026 6:00 PM
ISO local date-time        e.g. 2026-09-20T18:00
```

### Add an event: `event`

Adds an event with a start and end time.

Format: `event DESCRIPTION /from START /to END`

Example: `event project meeting /from 2026-09-20 14:00 /to 2026-09-20 16:00`

Event times can be any non-empty text. When both values are recognised date or date-times, the end must not be before the start, and the event can be found with `date`.

### View all tasks: `list`

Shows every task in its current order, with its task number and completion status.

Format: `list`

### Find tasks by description: `find`

Shows tasks whose descriptions contain a word or phrase. Matching is case-insensitive.

Format: `find KEYWORD_OR_PHRASE`

Example: `find report`

### View tasks on a date: `date`

Shows deadlines and events that occur on a specified date. Use any date or date-time format listed under `deadline`.

Format: `date DATE_OR_TIME`

Example: `date 20 Sep 2026`

### Mark or unmark a task: `mark`, `unmark`

Marks a task as complete, or returns it to incomplete.

Formats: `mark TASK_NUMBER` and `unmark TASK_NUMBER`

Examples: `mark 2`, `unmark 2`

### Edit a task: `edit`

Changes one or more details without removing the task. Its type, completion status, list position, and fields not included in the command are kept. If any new value is invalid, nothing is changed.

Format: `edit TASK_NUMBER FIELD VALUE [FIELD VALUE ...]`

| Task type | Fields you can edit |
| --- | --- |
| Todo | `/description` |
| Deadline | `/description`, `/by` |
| Event | `/description`, `/from`, `/to` |

Examples:

```text
edit 1 /description read two chapters
edit 2 /description submit final report /by 20 Sep 2026 6:00 PM
edit 3 /to 5pm /from 3pm
```

Each field can appear once and fields may be in any order. Values cannot contain a standalone slash-word such as `/to` or `/by`, because Soar treats it as another field marker.

### Delete a task: `delete`

Permanently removes the specified task.

Format: `delete TASK_NUMBER`

Example: `delete 3`

### Exit Soar: `bye`

Closes the current Soar session.

Format: `bye`

## Saving your tasks

Soar saves changes after adding, editing, marking, unmarking, or deleting a task. Your data is stored in `data/soar.txt` relative to where Soar runs. Avoid editing this file manually; invalid records can prevent Soar from starting.

## Command summary

| Action | Command |
| --- | --- |
| Add a todo | `todo DESCRIPTION` |
| Add a deadline | `deadline DESCRIPTION /by DATE_OR_TIME` |
| Add an event | `event DESCRIPTION /from START /to END` |
| List tasks | `list` |
| Find tasks | `find KEYWORD_OR_PHRASE` |
| View a date | `date DATE_OR_TIME` |
| Complete / reopen a task | `mark TASK_NUMBER` / `unmark TASK_NUMBER` |
| Edit a task | `edit TASK_NUMBER FIELD VALUE [FIELD VALUE ...]` |
| Delete a task | `delete TASK_NUMBER` |
| Exit | `bye` |
