# Dulio User Guide

Dulio is a simple task manager for keeping track of todos, deadlines, events,
and recurring tasks. You can add, view, search, complete, and remove tasks
using simple commands.

![alt text](Ui.png)

## Quick start

1. Start the application.
2. Enter a command in the input field.
3. Press **Enter** to execute it.

Use `bye` when you are finished. Dulio saves tasks so that they remain
available the next time you start the application.

## Adding tasks

### Todos

Adds a task without a date or time.

```text
todo read CS2103T notes
```

### Deadlines

Adds a task that must be completed by a date. Dates must use `yyyy-MM-dd`.

```text
deadline submit report /by 2026-10-15
```

### Events

Adds an event with a start and end time. The time values are stored as entered.

```text
event team meeting /from Monday 10am /to Monday 11am
```

### Recurring tasks

Adds a task that repeats at a specified interval.

```text
recurring exercise /every Monday
```

## Managing tasks

### View all tasks

```text
list
```

Displays all tasks and their list numbers. Use these numbers with `mark`,
`unmark`, and `delete`.

### Find tasks

Searches task descriptions for a keyword. The search is case-insensitive.

```text
find report
```

### Mark or unmark a task

```text
mark 1
unmark 1
```

Marks task 1 as completed or incomplete, respectively.

### Delete a task

```text
delete 1
```

Deletes task 1 from the list.

## Exiting Dulio

```text
bye
```

Closes the application after saving your tasks.

## Command summary

| Command | Format | Purpose |
| --- | --- | --- |
| Add todo | `todo DESCRIPTION` | Adds a todo. |
| Add deadline | `deadline DESCRIPTION /by DATE` | Adds a deadline. |
| Add event | `event DESCRIPTION /from START /to END` | Adds an event. |
| Add recurring task | `recurring DESCRIPTION /every INTERVAL` | Adds a recurring task. |
| List | `list` | Shows all tasks. |
| Find | `find KEYWORD` | Shows matching tasks. |
| Mark | `mark NUMBER` | Marks a task as done. |
| Unmark | `unmark NUMBER` | Marks a task as not done. |
| Delete | `delete NUMBER` | Deletes a task. |
| Exit | `bye` | Closes Dulio. |

## Notes

- Commands must use single spaces and must not have leading or trailing spaces.
- Task numbers are one-based: the first task is task `1`.
- A task description cannot be empty.
- Invalid commands display an error message and do not modify the task list.
