# Aster User Guide

Aster is a star-themed task manager with a JavaFX desktop interface. Type a command
in the input field and press **Enter**. Aster saves changes automatically, so your
tasks are available the next time you start the application.

## Features

### Add tasks

| Command | Example | What it does |
| --- | --- | --- |
| `todo` | `todo read a book` | Adds a task without a date. |
| `deadline` | `deadline submit report /by 2026-09-30` | Adds a task with a due date or time. |
| `event` | `event team meeting /from 2026-09-20 10:00 /to 2026-09-20 11:00` | Adds an event with a start and end date/time. |

For a deadline, include exactly one `/by` marker. For an event, include one `/from`
and one `/to` marker. Dates can be entered as `yyyy-mm-dd`; date/time values can use
`yyyy-mm-dd HH:mm` (for example, `2026-09-20 10:00`).

### View and search tasks

| Command | Example | What it does |
| --- | --- | --- |
| `list` | `list` | Displays all tasks and their task numbers. |
| `ondate` | `ondate 2026-09-20` | Displays deadlines and events occurring on a date. |
| `find` | `find report` | Finds tasks whose descriptions contain the keyword. |

Use the task numbers shown by `list` for the commands below.

### Update and organise tasks

| Command | Example | What it does |
| --- | --- | --- |
| `mark` | `mark 1` | Marks task 1 as completed. |
| `unmark` | `unmark 1` | Marks task 1 as incomplete again. |
| `tag` | `tag 1 #urgent` | Adds a tag to task 1. Tags start with `#` and may contain letters, digits, `-`, or `_`. |
| `delete` | `delete 1` | Permanently removes task 1 from the list. |

### Exit

Enter `bye` to close Aster. Tasks are saved automatically before the application
closes.

## Notes

- Commands and arguments must be entered on one line.
- Aster rejects invalid or incomplete commands with a helpful error message and
  continues running.
- Adding an identical task twice is not allowed.

## Storage

Tasks are stored in `data/aster.txt`. The file is created and updated automatically;
do not edit it while Aster is running.
