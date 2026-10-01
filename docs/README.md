# Bo User Guide

Bo is a personal task-management chatbot. You can use its graphical interface
or its command-line interface to add, organize, search, and complete tasks.

## Quick start

1. Install JDK 25 or later.
2. Open the project in IntelliJ and run `bo.gui.Launcher` for the graphical
   interface, or run `bo.Bo` for the command-line interface.
3. Type a command and press Enter. In the GUI, you can also click **Send**.
4. Type `bye` to close the application.

Bo saves your tasks automatically after changes. Saved tasks are stored in
`data/bo.txt`.

## Command format

In the examples below:

- `DESCRIPTION` is the text describing a task.
- `TASK_NUMBER` is the number shown by `list`.
- Dates must use the `yyyy-MM-dd` format, and event date-times must use
  `yyyy-MM-dd HH:mm`, for example `2026-10-10 14:00`.
- Text containing spaces does not need quotation marks.

## Features

### Adding a to-do: `todo`

Adds a task without a date.

Format:

```text
todo DESCRIPTION
```

Example: `todo read chapter 4`

### Adding a deadline: `deadline`

Adds a task that is due on a specified date.

Format:

```text
deadline DESCRIPTION /by DATE
```

Example: `deadline submit report /by 2026-10-10`

### Adding an event: `event`

Adds a task with a start and end date and time.

Format:

```text
event DESCRIPTION /from DATE_TIME /to DATE_TIME
```

Example: `event project meeting /from 2026-10-10 14:00 /to 2026-10-10 16:00`

### Viewing all tasks: `list`

Displays all tasks in their current order. The numbers shown here are used by
commands such as `mark`, `delete`, `snooze`, and `reschedule`.

Format: `list`

### Finding tasks: `find`

Searches task descriptions for a keyword. Matching is case-sensitive and
includes partial matches.

Format: `find KEYWORD`

Example: `find report`

### Marking a task complete: `mark`

Marks the specified task as completed.

Format: `mark TASK_NUMBER`

Example: `mark 2`

### Marking a task incomplete: `unmark`

Marks a completed task as incomplete again.

Format: `unmark TASK_NUMBER`

Example: `unmark 2`

### Deleting a task: `delete`

Permanently removes the specified task from the list.

Format: `delete TASK_NUMBER`

Example: `delete 3`

### Viewing upcoming deadlines: `upcoming`

Displays incomplete deadlines due today or later, ordered by date.

Format: `upcoming`

### Viewing deadlines on a date: `due`

Displays deadlines that fall on the specified date.

Format: `due DATE`

Example: `due 2026-10-10`

### Snoozing a deadline: `snooze`

Postpones a deadline by a positive number of days. Only deadline tasks can be
snoozed.

Format: `snooze TASK_NUMBER DAYSd`

Example: `snooze 2 3d`

Repeated snoozing uses the deadline's latest date. The resulting date cannot
be in the past.

### Rescheduling a deadline: `reschedule`

Replaces a deadline with a new date. Only deadline tasks can be rescheduled.

Format: `reschedule TASK_NUMBER DATE`

Example: `reschedule 2 2026-11-05`

The new date cannot be in the past. Completing a deadline does not prevent it
from being rescheduled, and its completed status is preserved.

### Exiting Bo: `bye`

Closes the application.

Format: `bye`

## Saving data

Bo automatically saves changes after adding, marking, unmarking, deleting,
snoozing, or rescheduling a task. No separate save command is needed.

If Bo cannot read the save file when starting, it reports the problem and
starts with an empty task list. Keep a backup before manually editing
`data/bo.txt`.

## Command summary

| Action | Format |
| --- | --- |
| Add to-do | `todo DESCRIPTION` |
| Add deadline | `deadline DESCRIPTION /by DATE` |
| Add event | `event DESCRIPTION /from START /to END` |
| List tasks | `list` |
| Find tasks | `find KEYWORD` |
| Show upcoming deadlines | `upcoming` |
| Show deadlines on a date | `due DATE` |
| Mark complete | `mark TASK_NUMBER` |
| Mark incomplete | `unmark TASK_NUMBER` |
| Delete task | `delete TASK_NUMBER` |
| Snooze deadline | `snooze TASK_NUMBER DAYSd` |
| Reschedule deadline | `reschedule TASK_NUMBER DATE` |
| Exit | `bye` |
